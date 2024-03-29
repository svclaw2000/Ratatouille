package com.kdjj.remote.datasource

import com.kdjj.data.datasource.RecipeRemoteDataSource
import com.kdjj.domain.model.*
import com.kdjj.remote.mapper.toDto
import com.kdjj.remote.service.RemoteRecipeService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RecipeRemoteDataSourceImplTest {

    private lateinit var mockRecipeService: RemoteRecipeService
    private lateinit var mockRecipeRemoteDataSource: RecipeRemoteDataSource
    private val dummyRecipeId = "dummyRecipeId1"
    private val dummyRecipeType = RecipeType(1, "DummyRecipeType")
    private val dummyRecipeStep1 = RecipeStep(
        stepId = "dummyStepId1",
        name = "dummyStep1",
        type = RecipeStepType.values().first(),
        description = "dummyStepDescription",
        imgPath = null,
        seconds = 0
    )
    private val dummyRecipe = Recipe(
        recipeId = dummyRecipeId,
        title = "dummyRecipeTitle1",
        type = dummyRecipeType,
        stuff = "",
        imgPath = null,
        stepList = listOf(dummyRecipeStep1),
        authorId = "",
        viewCount = 0,
        isFavorite = false,
        createTime = 0,
        state = RecipeState.NETWORK
    )

    @Before
    fun setup() {
        // given
        mockRecipeService = mock(RemoteRecipeService::class.java)
        mockRecipeRemoteDataSource = RecipeRemoteDataSourceImpl(mockRecipeService)
    }

    @Test
    fun uploadRecipe_giveRecipe_success(): Unit = runBlocking {
        // given
        `when`(mockRecipeService.uploadRecipe(dummyRecipe.toDto())).thenReturn(Unit)

        // when
        val uploadRecipeResult = mockRecipeRemoteDataSource.uploadRecipe(dummyRecipe)

        // then
        assertTrue(uploadRecipeResult.isSuccess)
    }

    @Test
    fun increaseViewCount_giveRecipe_success(): Unit = runBlocking {
        // given
        `when`(mockRecipeService.uploadRecipe(dummyRecipe.toDto())).thenReturn(Unit)

        // when
        val increaseViewCountResult = mockRecipeRemoteDataSource.increaseViewCount(dummyRecipe)

        // then
        assertTrue(increaseViewCountResult.isSuccess)
    }

    @Test
    fun deleteRecipe_giveRecipe_success(): Unit = runBlocking {
        // given
        `when`(mockRecipeService.deleteRecipe(dummyRecipe.toDto())).thenReturn(Unit)

        // when
        val deleteRecipeResult = mockRecipeRemoteDataSource.deleteRecipe(dummyRecipe)

        // then
        assertTrue(deleteRecipeResult.isSuccess)
    }

    @Test
    fun fetchRecipe_giveRecipeId_successAndRecipe(): Unit = runBlocking {
        // given
        `when`(mockRecipeService.fetchRecipe(dummyRecipeId)).thenReturn(dummyRecipe.toDto())

        // when
        val fetchRecipeResult = mockRecipeRemoteDataSource.fetchRecipe(dummyRecipeId)

        // then
        assertTrue(fetchRecipeResult.isSuccess)
        assertEquals(dummyRecipe, fetchRecipeResult.getOrNull())
    }
}
