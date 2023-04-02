package com.kdjj.local.dataSource

import com.kdjj.data.datasource.RecipeImageLocalDataSource
import com.kdjj.local.ImageFileHelper
import com.kdjj.local.dao.RecipeImageDao
import com.kdjj.local.dto.UselessImageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RecipeImageLocalDataSourceImpl @Inject constructor(
    private val imageFileHelper: ImageFileHelper,
    private val recipeImageDao: RecipeImageDao
) : RecipeImageLocalDataSource {

    override suspend fun convertToByteArray(
        uriList: List<String>
    ): Result<List<Pair<ByteArray, Float?>>> {
        return imageFileHelper.convertToByteArray(uriList)
    }

    override suspend fun convertToInternalStorageUri(
        byteArrayList: List<ByteArray>,
        fileNameList: List<String>,
        degreeList: List<Float?>
    ): Result<List<String>> {
        return imageFileHelper.convertToInternalStorageUri(byteArrayList, fileNameList, degreeList)
    }

    override fun isUriExists(
        uri: String
    ): Boolean = imageFileHelper.isUriExists(uri)

    override suspend fun deleteUselessImages(): Result<Unit> =
        runCatching {
            recipeImageDao.getAllUselessImage()
                .forEach {
                    imageFileHelper.deleteImageFile(it.imgPath)
                    recipeImageDao.deleteUselessImage(UselessImageDto(it.imgPath))
                }
        }

    override suspend fun checkImagesAreValid(uris: List<String>): Result<List<Boolean>> {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val allImageDtoList = recipeImageDao.getAllValidImage()
                uris.map { uri ->
                    allImageDtoList.any { it.imgPath == uri }
                }
            }
        }
    }
}

