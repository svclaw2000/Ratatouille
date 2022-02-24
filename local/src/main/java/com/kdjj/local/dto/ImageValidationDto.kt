package com.kdjj.local.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UselessImage")
internal data class UselessImageDto(
    @PrimaryKey
    val imgPath: String,
)