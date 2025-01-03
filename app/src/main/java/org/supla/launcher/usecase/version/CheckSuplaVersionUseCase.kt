package org.supla.launcher.usecase.version

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import dagger.hilt.android.qualifiers.ApplicationContext
import org.supla.launcher.data.model.Version
import javax.inject.Inject
import javax.inject.Singleton

private const val SUPLA_PACKAGE = "org.supla.android"

@Singleton
class CheckSuplaVersionUseCase @Inject constructor(
  @ApplicationContext private val context: Context
) {

  operator fun invoke(): Version? =
    try {
      context.packageManager.getPackageInfo(SUPLA_PACKAGE, 0)
        ?.versionName
        ?.let { Version.parse(it) }
    } catch (exception: NameNotFoundException) {
      null
    }
}