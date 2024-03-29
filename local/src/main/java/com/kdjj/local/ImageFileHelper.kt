package com.kdjj.local

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import com.kdjj.local.dao.RecipeImageDao
import com.kdjj.local.dto.UselessImageDto
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class ImageFileHelper @Inject constructor(
    private val fileDir: File,
    private val contentResolver: ContentResolver,
    private val recipeImageDao: RecipeImageDao
) {

    suspend fun convertToByteArray(
        uriList: List<String>
    ): Result<List<Pair<ByteArray, Float?>>> = withContext(Dispatchers.IO) {
        runCatching {
            uriList.map { uri ->
                async {
                    val changedUri = if (!uri.contains("://")) "file://${uri}" else uri
                    val inputStream = contentResolver.openInputStream(Uri.parse(changedUri))
                    val byteArray = inputStream?.readBytes() ?: throw Exception()

                    val exifInputStream = contentResolver.openInputStream(Uri.parse(changedUri))
                    val oldExif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ExifInterface(exifInputStream ?: throw Exception())
                    } else {
                        ExifInterface(changedUri)
                    }
                    val degree = when (oldExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> null
                    }
                    byteArray to degree
                }
            }.map { it.await() }
        }
    }

    suspend fun convertToInternalStorageUri(
        byteArrayList: List<ByteArray>,
        fileNameList: List<String>,
        degreeList: List<Float?>
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            val size = byteArrayList.size
            val resList = mutableListOf<Deferred<String>>()
            for (i in 0 until size) {
                resList.add(
                    async {
                        var fos: FileOutputStream? = null
                        val filePath = "$fileDir/${fileNameList[i]}.png"
                        recipeImageDao.insertUselessImage(UselessImageDto(filePath))
                        fos = FileOutputStream(filePath)
                        val bitmap = convertByteArrayToBitmap(byteArrayList[i], degreeList[i])
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        filePath
                    }
                )
            }
            resList.map { it.await() }
        }
    }

    fun isUriExists(uri: String): Boolean =
        try {
            val changedUri = if (!uri.contains("://")) "file://${uri}" else uri
            contentResolver.openInputStream(Uri.parse(changedUri))?.close()
            true
        } catch (e: Exception) {
            false
        }

    private fun convertByteArrayToBitmap(byteArray: ByteArray, degree: Float?): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val (scaledWidth, scaledHeight) = if (width > MAX_WIDTH_HEIGHT_SIZE || height > MAX_WIDTH_HEIGHT_SIZE) {
            if (width > height) {
                MAX_WIDTH_HEIGHT_SIZE to (MAX_WIDTH_HEIGHT_SIZE * height / width)
            } else {
                (MAX_WIDTH_HEIGHT_SIZE * width / height) to MAX_WIDTH_HEIGHT_SIZE
            }
        } else {
            width to height
        }
        return Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt(), scaledHeight.toInt(), true).let { scaledBitmap ->
            degree?.let {
                val matrix = Matrix().apply {
                    postRotate(degree)
                }
                Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
            } ?: scaledBitmap
        }
    }

    fun deleteImageFile(imgPath: String) {
        val file = File(imgPath)
        if (file.exists()) file.delete()
    }

    companion object{
        const val MAX_WIDTH_HEIGHT_SIZE = 300
    }
}
