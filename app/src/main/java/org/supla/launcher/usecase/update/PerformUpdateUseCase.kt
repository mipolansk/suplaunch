package org.supla.launcher.usecase.update

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import org.supla.launcher.BuildConfig
import org.supla.launcher.data.model.Version
import org.supla.launcher.data.source.network.DownloadUpdateApi
import org.supla.launcher.usecase.version.CheckSuplaVersionUseCase
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformUpdateUseCase @Inject constructor(
  private val downloadUpdateApi: DownloadUpdateApi,
  private val checkSuplaVersionUseCase: CheckSuplaVersionUseCase,
  @ApplicationContext private val context: Context
) {

  operator fun invoke(): Flow<State> {
    return flow {
      val release = downloadUpdateApi.latestRelease()

      val remoteVersion = Version.parse(release.name)
      if (remoteVersion == null) {
        emit(FailedState())
        return@flow
      }

      val currentVersion = checkSuplaVersionUseCase()

      if (currentVersion != null && remoteVersion <= currentVersion) {
        Timber.i("No newer version found, update skipped")
        emit(FailedState())
        return@flow
      }

      val downloadUrl = release.assets.getOrNull(0)?.browserDownloadUrl
      if (downloadUrl == null) {
        emit(FailedState())
        return@flow
      }
      val fileResponse = downloadUpdateApi.apkFile(downloadUrl)


      val destinationDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      val destinationFile = File(destinationDirectory, "SUPLA.apk")

      val body = fileResponse.body()
      if (body == null) {
        emit(FailedState())
        return@flow
      }

      body.saveFile(context, destinationFile)
    }
      .flowOn(Dispatchers.IO)
      .distinctUntilChanged()
  }

  sealed interface State

  data class DownloadingState(val progress: Int) : State
  data class FinishedState(val uri: Uri) : State
  data class FailedState(val error: Throwable? = null) : State
}

context(FlowCollector<PerformUpdateUseCase.State>)
private suspend fun ResponseBody.saveFile(context: Context, destination: File) {
    emit(PerformUpdateUseCase.DownloadingState(0))

    try {
      byteStream().use { inputStream ->
        destination.outputStream().use { outputStream ->
          val totalBytes = contentLength()
          val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
          var progressBytes = 0L
          var bytes = inputStream.read(buffer)

          while (bytes >= 0) {
            outputStream.write(buffer, 0, bytes)
            progressBytes += bytes
            bytes = inputStream.read(buffer)

            emit(PerformUpdateUseCase.DownloadingState(progressBytes.times(100).div(totalBytes).toInt()))
          }
        }
      }

      val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", destination)
      emit(PerformUpdateUseCase.FinishedState(uri))
    } catch (exception: Exception) {
      emit(PerformUpdateUseCase.FailedState(exception))
    }
}