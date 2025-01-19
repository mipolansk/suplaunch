package org.supla.launcher.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<S: ViewState>(defaultState: S) : ViewModel() {

  private val viewStateFlow: MutableStateFlow<S> = MutableStateFlow(defaultState)

  val viewState: StateFlow<S> = viewStateFlow

  protected fun updateState(updater: (S) -> S) {
    viewStateFlow.tryEmit(updater(viewStateFlow.value))
  }

  protected open suspend fun load() {}

  @Composable
  fun Load() {
    LaunchedEffect(Unit) {
      load()
    }
  }
}

open class ViewState
