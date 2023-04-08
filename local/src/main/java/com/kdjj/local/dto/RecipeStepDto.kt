package com.kdjj.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.kdjj.domain.model.RecipeStepType

@Entity(
    tableName = "RecipeStep",
    foreignKeys = [
        ForeignKey(
            entity = RecipeMetaDto::class,
            parentColumns = arrayOf("recipeMetaId", "isTemp"),
            childColumns = arrayOf("parentRecipeId", "isTemp"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["stepId", "isTemp"],
    indices = [Index("parentRecipeId", "isTemp")]
)
internal data class RecipeStepDto(
    val stepId: String,
    val name: String,
    val order: Int,
    val type: RecipeStepType,
    val description: String,
    val imgPath: String?,
    val seconds: Int,
    val parentRecipeId: String,
    @ColumnInfo(defaultValue = "0")
    val isTemp: Boolean
)
