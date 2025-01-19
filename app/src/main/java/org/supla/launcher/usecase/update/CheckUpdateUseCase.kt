package org.supla.launcher.usecase.update

import org.supla.launcher.BuildConfig
import org.supla.launcher.data.model.GithubProject
import org.supla.launcher.data.model.SuplaProject
import org.supla.launcher.data.model.SuplaunchProject
import org.supla.launcher.data.model.Version
import org.supla.launcher.data.source.network.GithubApi
import org.supla.launcher.usecase.version.CheckSuplaVersionUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckUpdateUseCase @Inject constructor(
  private val githubApi: GithubApi,
  private val checkSuplaVersionUseCase: CheckSuplaVersionUseCase
) {

  suspend operator fun invoke(project: GithubProject): Boolean {
    val release = githubApi.latestRelease(project.projectName, project.repoName)

    val remoteVersion = Version.parse(release.name) ?: return false
    val currentVersion = when(project) {
      is SuplaProject -> checkSuplaVersionUseCase()
      is SuplaunchProject -> Version.parse(BuildConfig.VERSION_NAME)
    } ?: return true

    return remoteVersion > currentVersion
  }
}