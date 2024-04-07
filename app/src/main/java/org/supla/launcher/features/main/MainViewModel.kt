package org.supla.launcher.features.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.supla.launcher.core.BaseViewModel
import org.supla.launcher.core.ViewEvent
import org.supla.launcher.core.ViewState
import org.supla.launcher.data.source.local.PermissionsManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val permissionsManager: PermissionsManager,
  private val packageManager: PackageManager
) : BaseViewModel<MainViewEvent, MainViewState>(MainViewState()) {

  fun download() {
    viewModelScope.launch {
      if (permissionsManager.isWritePermissionGranted().not()) {
        sendEvent(MainViewEvent.AskPermission)
      }
    }
  }

  fun launchApp(application: Application) {
    val intent = packageManager.getLaunchIntentForPackage(application.packageName)
    if (intent != null) {
      sendEvent(MainViewEvent.LaunchApplication(intent))
    } else {
      sendEvent(MainViewEvent.LaunchFailed)
    }
  }

  override fun onCreated() {
    val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    updateState { state ->
      state.copy(
        applications = packages
          .map { Application(it.packageName, packageManager.getApplicationLabel(it).toString()) { it.loadIcon(packageManager) } }
          .sortedBy { it.name }
      )
    }
  }
}

sealed class MainViewEvent : ViewEvent {
  object AskPermission : MainViewEvent()
  data class LaunchApplication(val intent: Intent) : MainViewEvent()
  object LaunchFailed : MainViewEvent()
}

data class MainViewState(
  val applications: List<Application> = emptyList()
) : ViewState()

data class Application(
  val packageName: String,
  val name: String,
  val iconLoader: () -> Drawable?
)