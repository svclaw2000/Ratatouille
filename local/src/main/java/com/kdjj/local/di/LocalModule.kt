package com.kdjj.local.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kdjj.local.database.RecipeDatabase
import com.kdjj.local.dto.RecipeTypeDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalModule {

    private val migration_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""UPDATE RecipeMeta SET imgPath=NULL WHERE imgPath == "";""")
            database.execSQL("""UPDATE RecipeStep SET imgPath=NULL WHERE imgPath == "";""")
            database.execSQL("""UPDATE RecipeTempMeta SET imgPath=NULL WHERE imgPath == "";""")
            database.execSQL("""UPDATE RecipeTempStep SET imgPath=NULL WHERE imgPath == "";""")
        }
    }

    private val migration_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 테이블 변경
            database.execSQL("CREATE TABLE IF NOT EXISTS `_new_RecipeMeta` (`recipeMetaId` TEXT NOT NULL, `title` TEXT NOT NULL, `stuff` TEXT NOT NULL, `imgPath` TEXT, `authorId` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `createTime` INTEGER NOT NULL, `state` TEXT NOT NULL, `recipeTypeId` INTEGER NOT NULL, `isTemp` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`recipeMetaId`, `isTemp`), FOREIGN KEY(`recipeTypeId`) REFERENCES `RecipeType`(`recipeTypeId`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
            database.execSQL("INSERT INTO `_new_RecipeMeta` (`createTime`,`imgPath`,`state`,`recipeMetaId`,`title`,`authorId`,`stuff`,`isFavorite`,`recipeTypeId`) SELECT `createTime`,`imgPath`,`state`,`recipeMetaId`,`title`,`authorId`,`stuff`,`isFavorite`,`recipeTypeId` FROM `RecipeMeta`")
            database.execSQL("DROP TABLE `RecipeMeta`")
            database.execSQL("ALTER TABLE `_new_RecipeMeta` RENAME TO `RecipeMeta`")
            database.execSQL("CREATE TABLE IF NOT EXISTS `_new_RecipeStep` (`stepId` TEXT NOT NULL, `name` TEXT NOT NULL, `order` INTEGER NOT NULL, `type` TEXT NOT NULL, `description` TEXT NOT NULL, `imgPath` TEXT, `seconds` INTEGER NOT NULL, `parentRecipeId` TEXT NOT NULL, `isTemp` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`stepId`, `isTemp`), FOREIGN KEY(`parentRecipeId`, `isTemp`) REFERENCES `RecipeMeta`(`recipeMetaId`, `isTemp`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            database.execSQL("INSERT INTO `_new_RecipeStep` (`seconds`,`parentRecipeId`,`imgPath`,`stepId`,`name`,`description`,`type`,`order`) SELECT `seconds`,`parentRecipeId`,`imgPath`,`stepId`,`name`,`description`,`type`,`order` FROM `RecipeStep`")
            database.execSQL("DROP TABLE `RecipeStep`")
            database.execSQL("ALTER TABLE `_new_RecipeStep` RENAME TO `RecipeStep`")

            database.execSQL("INSERT INTO `RecipeMeta` (`createTime`,`imgPath`,`state`,`recipeMetaId`,`title`,`authorId`,`stuff`,`isFavorite`,`recipeTypeId`, `isTemp`) SELECT `createTime`,`imgPath`,`state`,`recipeMetaId`,`title`,`authorId`,`stuff`,`isFavorite`,`recipeTypeId`, 1 FROM `RecipeTempMeta`")
            database.execSQL("INSERT INTO `RecipeStep` (`seconds`,`parentRecipeId`,`imgPath`,`stepId`,`name`,`description`,`type`,`order`, `isTemp`) SELECT `seconds`,`parentRecipeId`,`imgPath`,`stepId`,`name`,`description`,`type`,`order`, 1 FROM `RecipeTempStep`")

            database.execSQL("DROP TABLE `RecipeTempStep`")
            database.execSQL("DROP TABLE `RecipeTempMeta`")
        }
    }

    @Provides
    @Singleton
    internal fun provideRecipeDataBase(
        @ApplicationContext context: Context
    ): RecipeDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RecipeDatabase::class.java,
            RecipeDatabase.RECIPE_DATABASE_NAME
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    RecipeTypeDto.defaultTypes.forEach { recipeType ->
                        db.execSQL("INSERT INTO RecipeType VALUES (${recipeType.recipeTypeId}, '${recipeType.title}');")
                    }
                }
            }
        }).addMigrations(
            migration_2_3,
            migration_3_4
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideFileDir(
        @ApplicationContext context: Context
    ): File =
        context.filesDir
    
    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver =
        context.contentResolver
}
