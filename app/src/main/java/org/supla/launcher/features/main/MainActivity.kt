package org.supla.launcher.features.main

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.supla.launcher.R
import org.supla.launcher.core.BaseActivity
import org.supla.launcher.features.main.ui.MainUI
import org.supla.launcher.service.FloatingWidgetService
import org.supla.launcher.service.SleepModeService
import org.supla.launcher.ui.theme.SuplaLauncherTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewEvent, MainViewState>() {

  override val viewModel: MainViewModel by viewModels()

  @Inject
  lateinit var sleepModeService: SleepModeService

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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sleepModeService.onCreate(window)

    lifecycleScope.launch {
      CoroutineScope(Dispatchers.IO).launch {
        sleepModeService.launchStateMachine()
      }
    }

    setContent {
      SuplaLauncherTheme {
        MainUI(
          viewModel.viewState.collectAsState().value,
          onSuplaClick = { startSupla() },
          onDownloadClick = { viewModel.download() },
          onOffClick = { sleepModeService.forceSleepState() },
          onAppClick = { viewModel.launchApp(it) },
          sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        )
      }
    }

    if (!Settings.canDrawOverlays(this)) {
      askOverlayPermission()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    sleepModeService.onDestroy()
  }

  override fun handleEvent(viewEvent: MainViewEvent) {
    when (viewEvent) {
      is MainViewEvent.AskPermission -> askWritePermission()
      is MainViewEvent.LaunchApplication -> startActivity(viewEvent.intent)
      is MainViewEvent.LaunchFailed -> Toast.makeText(this, R.string.launch_failed, Toast.LENGTH_SHORT).show()
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

