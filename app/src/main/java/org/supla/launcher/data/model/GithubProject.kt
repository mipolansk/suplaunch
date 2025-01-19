package org.supla.launcher.data.model

sealed interface GithubProject {
  val projectName: String
  val repoName: String
  val destinationFileName: String
}

data object SuplaProject: GithubProject {
  override val projectName: String
    get() = "SUPLA"
  override val repoName: String
    get() = "supla-android"
  override val destinationFileName: String
    get() = "SUPLA.apk"
}

data object SuplaunchProject: GithubProject {
  override val projectName: String
    get() = "mipolansk"
  override val repoName: String
    get() = "suplaunch"
  override val destinationFileName: String
    get() = "Suplaunch.apk"
}
