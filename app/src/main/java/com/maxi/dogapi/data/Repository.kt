package com.maxi.dogapi.data

import android.graphics.Bitmap
import com.maxi.dogapi.data.remote.RemoteDataSource
import com.maxi.dogapi.model.BaseApiResponse
import com.maxi.dogapi.model.DogResponse
import com.maxi.dogapi.utils.NetworkResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


@ActivityRetainedScoped
class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun getDog(): Flow<NetworkResult<DogResponse>> {
        return flow<NetworkResult<DogResponse>> {
            emit(safeApiCall { remoteDataSource.getDog() })
        }.flowOn(Dispatchers.IO)
    }

}

private fun saveImage(image: Bitmap, storageDir: File, imageFileName: String) {
    var successDirCreated = false
    if (!storageDir.exists()) {
        successDirCreated = storageDir.mkdir()
    }
    if (successDirCreated) {
        val imageFile = File(storageDir, imageFileName)
        val savedImagePath: String = imageFile.getAbsolutePath()
        try {
            val fOut: OutputStream = FileOutputStream(imageFile)
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {

    }
}
