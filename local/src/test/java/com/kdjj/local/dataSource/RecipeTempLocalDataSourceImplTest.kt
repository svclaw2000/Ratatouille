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
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RecipeTempLocalDataSourceImplTest {

    private lateinit var mockRecipeDatabase: RecipeDatabase
    private lateinit var mockRecipeTempDao: RecipeDao
    private lateinit var mockUselessImageDao: RecipeImageDao
    private lateinit var recipeTempLocalDataSourceImpl: RecipeTempLocalDataSourceImpl

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

    private val dummyRecipeTypeDto = RecipeTypeDto(1L, "한식")

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
        isTemp = true,
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
        isTemp = true,
    )

    private val dummyRecipeTempDto = RecipeDto(
        dummyRecipeMeta,
        dummyRecipeTypeDto,
        listOf(dummyRecipeStepDto)
    )


    private val dummyId = "testId"

    @Before
    fun setup() {
        mockRecipeDatabase = mock(RecipeDatabase::class.java)
        mockRecipeTempDao = mock(RecipeDao::class.java)
        mockUselessImageDao = mock(RecipeImageDao::class.java)
        recipeTempLocalDataSourceImpl = RecipeTempLocalDataSourceImpl(
            mockRecipeTempDao,
            mockRecipeDatabase,
            mockUselessImageDao
        )
    }

    @Test
    fun getRecipeTemp_getRecipe_true(): Unit = runBlocking {
        //given
        `when`(mockRecipeTempDao.getRecipeDto(dummyId, isTemp = true)).thenReturn(dummyRecipeTempDto)
        //when
        val testResult = recipeTempLocalDataSourceImpl.getRecipeTemp(dummyId)
        //then
        assertEquals(dummyRecipeTempDto.toDomain(), testResult.getOrNull())
    }
}