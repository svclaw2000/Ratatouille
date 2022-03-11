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
        val imgHashList = listOf(recipe.imgHash) + recipe.stepList.map { it.imgHash }

        val imgInfoList = imgHashList.filterNotNull()
            .map { hash ->
                ImageInfo(hash, idGenerator.generateId())
            }

        return copyImageToInternal(imgInfoList).flatMap { copiedImgHashList ->
            var i = 0
            val totalImgList = imgHashList.map { path ->
                path?.let { copiedImgHashList[i++] }
            }

            val stepList = recipe.stepList.mapIndexed { idx, step ->
                if (step.stepId.isEmpty()) step.copy(
                    stepId = idGenerator.generateId(),
                    imgHash = totalImgList[idx + 1]
                )
                else step.copy(
                    imgHash = totalImgList[idx + 1]
                )
            }

            tempRepository.saveRecipeTemp(
                recipe.copy(
                    imgHash = totalImgList.first(),
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