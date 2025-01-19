package org.supla.launcher.features.main

import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.supla.launcher.features.main.ui.AppsPage
import org.supla.launcher.features.main.ui.HomePage
import org.supla.launcher.ui.theme.SuplaLauncherTheme

@Composable
fun MainScreen(
  navController: NavController,
  onSuplaClick: (() -> Unit)? = null,
  sensorManager: SensorManager? = null,
  viewModel: MainViewModel = hiltViewModel()
) {
  viewModel.Load()
  val viewState by viewModel.viewState.collectAsState()

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
            onSuplaClick,
            viewModel::forceSleep,
            sensorManager
          )

          1 -> AppsPage(navController, viewState.applications, viewModel::launchApp)
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  SuplaLauncherTheme {
    MainScreen(rememberNavController())
  }
}