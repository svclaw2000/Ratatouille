package com.kdjj.local.dao

import androidx.room.*
import com.kdjj.local.dto.RecipeImageViewDto
import com.kdjj.local.dto.UselessImageDto

@Dao
internal interface RecipeImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUselessImage(uselessImageDto: UselessImageDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUselessImage(uselessImageDto: List<UselessImageDto>)

    @Query("SELECT * FROM UselessImage")
    suspend fun getAllUselessImage(): List<UselessImageDto>

    @Delete
    suspend fun deleteUselessImage(uselessImageDto: UselessImageDto)

    @Query("delete from UselessImage where imgPath in (:idList)")
    suspend fun deleteUselessImage(idList: List<String>)

    @Query("SELECT * FROM RecipeImageViewDto")
    suspend fun getAllValidImage(): List<RecipeImageViewDto>
}