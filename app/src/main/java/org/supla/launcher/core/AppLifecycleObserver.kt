package org.supla.launcher.core

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.supla.launcher.service.SleepModeEvent
import org.supla.launcher.service.SleepModeStateMachine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor(private val sleepModeStateMachine: SleepModeStateMachine) : DefaultLifecycleObserver {

  override fun onStart(owner: LifecycleOwner) {
    sleepModeStateMachine.handleEvent(SleepModeEvent.AppState(SleepModeEvent.AppState.Foreground))
  }

  override fun onStop(owner: LifecycleOwner) {
    sleepModeStateMachine.handleEvent(SleepModeEvent.AppState(SleepModeEvent.AppState.Background))
  }
}