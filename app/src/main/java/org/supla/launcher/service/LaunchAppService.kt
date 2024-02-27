package org.supla.launcher.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import org.supla.launcher.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LaunchAppService @Inject constructor(@ApplicationContext private val context: Context) {

  fun launchSupla() {
    launchIntent(context.packageManager.getLaunchIntentForPackage("org.supla.android")) {
      Toast.makeText(context, R.string.supla_not_installed, Toast.LENGTH_LONG).show()
    }
  }

  fun launchSuplaunch() {
    launchIntent(context.packageManager.getLaunchIntentForPackage("org.supla.launcher")) {
      Toast.makeText(context, R.string.supla_not_installed, Toast.LENGTH_LONG).show()
    }
  }

  fun launchHome() {
    launchIntent(context.packageManager.getLaunchIntentForPackage("com.eWeLinkControlPanel")) {
      Toast.makeText(context, R.string.ewelink_not_installed, Toast.LENGTH_LONG).show()
    }
  }

  fun launchIntent(intent: Intent?, noIntentAction: (() -> Unit)? = null) {
    if (intent != null) {
      context.startActivity(intent)
    } else {
      noIntentAction?.let { it() }
    }
  }
}