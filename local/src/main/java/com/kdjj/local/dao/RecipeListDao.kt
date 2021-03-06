package com.kdjj.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.kdjj.local.dto.RecipeDto

@Dao
internal interface RecipeListDao {

    @Transaction
    @Query("SELECT * FROM RecipeMeta ORDER BY createTime DESC LIMIT :pageSize OFFSET :index")
    suspend fun fetchLatestRecipeList(pageSize: Int, index: Int): List<RecipeDto>

    @Transaction
    @Query("SELECT * FROM RecipeMeta WHERE isFavorite = :favorite ORDER BY createTime DESC LIMIT :pageSize OFFSET :index")
    suspend fun fetchFavoriteRecipeList(pageSize: Int, index: Int, favorite:Boolean = true): List<RecipeDto>

    @Transaction
    @Query("SELECT * FROM RecipeMeta WHERE title LIKE :keyword LIMIT :pageSize OFFSET :index")
    suspend fun fetchSearchRecipeList(pageSize: Int, keyword: String, index: Int): List<RecipeDto>

    @Transaction
    @Query("SELECT * FROM RecipeMeta ORDER BY title LIMIT :pageSize OFFSET :index")
    suspend fun fetchTitleRecipeList(pageSize: Int, index: Int): List<RecipeDto>
}
