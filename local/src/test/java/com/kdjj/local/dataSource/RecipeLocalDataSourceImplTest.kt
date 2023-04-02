package com.kdjj.local.dataSource

import com.kdjj.domain.model.*
import com.kdjj.local.dao.RecipeDao
import com.kdjj.local.dao.RecipeImageDao
import com.kdjj.local.database.RecipeDatabase
import com.kdjj.local.dto.RecipeDto
import com.kdjj.local.dto.RecipeMetaDto
import com.kdjj.local.dto.RecipeStepDto
import com.kdjj.local.dto.RecipeTypeDto
import com.kdjj.local.mapper.toDomain
import com.kdjj.local.mapper.toDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class RecipeLocalDataSourceImplTest {

    private lateinit var mockRecipeDatabase: RecipeDatabase
    private lateinit var mockRecipeDao: RecipeDao
    private lateinit var mockUselessImageDao: RecipeImageDao
    private lateinit var recipeLocalDataSourceImpl: RecipeLocalDataSourceImpl

    private val dummyRecipeStepList = listOf(
        RecipeStep(
            "stepId",
            "굽기",
            RecipeStepType.COOK,
            "description",
            "image path",
            1000,
        )
    )

    private val dummyRecipe = Recipe(
        "recipeId",
        "고기",
        RecipeType(1, "한식"),
        "stuff",
        "image path",
        dummyRecipeStepList,
        "authrId",
        100,
        false,
        1000,
        RecipeState.CREATE
    )

    private val dummyRecipeMeta = RecipeMetaDto(
        recipeMetaId = "recipeId",
        title = "두둥탁! 맛있는 감자탕!!",
        stuff = "stuff",
        imgPath = "image path",
        authorId = "authorId",
        isFavorite = false,
        createTime = 1000,
        state = RecipeState.CREATE,
        recipeTypeId = 1L,
        isTemp = false,
    )

    private val dummyRecipeStepDto = RecipeStepDto(
        stepId = "stepId",
        name = "삶기",
        order = 1,
        type = RecipeStepType.COOK,
        description = "description",
        imgPath = "image path",
        seconds = 1000,
        parentRecipeId = "recipeId",
        isTemp = false,
    )

    private val dummyRecipeTypeDto = RecipeTypeDto(1L, "한식")

    private val dummyRecipeDto = RecipeDto(
        dummyRecipeMeta,
        dummyRecipeTypeDto,
        listOf(dummyRecipeStepDto)
    )

    private val dummyRecipeId = "recipeId"

    @Before
    fun setup() {
        mockRecipeDatabase = mock(RecipeDatabase::class.java)
        mockRecipeDao = mock(RecipeDao::class.java)
        mockUselessImageDao = mock(RecipeImageDao::class.java)
        recipeLocalDataSourceImpl =
            RecipeLocalDataSourceImpl(mockRecipeDatabase, mockRecipeDao, mockUselessImageDao)
    }

    @Test
    fun updateFavoriteRecipe_successfullyFavoriteUpdated_true(): Unit = runBlocking {
        //given
        recipeLocalDataSourceImpl.updateRecipe(dummyRecipe)
        //then
        verify(mockRecipeDao, times(1)).updateRecipeMeta(dummyRecipe.toDto(isTemp = false))
    }

    @Test
    fun getRecipe_getRecipeByRecipeId_true(): Unit = runBlocking {
        //when
        `when`(mockRecipeDao.getRecipeDto(dummyRecipeId, isTemp = false)).thenReturn(dummyRecipeDto)
        //given
        val testResult = recipeLocalDataSourceImpl.getRecipe(dummyRecipeId)
        //then
        assertEquals(dummyRecipeDto.toDomain(), testResult.getOrNull())
    }
}