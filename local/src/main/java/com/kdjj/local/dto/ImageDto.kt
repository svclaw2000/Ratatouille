package com.kdjj.local.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Image")
internal data class ImageDto(
    @PrimaryKey val hash: String
)