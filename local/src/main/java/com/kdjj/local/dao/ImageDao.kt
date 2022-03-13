package com.kdjj.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kdjj.local.dto.ImageDto

@Dao
internal interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertImage(imageDto: ImageDto)

    @Query("""
        SELECT * FROM Image I WHERE NOT EXISTS (
            SELECT imgHash FROM (
                SELECT imgHash FROM RecipeMeta UNION 
                SELECT imgHash FROM RecipeStep UNION 
                SELECT imgHash FROM RecipeTempMeta UNION 
                SELECT imgHash FROM RecipeTempStep
            ) U WHERE I.hash == U.imgHash
        );
    """)
    suspend fun getUnusedImages(): List<ImageDto>

    @Query("DELETE FROM Image WHERE hash in (:hashList)")
    suspend fun deleteImages(hashList: List<ImageDto>)
}