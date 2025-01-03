package org.supla.launcher.usecase.update

import org.supla.launcher.data.model.Version
import org.supla.launcher.data.source.network.DownloadUpdateApi
import org.supla.launcher.usecase.version.CheckSuplaVersionUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckUpdateUseCase @Inject constructor(
  private val downloadUpdateApi: DownloadUpdateApi,
  private val checkSuplaVersionUseCase: CheckSuplaVersionUseCase
) {

  suspend operator fun invoke(): Boolean {
    val release = downloadUpdateApi.latestRelease()

    val remoteVersion = Version.parse(release.name) ?: return false
    val currentVersion = checkSuplaVersionUseCase() ?: return true

    return remoteVersion > currentVersion
  }
}