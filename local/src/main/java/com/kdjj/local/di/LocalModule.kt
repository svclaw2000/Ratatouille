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

    private val migration_1_2 = object : Migration(1, 2) {

        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS Image (
                    hash TEXT NOT NULL PRIMARY KEY
                );
            """.trimIndent())

            database.execSQL("""
                CREATE TABLE RecipeMeta2 (
                    recipeMetaId TEXT    NOT NULL PRIMARY KEY,
                    title        TEXT    NOT NULL,
                    stuff        TEXT    NOT NULL,
                    imgHash      TEXT    REFERENCES Image(hash) ON DELETE RESTRICT,
                    authorId     TEXT    NOT NULL,
                    isFavorite   INTEGER NOT NULL,
                    createTime   INTEGER NOT NULL,
                    state        TEXT    NOT NULL,
                    recipeTypeId INTEGER NOT NULL REFERENCES RecipeType(recipeTypeId) ON DELETE RESTRICT
                );
            """.trimIndent())

            database.execSQL("""
                CREATE TABLE RecipeStep2 (
                    stepId         TEXT    NOT NULL PRIMARY KEY,
                    name           TEXT    NOT NULL,
                    "order"        INTEGER NOT NULL,
                    type           TEXT    NOT NULL,
                    description    TEXT    NOT NULL,
                    imgHash        TEXT    REFERENCES Image(hash) ON DELETE RESTRICT,
                    seconds        INTEGER NOT NULL,
                    parentRecipeId TEXT    NOT NULL REFERENCES RecipeMeta(recipeMetaId) ON DELETE CASCADE
                );
            """.trimIndent())

            database.execSQL("""
                CREATE TABLE RecipeTempMeta2 (
                    recipeMetaId TEXT    NOT NULL PRIMARY KEY,
                    title        TEXT    NOT NULL,
                    stuff        TEXT    NOT NULL,
                    imgHash      TEXT    REFERENCES Image(hash) ON DELETE RESTRICT,
                    authorId     TEXT    NOT NULL,
                    isFavorite   INTEGER NOT NULL,
                    createTime   INTEGER NOT NULL,
                    state        TEXT    NOT NULL,
                    recipeTypeId INTEGER NOT NULL REFERENCES RecipeType(recipeTypeId) ON DELETE RESTRICT
                );
            """.trimIndent())

            database.execSQL("""
                CREATE TABLE RecipeTempStep2 (
                    stepId         TEXT    NOT NULL PRIMARY KEY,
                    name           TEXT    NOT NULL,
                    "order"        INTEGER NOT NULL,
                    type           TEXT    NOT NULL,
                    description    TEXT    NOT NULL,
                    imgHash        TEXT    REFERENCES Image(hash) ON DELETE RESTRICT,
                    seconds        INTEGER NOT NULL,
                    parentRecipeId TEXT    NOT NULL REFERENCES RecipeTempMeta(recipeMetaId) ON DELETE CASCADE
                );
            """.trimIndent())

            database.execSQL("INSERT INTO RecipeMeta2 SELECT * FROM RecipeMeta;")
            database.execSQL("INSERT INTO RecipeStep2 SELECT * FROM RecipeStep;")
            database.execSQL("INSERT INTO RecipeTempMeta2 SELECT * FROM RecipeTempMeta;")
            database.execSQL("INSERT INTO RecipeTempStep2 SELECT * FROM RecipeTempStep;")

            database.execSQL("UPDATE RecipeMeta2 SET imgHash=substr(imgHash, -36, 32);")
            database.execSQL("UPDATE RecipeStep2 SET imgHash=substr(imgHash, -36, 32);")
            database.execSQL("UPDATE RecipeTempMeta2 SET imgHash=substr(imgHash, -36, 32);")
            database.execSQL("UPDATE RecipeTempStep2 SET imgHash=substr(imgHash, -36, 32);")

            database.execSQL("""UPDATE RecipeMeta2 SET imgHash=NULL WHERE imgHash == "";""")
            database.execSQL("""UPDATE RecipeStep2 SET imgHash=NULL WHERE imgHash == "";""")
            database.execSQL("""UPDATE RecipeTempMeta2 SET imgHash=NULL WHERE imgHash == "";""")
            database.execSQL("""UPDATE RecipeTempStep2 SET imgHash=NULL WHERE imgHash == "";""")

            database.execSQL("INSERT INTO Image SELECT imgHash FROM RecipeMeta2 WHERE imgHash IS NOT NULL;")
            database.execSQL("INSERT INTO Image SELECT imgHash FROM RecipeStep2 WHERE imgHash IS NOT NULL;")
            database.execSQL("INSERT INTO Image SELECT imgHash FROM RecipeTempMeta2 WHERE imgHash IS NOT NULL;")
            database.execSQL("INSERT INTO Image SELECT imgHash FROM RecipeTempStep2 WHERE imgHash IS NOT NULL;")

            database.execSQL("DROP TABLE RecipeStep;")
            database.execSQL("DROP TABLE RecipeMeta;")
            database.execSQL("DROP TABLE RecipeTempMeta;")
            database.execSQL("DROP TABLE RecipeTempStep;")

            database.execSQL("ALTER TABLE RecipeMeta2 RENAME TO RecipeMeta;")
            database.execSQL("ALTER TABLE RecipeStep2 RENAME TO RecipeStep;")
            database.execSQL("ALTER TABLE RecipeTempMeta2 RENAME TO RecipeTempMeta;")
            database.execSQL("ALTER TABLE RecipeTempStep2 RENAME TO RecipeTempStep;")
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
        }).addMigrations(migration_1_2).build()
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
