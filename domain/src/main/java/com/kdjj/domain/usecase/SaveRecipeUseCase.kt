package com.kdjj.domain.usecase

import com.kdjj.domain.common.AuthorIdProvider
import com.kdjj.domain.common.IdGenerator
import com.kdjj.domain.common.flatMap
import com.kdjj.domain.model.ImageInfo
import com.kdjj.domain.model.RecipeState
import com.kdjj.domain.model.request.SaveRecipeRequest
import com.kdjj.domain.repository.RecipeImageRepository
import com.kdjj.domain.repository.RecipeRepository
import javax.inject.Inject

internal class SaveRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val imageRepository: RecipeImageRepository,
    private val idGenerator: IdGenerator,
    private val authorIdProvider: AuthorIdProvider
) : ResultUseCase<SaveRecipeRequest, Unit> {

    override suspend fun invoke(request: SaveRecipeRequest): Result<Unit> {
        val recipe = request.recipe
        val imgHashList = listOf(recipe.imgHash) + recipe.stepList.map { step -> step.imgHash }

        val imgInfoList = imgHashList.filterNotNull()
            .map { hash ->
                ImageInfo(hash, idGenerator.generateId())
            }

        return copyImageToInternal(imgInfoList).flatMap { changedImgHashList ->
            var i = 0
            val totalImgList = imgHashList.map { path ->
                path?.let { changedImgHashList[i++] }
            }

            val stepList = recipe.stepList.mapIndexed { idx, step ->
                step.copy(
                    stepId = if (step.stepId.isBlank() || recipe.state == RecipeState.NETWORK) idGenerator.generateId() else step.stepId,
                    imgHash = totalImgList[idx + 1]
                )
            }

            when (recipe.state) {
                RecipeState.CREATE -> recipeRepository.saveMyRecipe(
                    recipe.copy(
                        recipeId = idGenerator.generateId(),
                        imgHash = totalImgList.first(),
                        stepList = stepList,
                        createTime = System.currentTimeMillis(),
                        state = RecipeState.LOCAL,
                        authorId = authorIdProvider.getAuthorId()
                    )
                )
                RecipeState.LOCAL, RecipeState.UPLOAD, RecipeState.DOWNLOAD -> {
                    recipeRepository.getLocalRecipe(request.recipe.recipeId)
                        .flatMap { originRecipe ->
                            recipeRepository.updateMyRecipe(
                                recipe.copy(
                                    imgHash = totalImgList.first(),
                                    stepList = stepList,
                                    createTime = System.currentTimeMillis()
                                ),
                                originRecipe.stepList
                                    .map { it.imgHash }
                                    .plus(originRecipe.imgHash)
                                    .filterNotNull()
                            )
                        }
                }
                RecipeState.NETWORK -> recipeRepository.saveMyRecipe(
                    recipe.copy(
                        recipeId = idGenerator.generateId(),
                        imgHash = totalImgList.first(),
                        stepList = stepList,
                        createTime = System.currentTimeMillis(),
                        state = RecipeState.DOWNLOAD
                    )
                )
            }
        }
    }

    private suspend fun copyImageToInternal(imgInfoList: List<ImageInfo>): Result<List<String>> =
        when {
            imgInfoList.isEmpty() ->
                Result.success(listOf())
            imgInfoList.first().hash.startsWith("https://") || imgInfoList.first().hash.startsWith("gs://") ->
                imageRepository.copyRemoteImageToInternal(imgInfoList)
            else ->
                imageRepository.copyExternalImageToInternal(imgInfoList)
        }
}