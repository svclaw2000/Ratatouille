package com.kdjj.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kdjj.local.dao.RecipeDao
import com.kdjj.local.dao.RecipeImageDao
import com.kdjj.local.dao.RecipeListDao
import com.kdjj.local.dao.RecipeTypeDao
import com.kdjj.local.dto.*

@Database(
    entities = [
        RecipeMetaDto::class,
        RecipeTypeDto::class,
        RecipeStepDto::class,
        UselessImageDto::class
    ],
    views = [
        RecipeImageViewDto::class
    ],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 4, to = 5)
    ]
)
internal abstract class RecipeDatabase : RoomDatabase() {

    internal abstract fun getRecipeDao(): RecipeDao
    internal abstract fun getRecipeListDao(): RecipeListDao
    internal abstract fun getRecipeTypeDao(): RecipeTypeDao
    internal abstract fun getUselessImageDao(): RecipeImageDao

    companion object {

        const val RECIPE_DATABASE_NAME = "RecipeDatabase.db"
    }
}
