package org.supla.launcher.features.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.supla.launcher.core.BaseViewModel
import org.supla.launcher.core.ViewEvent
import org.supla.launcher.core.ViewState
import org.supla.launcher.data.source.local.PermissionsManager
import org.supla.launcher.usecase.update.CheckUpdateUseCase
import org.supla.launcher.usecase.update.PerformUpdateUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val permissionsManager: PermissionsManager,
  private val packageManager: PackageManager,
  private val checkUpdateUseCase: CheckUpdateUseCase,
  private val performUpdateUseCase: PerformUpdateUseCase,
) : BaseViewModel<MainViewEvent, MainViewState>(MainViewState()) {

  fun download() {
    viewModelScope.launch {
      if (permissionsManager.isWritePermissionGranted().not()) {
        sendEvent(MainViewEvent.AskPermission)
        return@launch
      }

      updateState { it.copy(showIndeterminateProgress = true) }

      val updateAvailable = checkUpdateUseCase()

      updateState { it.copy(updateAvailable = updateAvailable, showIndeterminateProgress = false) }
    }
  }

  fun closeUpdateDialog() {
    updateState { it.copy(updateAvailable = null) }
  }

  fun performUpdate() {
    viewModelScope.launch {
      updateState { it.copy(showProgress = 0, updateAvailable = null) }

      performUpdateUseCase().collect { state ->
        withContext(Dispatchers.Main) {
          when (state) {
            is PerformUpdateUseCase.DownloadingState ->
              updateState { it.copy(showProgress = state.progress) }

            is PerformUpdateUseCase.FinishedState -> {
              val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
              intent.setData(state.uri)
              intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
              sendEvent(MainViewEvent.LaunchApplication(intent))
              updateState { it.copy(showProgress = null) }
            }

            is PerformUpdateUseCase.FailedState -> {
              updateState { it.copy(showProgress = null, showUpdateFailed = true) }
            }
          }
        }
      }
    }
  }

  fun closeUpdateFailedDialog() {
    updateState { it.copy(showUpdateFailed = false) }
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

sealed class MainViewEvent : ViewEvent {
  data object AskPermission : MainViewEvent()
  data class LaunchApplication(val intent: Intent) : MainViewEvent()
  data object LaunchFailed : MainViewEvent()
}

data class MainViewState(
  val applications: List<Application> = emptyList(),
  val showIndeterminateProgress: Boolean = false,
  val showProgress: Int? = null,
  val updateAvailable: Boolean? = null,
  val showUpdateFailed: Boolean = false
) : ViewState()

data class Application(
  val packageName: String,
  val name: String,
  val iconLoader: () -> Drawable?
)