package com.kdjj.local.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kdjj.domain.model.RecipeState

@Entity(
    tableName = "RecipeMeta",
    foreignKeys = [
        ForeignKey(
            entity = RecipeTypeDto::class,
            parentColumns = arrayOf("recipeTypeId"),
            childColumns = arrayOf("recipeTypeId"),
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
internal data class RecipeMetaDto(
    @PrimaryKey
    val recipeMetaId: String,
    val title: String,
    val stuff: String,
    val imgPath: String?,
    val authorId: String,
    val isFavorite: Boolean,
    val createTime: Long,
    val state: RecipeState,
    val recipeTypeId: Long
)
