package com.kdjj.domain.usecase

import com.kdjj.domain.model.Recipe
import com.kdjj.domain.model.request.FetchRecipeTempRequest
import com.kdjj.domain.repository.RecipeImageRepository
import com.kdjj.domain.repository.RecipeTempRepository
import javax.inject.Inject

internal class FetchRecipeTempUseCase @Inject constructor(
    private val tempRepository: RecipeTempRepository,
    private val imageRepository: RecipeImageRepository
) : ResultUseCase<FetchRecipeTempRequest, Recipe?> {

    override suspend fun invoke(request: FetchRecipeTempRequest): Result<Recipe?> {
        return tempRepository.getRecipeTemp(request.recipeId).map { recipe ->
            recipe?.let {
                val imgHash = if (
                    recipe.imgHash != null &&
                    imageRepository.isUriExists(recipe.imgHash)
                ) recipe.imgHash else null

                val stepList = recipe.stepList.map { step ->
                    if (step.imgHash != null && imageRepository.isUriExists(step.imgHash)) step
                    else step.copy(imgHash = null)
                }
                recipe.copy(imgHash = imgHash, stepList = stepList)
            }
        }
    }
}