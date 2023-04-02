package com.kdjj.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
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
    ],
    primaryKeys = ["recipeMetaId", "isTemp"]
)
internal data class RecipeMetaDto(
    val recipeMetaId: String,
    val title: String,
    val stuff: String,
    val imgPath: String?,
    val authorId: String,
    val isFavorite: Boolean,
    val createTime: Long,
    val state: RecipeState,
    val recipeTypeId: Long,
    @ColumnInfo(defaultValue = "0")
    val isTemp: Boolean
)
