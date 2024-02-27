package org.supla.launcher

import android.app.Application
import android.content.Intent
import android.content.pm.ResolveInfo
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.supla.launcher.service.FloatingWidgetService


@HiltAndroidApp
class SuplaunchApplication: Application() {

  val scope = CoroutineScope(SupervisorJob())

  override fun onCreate() {
    super.onCreate()

    if (Settings.canDrawOverlays(this)) {
      startService(Intent(this, FloatingWidgetService::class.java))
    }
  }
}