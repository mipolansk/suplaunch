package org.supla.launcher.features.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import dagger.hilt.android.lifecycle.HiltViewModel
import org.supla.launcher.core.BaseViewModel
import org.supla.launcher.core.ViewState
import org.supla.launcher.service.SleepModeService
import org.supla.launcher.usecase.communication.AppEventsManager
import org.supla.launcher.usecase.communication.LaunchApplication
import org.supla.launcher.usecase.communication.LaunchFailed
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val packageManager: PackageManager,
  private val sleepModeService: SleepModeService,
  private val appEventsManager: AppEventsManager
) : BaseViewModel<MainViewState>(MainViewState()) {

  fun forceSleep() {
    sleepModeService.forceSleepState()
  }

  fun launchApp(application: Application) {
    val intent = packageManager.getLaunchIntentForPackage(application.packageName)
    if (intent != null) {
      appEventsManager.send(LaunchApplication(intent))
    } else {
      appEventsManager.send(LaunchFailed())
    }
  }

  override suspend fun load() {
    val intent = Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER")
    val packages = packageManager.queryIntentActivities(intent, 0)
    updateState { state ->
      state.copy(
        applications = packages
          .mapNotNull { it.activityInfo }
          .filter { it.packageName.equals("org.supla.launcher").not() }
          .map {
            Application(it.packageName, it.loadLabel(packageManager).toString()) { it.loadIcon(packageManager) }
          }
          .sortedBy { it.name }
      )
    }
  }
}

data class MainViewState(
  val applications: List<Application> = emptyList(),
) : ViewState()

data class Application(
  val packageName: String,
  val name: String,
  val iconLoader: () -> Drawable?
)