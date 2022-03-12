package com.kdjj.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.kdjj.local.dto.RecipeDto

@Dao
internal interface RecipeListDao {

    @Transaction
    @Query("SELECT * FROM RecipeMeta WHERE isTemp = 0 ORDER BY createTime DESC LIMIT :pageSize OFFSET :index")
    suspend fun fetchLatestRecipeList(pageSize: Int, index: Int): List<RecipeDto>

    @Transaction
    @Query("SELECT * FROM RecipeMeta WHERE isFavorite = :favorite AND isTemp = 0 ORDER BY createTime DESC LIMIT :pageSize OFFSET :index")
    suspend fun fetchFavoriteRecipeList(pageSize: Int, index: Int, favorite:Boolean = true): List<RecipeDto>

    @Transaction
    @Query("SELECT * FROM RecipeMeta WHERE title LIKE :keyword AND isTemp = 0 LIMIT :pageSize OFFSET :index")
    suspend fun fetchSearchRecipeList(pageSize: Int, keyword: String, index: Int): List<RecipeDto>

    @Transaction
    @Query("SELECT * FROM RecipeMeta WHERE isTemp = 0 ORDER BY title LIMIT :pageSize OFFSET :index")
    suspend fun fetchTitleRecipeList(pageSize: Int, index: Int): List<RecipeDto>
}
