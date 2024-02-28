package org.supla.launcher

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.supla.launcher.core.AppLifecycleObserver
import org.supla.launcher.service.FloatingWidgetService
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class SuplaunchApplication : Application() {

  val scope = CoroutineScope(SupervisorJob())

  @Inject
  lateinit var lifecycleObserver: AppLifecycleObserver

  override fun onCreate() {
    super.onCreate()

    if (Settings.canDrawOverlays(this)) {
      startService(Intent(this, FloatingWidgetService::class.java))
    }

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
  }
}