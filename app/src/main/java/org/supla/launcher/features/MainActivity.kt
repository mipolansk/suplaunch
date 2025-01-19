package org.supla.launcher.features

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.supla.launcher.R
import org.supla.launcher.core.BaseActivity
import org.supla.launcher.features.main.MainViewState
import org.supla.launcher.service.FloatingWidgetService
import org.supla.launcher.service.SleepModeService
import org.supla.launcher.ui.theme.SuplaLauncherTheme
import org.supla.launcher.usecase.communication.AppEvent
import org.supla.launcher.usecase.communication.AppEventsManager
import org.supla.launcher.usecase.communication.AskPermission
import org.supla.launcher.usecase.communication.LaunchApplication
import org.supla.launcher.usecase.communication.LaunchFailed
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewState>() {

  @Inject
  lateinit var sleepModeService: SleepModeService

  @Inject
  lateinit var appEventsManager: AppEventsManager

  private val requestPermissionLauncher =
    registerForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
      if (isGranted) {
        //viewModel.download()
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
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        appEventsManager.events.collect {
          handleEvent(it)
        }
      }
    }

    setContent {
      SuplaLauncherTheme {
        SuplaunchNavigationHost(onSuplaLaunch = this::startSupla)
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

  fun handleEvent(appEvent: AppEvent) {
    when (appEvent) {
      is AskPermission -> askWritePermission()
      is LaunchApplication -> {
        if (appEvent.intent.resolveActivity(packageManager) != null) {
          startActivity(appEvent.intent)
        } else {
          Toast.makeText(this, R.string.launch_failed, Toast.LENGTH_SHORT).show()
        }
      }

      is LaunchFailed -> Toast.makeText(this, R.string.launch_failed, Toast.LENGTH_SHORT).show()
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

