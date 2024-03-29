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
        val imgPathList = listOf(recipe.imgPath) + recipe.stepList.map { step -> step.imgPath }

        val notNullImgPathList = imgPathList.filterNotNull()
        val imgValidationList = imageRepository.checkImagesAreValid(notNullImgPathList)
            .getOrElse { List(notNullImgPathList.size) { false } }

        val invalidImgInfoList = notNullImgPathList.zip(imgValidationList)
            .mapNotNull { (path, isValid) ->
                if (isValid) null else ImageInfo(path, idGenerator.generateId())
            }

        return copyImageToInternal(invalidImgInfoList).flatMap { copiedImgPathList ->
            val totalImgList = imgPathList.map { path ->
                if (path != null) {
                    val index = notNullImgPathList.indexOfFirst { it == path }
                    if (imgValidationList[index]) {
                        notNullImgPathList[index]
                    } else {
                        val invalidImgIndex = invalidImgInfoList.indexOfFirst { it.uri == path }
                        copiedImgPathList[invalidImgIndex]
                    }
                } else {
                    null
                }
            }

            val stepList = recipe.stepList.mapIndexed { idx, step ->
                step.copy(
                    stepId = if (step.stepId.isBlank() || recipe.state == RecipeState.NETWORK) idGenerator.generateId() else step.stepId,
                    imgPath = totalImgList[idx + 1]
                )
            }

            when (recipe.state) {
                RecipeState.CREATE -> recipeRepository.saveMyRecipe(
                    recipe.copy(
                        recipeId = idGenerator.generateId(),
                        imgPath = totalImgList.first(),
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
                                    imgPath = totalImgList.first(),
                                    stepList = stepList,
                                    createTime = System.currentTimeMillis()
                                ),
                                originRecipe.stepList
                                    .map { it.imgPath }
                                    .plus(originRecipe.imgPath)
                                    .filterNotNull()
                            )
                        }
                }
                RecipeState.NETWORK -> recipeRepository.saveMyRecipe(
                    recipe.copy(
                        recipeId = idGenerator.generateId(),
                        imgPath = totalImgList.first(),
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
            imgInfoList.first().uri.startsWith("https://") || imgInfoList.first().uri.startsWith("gs://") ->
                imageRepository.copyRemoteImageToInternal(imgInfoList)
            else ->
                imageRepository.copyExternalImageToInternal(imgInfoList)
        }
}