package org.supla.launcher.features.main

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import org.supla.launcher.R
import org.supla.launcher.core.BaseActivity
import org.supla.launcher.service.FloatingWidgetService
import org.supla.launcher.ui.theme.SuplaLauncherTheme

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewEvent, MainViewState>() {

  override val viewModel: MainViewModel by viewModels()

  private val requestPermissionLauncher =
    registerForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
      if (isGranted) {
        viewModel.download()
      } else {
        // Explain to the user that the feature is unavailable because the
        // feature requires a permission that the user has denied. At the
        // same time, respect the user's decision. Don't link to system
        // settings in an effort to convince the user to change their
        // decision.
      }
    }

  private val requestOverlayPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
      if (Settings.canDrawOverlays(this)) {
        startService(Intent(this, FloatingWidgetService::class.java))
      }
    }

  var active = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      var position by remember { mutableStateOf(0f) }

      val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
      sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.let { sensor ->
        sensorManager.registerListener(object : SensorEventListener {
          override fun onSensorChanged(p0: SensorEvent?) {
            p0?.values?.firstOrNull()?.let {
              position = it
            }
          }

          override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
          }
        }, sensor, 500_000)
      }

      SuplaLauncherTheme {
        MainUI (
          onSuplaClick = { startSupla() },
          onDownloadClick = { viewModel.download() },
          onOffClick = {
            val attributes = window.attributes
            attributes.screenBrightness = if (active) .01f else 1f
            active = !active
            window.attributes = attributes
          },
          //sensorValue = String.format("%.2f", position)
        )
      }
    }

    if (!Settings.canDrawOverlays(this)) {
      askOverlayPermission()
    }
  }

  override fun handleEvent(viewEvent: MainViewEvent) {
    when (viewEvent) {
      is MainViewEvent.AskPermission -> askWritePermission()
    }
  }

  private fun startSupla() {
    val launchIntent = packageManager.getLaunchIntentForPackage("org.supla.android")
    if (launchIntent != null) {
      startActivity(launchIntent)
    } else {
      Toast.makeText(this, R.string.supla_not_installed, Toast.LENGTH_LONG).show()
    }
  }

  private fun askWritePermission() {
    when {
      ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE) -> {
        showRationale()
      }

      else -> requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
    }
  }

  private fun askOverlayPermission() {
    requestOverlayPermissionLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
  }

  private fun showRationale() {
    Toast.makeText(this, "Permission needed!", Toast.LENGTH_LONG).show()
  }
}

