package org.supla.launcher.features.settings

import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.supla.launcher.core.BaseViewModel
import org.supla.launcher.core.ViewState
import org.supla.launcher.data.model.SuplaProject
import org.supla.launcher.data.model.SuplaunchProject
import org.supla.launcher.data.source.local.PermissionsManager
import org.supla.launcher.usecase.communication.AppEventsManager
import org.supla.launcher.usecase.communication.AskPermission
import org.supla.launcher.usecase.communication.LaunchApplication
import org.supla.launcher.usecase.update.CheckUpdateUseCase
import org.supla.launcher.usecase.update.PerformUpdateUseCase
import org.supla.launcher.usecase.version.CheckSuplaVersionUseCase
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val checkSuplaVersionUseCase: CheckSuplaVersionUseCase,
  private val permissionsManager: PermissionsManager,
  private val checkUpdateUseCase: CheckUpdateUseCase,
  private val performUpdateUseCase: PerformUpdateUseCase,
  private val appEventsManager: AppEventsManager
) : BaseViewModel<SettingsViewState>(SettingsViewState()) {

  override suspend fun load() {
    updateState { it.copy(suplaVersion = checkSuplaVersionUseCase()?.toString()) }
  }

  fun checkSuplaUpdate() {
    viewModelScope.launch {
      if (permissionsManager.isWritePermissionGranted().not()) {
        appEventsManager.send(AskPermission())
        return@launch
      }

      updateState { it.copy(loadingSuplaVersion = true) }

      delay(1.seconds)
      val updateAvailable = checkUpdateUseCase(SuplaProject)

      updateState { it.copy(suplaUpdateAvailable = updateAvailable, loadingSuplaVersion = false) }
    }
  }

  fun closeSuplaUpdateDialog() {
    updateState { it.copy(suplaUpdateAvailable = null) }
  }

  fun performSuplaUpdate() {
    viewModelScope.launch {
      updateState { it.copy(suplaDownloadProgress = 0f, suplaUpdateAvailable = null) }

      performUpdateUseCase(SuplaProject).collect { state ->
        withContext(Dispatchers.Main) {
          when (state) {
            is PerformUpdateUseCase.DownloadingState ->
              updateState { it.copy(suplaDownloadProgress = state.progress.div(100f)) }

            is PerformUpdateUseCase.FinishedState -> {
              val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
              intent.setData(state.uri)
              intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
              appEventsManager.send(LaunchApplication(intent))
              updateState { it.copy(suplaDownloadProgress = null) }
            }

            is PerformUpdateUseCase.FailedState -> {
              updateState { it.copy(suplaDownloadProgress = null, suplaUpdateFailed = true) }
            }
          }
        }
      }
    }
  }

  fun closeSuplaUpdateFailedDialog() {
    updateState { it.copy(suplaUpdateFailed = false) }
  }

  fun checkSuplaunchUpdate() {
    viewModelScope.launch {
      if (permissionsManager.isWritePermissionGranted().not()) {
        appEventsManager.send(AskPermission())
        return@launch
      }

      updateState { it.copy(loadingSuplaunchVersion = true) }

      delay(1.seconds)
      val updateAvailable = checkUpdateUseCase(SuplaunchProject)

      updateState { it.copy(suplaunchUpdateAvailable = updateAvailable, loadingSuplaunchVersion = false) }
    }
  }

  fun closeSuplaunchUpdateDialog() {
    updateState { it.copy(suplaunchUpdateAvailable = null) }
  }

  fun performSuplaunchUpdate() {
    viewModelScope.launch {
      updateState { it.copy(suplaunchDownloadProgress = 0f, suplaunchUpdateAvailable = null) }

      performUpdateUseCase(SuplaunchProject).collect { state ->
        withContext(Dispatchers.Main) {
          when (state) {
            is PerformUpdateUseCase.DownloadingState ->
              updateState { it.copy(suplaunchDownloadProgress = state.progress.div(100f)) }

            is PerformUpdateUseCase.FinishedState -> {
              val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
              intent.setData(state.uri)
              intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
              appEventsManager.send(LaunchApplication(intent))
              updateState { it.copy(suplaunchDownloadProgress = null) }
            }

            is PerformUpdateUseCase.FailedState -> {
              updateState { it.copy(suplaunchDownloadProgress = null, suplaunchUpdateFailed = true) }
            }
          }
        }
      }
    }
  }

  fun closeSuplaunchUpdateFailedDialog() {
    updateState { it.copy(suplaunchUpdateFailed = false) }
  }

  fun openNetworkSettings() {
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
    appEventsManager.send(LaunchApplication(intent))
  }
}


data class SettingsViewState(
  val suplaVersion: String? = null,

  val loadingSuplaVersion: Boolean = false,
  val suplaUpdateAvailable: CheckUpdateUseCase.Result? = null,
  val suplaDownloadProgress: Float? = null,
  val suplaUpdateFailed: Boolean = false,

  val loadingSuplaunchVersion: Boolean = false,
  val suplaunchUpdateAvailable: CheckUpdateUseCase.Result? = null,
  val suplaunchDownloadProgress: Float? = null,
  val suplaunchUpdateFailed: Boolean = false,
) : ViewState()