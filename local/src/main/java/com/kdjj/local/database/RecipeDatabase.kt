package com.kdjj.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kdjj.local.dao.RecipeDao
import com.kdjj.local.dao.RecipeImageDao
import com.kdjj.local.dao.RecipeListDao
import com.kdjj.local.dao.RecipeTypeDao
import com.kdjj.local.dto.RecipeImageViewDto
import com.kdjj.local.dto.RecipeMetaDto
import com.kdjj.local.dto.RecipeStepDto
import com.kdjj.local.dto.RecipeTypeDto
import com.kdjj.local.dto.UselessImageDto

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
    version = 6,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
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
