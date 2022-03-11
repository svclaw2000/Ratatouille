package com.kdjj.local.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kdjj.domain.model.RecipeStepType

@Entity(
    tableName = "RecipeStep",
    foreignKeys = [
        ForeignKey(
            entity = RecipeMetaDto::class,
            parentColumns = arrayOf("recipeMetaId"),
            childColumns = arrayOf("parentRecipeId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ImageDto::class,
            parentColumns = arrayOf("hash"),
            childColumns = arrayOf("imgHash"),
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
internal data class RecipeStepDto(
    @PrimaryKey
    val stepId: String,
    val name: String,
    val order: Int,
    val type: RecipeStepType,
    val description: String,
    val imgHash: String?,
    val seconds: Int,
    val parentRecipeId: String,
)
