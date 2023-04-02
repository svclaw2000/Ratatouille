package com.kdjj.domain.usecase

import com.kdjj.domain.common.IdGenerator
import com.kdjj.domain.common.flatMap
import com.kdjj.domain.model.ImageInfo
import com.kdjj.domain.model.request.SaveRecipeTempRequest
import com.kdjj.domain.repository.RecipeImageRepository
import com.kdjj.domain.repository.RecipeTempRepository
import javax.inject.Inject

internal class SaveRecipeTempUseCase @Inject constructor(
    private val tempRepository: RecipeTempRepository,
    private val imageRepository: RecipeImageRepository,
    private val idGenerator: IdGenerator
) : ResultUseCase<SaveRecipeTempRequest, Unit> {

    override suspend fun invoke(request: SaveRecipeTempRequest): Result<Unit> {
        val recipe = request.recipe
        val imgPathList = listOf(recipe.imgPath) + recipe.stepList.map { it.imgPath }

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
                if (step.stepId.isEmpty()) step.copy(
                    stepId = idGenerator.generateId(),
                    imgPath = totalImgList[idx + 1]
                )
                else step.copy(
                    imgPath = totalImgList[idx + 1]
                )
            }

            tempRepository.saveRecipeTemp(
                recipe.copy(
                    imgPath = totalImgList.first(),
                    stepList = stepList
                )
            )
        }
    }

    private suspend fun copyImageToInternal(imgInfoList: List<ImageInfo>): Result<List<String>> {
        return if (imgInfoList.isEmpty()) Result.success(listOf())
        else imageRepository.copyExternalImageToInternal(imgInfoList)
    }
}