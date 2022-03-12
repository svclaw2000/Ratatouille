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
        }).addMigrations(migration_2_3).build()
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
