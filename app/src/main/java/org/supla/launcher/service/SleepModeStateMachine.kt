package org.supla.launcher.service

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.Window
import dagger.hilt.android.qualifiers.ApplicationContext
import org.supla.launcher.features.MainActivity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

// Use for emulator
//private const val WEAK_UP_DISTANCE = 5f
//private const val WEAK_UP_DELAY_MINS = 1

// Use for real device
private const val WEAK_UP_DISTANCE = 20f
private const val WEAK_UP_DELAY_SECS = 300

@Singleton
class SleepModeStateMachine @Inject constructor(
  @ApplicationContext private val context: Context
) {

  private var state: State = State.AwakeState(this)
  private val handler = Handler(Looper.getMainLooper())
  private var window: Window? = null
  private val lock = Any()

  fun attach(window: Window) {
    this.window = window
  }

  fun detach() {
    window = null
  }

  fun handleEvent(event: SleepModeEvent) {
    synchronized(lock) {
      state.handleEvent(event)
    }
  }

  fun changeState(state: State) {
    Timber.i("State change from ${this.state.javaClass.simpleName} to ${state.javaClass.simpleName}")
    this.state = state
  }

  fun screenDim() {
    Timber.i("Dimming screen")
    val action = {
      window?.let {
        val attributes = it.attributes
        attributes.screenBrightness = 0f
        it.attributes = attributes
      }
    }

    if (state is State.ForcedSleepingState) {
      action()
    } else {
      val runnable = Runnable {
        if (state is State.SleepingState) {
          action()
        }
      }
      handler.postDelayed(runnable, 1500)
    }
  }

  private fun screenBrighten() {
    Timber.i("Brightening screen")
    window?.let {
      val attributes = it.attributes
      attributes.screenBrightness = 1f
      it.attributes = attributes
    }
  }

  private fun reopenApp() {
    Timber.i("Reopening application")
    context.startActivity(Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
  }

  sealed class State(protected val machine: SleepModeStateMachine) {

    abstract fun handleEvent(event: SleepModeEvent)

    class SleepingState(machine: SleepModeStateMachine) : State(machine) {
      override fun handleEvent(event: SleepModeEvent) {
        when (event) {
          is SleepModeEvent.Distance -> handleDistance(event.distance)
          is SleepModeEvent.AppState -> handleAppState(event.state)
        }

      }

      private fun handleDistance(distance: Float) {
        if (distance > WEAK_UP_DISTANCE) {
          machine.screenBrighten()
          machine.changeState(AwakeState(machine))
        }
      }

      private fun handleAppState(state: SleepModeEvent.AppState.Value) {
        if (state is SleepModeEvent.AppState.Background) {
          machine.changeState(BackgroundState(machine))
        }
      }
    }

    class AwakeState(machine: SleepModeStateMachine) : State(machine) {
      override fun handleEvent(event: SleepModeEvent) {
        when (event) {
          is SleepModeEvent.Distance -> handleDistance(event.distance)
          is SleepModeEvent.AppState -> handleAppState(event.state)
        }
      }

      private fun handleDistance(distance: Float) {
        if (distance < WEAK_UP_DISTANCE) {
          machine.changeState(InactiveForegroundState(machine))
        }
      }

      private fun handleAppState(state: SleepModeEvent.AppState.Value) {
        if (state is SleepModeEvent.AppState.Background) {
          machine.changeState(BackgroundState(machine))
        }
      }
    }

    class InactiveForegroundState(machine: SleepModeStateMachine) : State(machine) {

      private val stateStartTime: Long = System.currentTimeMillis()

      override fun handleEvent(event: SleepModeEvent) {
        when (event) {
          is SleepModeEvent.Distance -> handleDistance(event.distance)
          is SleepModeEvent.AppState -> handleAppState(event.state)
        }
      }

      private fun handleDistance(distance: Float) {
        if (distance < WEAK_UP_DISTANCE && timeElapsed()) {
          machine.screenDim()
          machine.changeState(SleepingState(machine))
        }
        if (distance > WEAK_UP_DISTANCE) {
          machine.changeState(AwakeState(machine))
        }
      }

      private fun handleAppState(state: SleepModeEvent.AppState.Value) {
        if (state is SleepModeEvent.AppState.Background) {
          machine.changeState(BackgroundState(machine))
        }
      }

      private fun timeElapsed(): Boolean =
        stateStartTime + WEAK_UP_DELAY_SECS.times(1000) < System.currentTimeMillis()
    }

    class BackgroundState(machine: SleepModeStateMachine) : State(machine) {
      override fun handleEvent(event: SleepModeEvent) {
        when (event) {
          is SleepModeEvent.Distance -> handleDistance(event.distance)
          is SleepModeEvent.AppState -> handleAppState(event.state)
        }
      }

      private fun handleDistance(distance: Float) {
        if (distance < WEAK_UP_DISTANCE) {
          machine.changeState(InactiveBackgroundState(machine))
        }
      }

      private fun handleAppState(state: SleepModeEvent.AppState.Value) {
        if (state is SleepModeEvent.AppState.Foreground) {
          machine.changeState(AwakeState(machine))
        }
      }
    }

    class InactiveBackgroundState(machine: SleepModeStateMachine) : State(machine) {

      private val stateStartTime: Long = System.currentTimeMillis()

      override fun handleEvent(event: SleepModeEvent) {
        when (event) {
          is SleepModeEvent.Distance -> handleDistance(event.distance)
          is SleepModeEvent.AppState -> handleAppState(event.state)
        }
      }

      private fun handleDistance(distance: Float) {
        if (distance < WEAK_UP_DISTANCE && timeElapsed()) {
          machine.reopenApp()
          machine.screenDim()
          machine.changeState(SleepingState(machine))
        }
        if (distance > WEAK_UP_DISTANCE) {
          machine.changeState(BackgroundState(machine))
        }
      }

      private fun handleAppState(state: SleepModeEvent.AppState.Value) {
        if (state is SleepModeEvent.AppState.Foreground) {
          machine.changeState(InactiveForegroundState(machine))
        }
      }

      private fun timeElapsed(): Boolean =
        stateStartTime + WEAK_UP_DELAY_SECS.times(1000) < System.currentTimeMillis()

    }

    /** State used to force sleep mode, when user pressing turn off button */
    class ForcedSleepingState(machine: SleepModeStateMachine) : State(machine) {

      private val stateStartTime: Long = System.currentTimeMillis()

      override fun handleEvent(event: SleepModeEvent) {
        when (event) {
          is SleepModeEvent.Distance -> handleDistance(event.distance)
          is SleepModeEvent.AppState -> handleAppState(event.state)
        }
      }

      private fun handleDistance(distance: Float) {
        if (timeElapsed()) {
          if (distance < WEAK_UP_DISTANCE) {
            machine.changeState(SleepingState(machine))
          } else {
            machine.screenBrighten()
            machine.changeState(AwakeState(machine))
          }
        }
      }

      private fun handleAppState(state: SleepModeEvent.AppState.Value) {
        if (state is SleepModeEvent.AppState.Background) {
          machine.changeState(BackgroundState(machine))
        }
      }

      private fun timeElapsed(): Boolean =
        stateStartTime + 5000 < System.currentTimeMillis()
    }
  }
}

