package org.supla.launcher.usecase.communication

import android.content.Intent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppEventsManager @Inject constructor() {

  private val eventsFlow: MutableSharedFlow<AppEvent> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  val events: Flow<AppEvent> = eventsFlow
    .filter { it.processed.not() }
    .map { it.apply { processed = true } }

  fun send(event: AppEvent) {
    eventsFlow.tryEmit(event)
  }
}

sealed class AppEvent {
  var processed: Boolean = false
}

class AskPermission : AppEvent()
data class LaunchApplication(val intent: Intent) : AppEvent()
class LaunchFailed : AppEvent()