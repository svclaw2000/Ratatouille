package com.kdjj.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kdjj.local.dao.RecipeDao
import com.kdjj.local.dao.RecipeListDao
import com.kdjj.local.dao.RecipeTypeDao
import com.kdjj.local.dao.UselessImageDao
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
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
internal abstract class RecipeDatabase : RoomDatabase() {

    internal abstract fun getRecipeDao(): RecipeDao
    internal abstract fun getRecipeListDao(): RecipeListDao
    internal abstract fun getRecipeTypeDao(): RecipeTypeDao
    internal abstract fun getUselessImageDao(): UselessImageDao

    companion object {

        const val RECIPE_DATABASE_NAME = "RecipeDatabase.db"
    }
}
