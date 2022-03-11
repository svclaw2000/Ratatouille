package com.kdjj.domain.usecase

import com.kdjj.domain.common.flatMap
import com.kdjj.domain.model.RecipeState
import com.kdjj.domain.model.request.UploadRecipeRequest
import com.kdjj.domain.repository.RecipeImageRepository
import com.kdjj.domain.repository.RecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class UploadRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val recipeImageRepository: RecipeImageRepository
) : ResultUseCase<UploadRecipeRequest, Unit> {

    override suspend fun invoke(request: UploadRecipeRequest): Result<Unit> {
        val recipe = request.recipe
        return (listOf(recipe.imgHash) + recipe.stepList.map { it.imgHash }).map {
            coroutineScope {
                async {
                    convertImageToRemote(it)
                }
            }
        }.fold(Result.success(listOf<String?>())) { acc, deferred ->
            acc.flatMap { imgList ->
                deferred.await().flatMap { imgHash ->
                    Result.success(imgList + imgHash)
                }
            }
        }.flatMap { imgList ->
            val stepList = recipe.stepList.mapIndexed { idx, step ->
                step.copy(
                    imgHash = imgList[idx + 1]
                )
            }
            recipeRepository.uploadRecipe(
                recipe.copy(
                    imgHash = imgList.first(),
                    stepList = stepList,
                    createTime = System.currentTimeMillis()
                )
            ).onSuccess {
                recipeRepository.updateMyRecipe(
                    recipe.copy(
                        state = RecipeState.UPLOAD
                    )
                )
            }
        }
    }

    private suspend fun convertImageToRemote(imgHash: String?): Result<String?> {
        return recipeImageRepository.convertInternalUriToRemoteStorageUri(
            imgHash ?: return Result.success(null)
        )
    }
}
