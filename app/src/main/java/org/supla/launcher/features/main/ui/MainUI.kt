package org.supla.launcher.features.main.ui

import android.hardware.SensorManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.supla.launcher.features.main.Application
import org.supla.launcher.features.main.MainViewState
import org.supla.launcher.ui.theme.SuplaLauncherTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainUI(
  viewState: MainViewState,
  onSuplaClick: (() -> Unit)? = null,
  onDownloadClick: (() -> Unit)? = null,
  onOffClick: (() -> Unit)? = null,
  onAppClick: ((Application) -> Unit)? = null,
  sensorManager: SensorManager? = null
) {
  HorizontalPager(
    pageCount = 2,
    modifier = Modifier.fillMaxSize()
  ) { page ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.primary)
    ) {
      when (page) {
        0 -> HomePage(onSuplaClick, onDownloadClick, onOffClick, sensorManager)
        1 -> AppsPage(viewState.applications, onAppClick)
      }
    }
  }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  SuplaLauncherTheme {
    MainUI(MainViewState())
  }
}