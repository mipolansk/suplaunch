package org.supla.launcher.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

abstract class BaseActivity<E : ViewEvent, S: ViewState> : ComponentActivity() {

  abstract val viewModel: BaseViewModel<E, S>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.viewEvents.collect {
          handleEvent(it)
        }
      }
    }
  }

  protected open fun handleEvent(viewEvent: E) {
  }
}