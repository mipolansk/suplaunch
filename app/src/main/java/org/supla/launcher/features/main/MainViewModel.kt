package org.supla.launcher.features.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.supla.launcher.core.BaseViewModel
import org.supla.launcher.core.ViewEvent
import org.supla.launcher.core.ViewState
import org.supla.launcher.data.source.local.PermissionsManager
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainViewModel @Inject constructor(
  private val permissionsManager: PermissionsManager
) : BaseViewModel<MainViewEvent, MainViewState>(MainViewState()) {

  fun download() {
    viewModelScope.launch {
      if (permissionsManager.isWritePermissionGranted().not()) {
        sendEvent(MainViewEvent.AskPermission)
      }
    }
  }
}

sealed class MainViewEvent : ViewEvent {
  object AskPermission : MainViewEvent()
}

class MainViewState: ViewState()
