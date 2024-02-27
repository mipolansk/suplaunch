package org.supla.launcher.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Window
import kotlinx.coroutines.delay
import org.supla.launcher.extensions.guardLet
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

private const val WEAK_UP_DISTANCE = 1000f
private const val WEAK_UP_TIME_MINS = 5

@Singleton
class SleepModeService @Inject constructor(private val sensorManager: SensorManager) : SensorEventListener {

  private val lock = Any()

  private lateinit var window: Window
  private var lastNearTime = LocalDateTime.now()
  private var state = State.AWAKE

  fun onCreate(window: Window) {
    this.window = window
    sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.let {
      sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
    }

  }

  suspend fun observe() {
    while (true) {
      delay(1.seconds)
      when (state) {
        State.AWAKE -> {
          if (ChronoUnit.MINUTES.between(LocalDateTime.now(), lastNearTime).toInt() > WEAK_UP_TIME_MINS) {
            changeState(State.SLEEPING)
          }
        }

        State.SLEEPING -> {} // do nothing
      }
    }
  }

  fun onDestroy() {
    sensorManager.unregisterListener(this)
  }

  override fun onSensorChanged(event: SensorEvent?) {
    val (values) = guardLet(event?.values) { return }
    if (values.size < 1) {
      return
    }

    val value = values[0]
    if (value > WEAK_UP_DISTANCE) {
      lastNearTime = LocalDateTime.now()
    }
  }

  override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
  }

  private fun changeState(newState: State) {
    synchronized(lock) {
      when (newState) {
        State.SLEEPING -> screenDim(window)
        State.AWAKE -> screenBrighten(window)
      }
    }
  }

  private fun screenDim(window: Window) {
    val attributes = window.attributes
    attributes.screenBrightness = 0f
    window.attributes = attributes
  }

  private fun screenBrighten(window: Window) {
    val attributes = window.attributes
    attributes.screenBrightness = 1f
    window.attributes = attributes
  }

  enum class State {
    AWAKE, SLEEPING
  }
}
