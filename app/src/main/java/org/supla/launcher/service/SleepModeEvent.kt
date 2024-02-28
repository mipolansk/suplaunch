package org.supla.launcher.service

sealed interface SleepModeEvent {

  data class Distance(
    val distance: Float
  ) : SleepModeEvent

  data class AppState(
    val state: Value
  ) : SleepModeEvent {

    sealed interface Value
    object Foreground : Value
    object Background : Value
  }
}