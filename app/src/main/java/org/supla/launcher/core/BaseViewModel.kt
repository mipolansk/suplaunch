package org.supla.launcher.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

abstract class BaseViewModel<E : ViewEvent, S: ViewState>(defaultState: S) : ViewModel() {

  private val viewEventsFlow: MutableSharedFlow<Event<E?>> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  private val viewStateFlow: MutableStateFlow<S> = MutableStateFlow(defaultState)

  val viewEvents: Flow<E> = viewEventsFlow
    .filter { it.item != null }
    .filter { it.processed.not() }
    .map {
      it.run {
        processed = true
        item!!
      }
    }
  val viewState: StateFlow<S> = viewStateFlow

  protected fun sendEvent(event: E) {
    viewEventsFlow.tryEmit(Event(event))
  }

  private data class Event<T>(
    val item: T,
    var processed: Boolean = false
  )
}

interface ViewEvent
open class ViewState