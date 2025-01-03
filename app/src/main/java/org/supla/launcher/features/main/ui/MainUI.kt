package org.supla.launcher.features.main.ui

import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.supla.launcher.features.main.Application
import org.supla.launcher.features.main.MainViewState
import org.supla.launcher.ui.theme.SuplaLauncherTheme


@Composable
fun MainUI(
  viewState: MainViewState,
  onSuplaClick: (() -> Unit)? = null,
  onDownloadClick: (() -> Unit)? = null,
  onOffClick: (() -> Unit)? = null,
  onAppClick: ((Application) -> Unit)? = null,
  onUpdateClose: () -> Unit = {},
  onUpdateStart: () -> Unit = {},
  onFailedDialogClose: () -> Unit = {},
  sensorManager: SensorManager? = null
) {
  Box {
    HorizontalPager(
      state = rememberPagerState { 2 },
      modifier = Modifier.fillMaxSize()
    ) { page ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(color = MaterialTheme.colorScheme.primary)
      ) {
        when (page) {
          0 -> HomePage(
            viewState.showIndeterminateProgress,
            viewState.showProgress,
            onSuplaClick,
            onDownloadClick,
            onOffClick,
            sensorManager
          )

          1 -> AppsPage(viewState.applications, onAppClick)
        }
      }
    }

    viewState.updateAvailable?.let {
      UpdateAvailableDialog(
        updateAvailable = it,
        onUpdateClose = onUpdateClose,
        onUpdateStart = onUpdateStart
      )
    }

    if (viewState.showUpdateFailed) {
      UpdateFailedDialog(onClose = onFailedDialogClose)
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