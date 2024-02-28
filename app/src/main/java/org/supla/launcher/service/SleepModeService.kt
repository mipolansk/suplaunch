package org.supla.launcher.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Window
import org.supla.launcher.extensions.guardLet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepModeService @Inject constructor(
  private val sensorManager: SensorManager,
  private val stateMachine: SleepModeStateMachine
) : SensorEventListener {

  fun onCreate(window: Window) {
    stateMachine.attach(window)
    sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.let {
      sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
    }

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

    stateMachine.handleEvent(SleepModeEvent.Distance(values[0]))
  }

  override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
  }
}
