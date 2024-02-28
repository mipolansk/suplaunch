package org.supla.launcher.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Window
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.supla.launcher.extensions.guardLet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepModeService @Inject constructor(
  private val sensorManager: SensorManager,
  private val stateMachine: SleepModeStateMachine
) : SensorEventListener {

  private var lastValue: Float = 0f

  fun onCreate(window: Window) {
    stateMachine.attach(window)
    sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.let {
      sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
    }
  }

  suspend fun launchStateMachine() {
    var working = true
    while (working) {
      try {
        delay(100)
      } catch (ex: CancellationException) {
        working = false
      }

      CoroutineScope(Dispatchers.Main).launch {
        stateMachine.handleEvent(SleepModeEvent.Distance(lastValue))
      }
    }
  }

  fun forceSleepState() {
    stateMachine.changeState(SleepModeStateMachine.State.ForcedSleepingState(stateMachine))
    stateMachine.screenDim()
  }

  fun onDestroy() {
    sensorManager.unregisterListener(this)
    stateMachine.detach()
  }

  override fun onSensorChanged(event: SensorEvent?) {
    val (values) = guardLet(event?.values) { return }
    if (values.isEmpty()) {
      return
    }

    lastValue = values[0]
  }

  override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
  }
}
