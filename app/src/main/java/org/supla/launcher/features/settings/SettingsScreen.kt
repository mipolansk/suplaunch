package org.supla.launcher.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.supla.launcher.BuildConfig
import org.supla.launcher.R
import org.supla.launcher.features.main.ui.UpdateAvailableDialog
import org.supla.launcher.features.main.ui.UpdateFailedDialog
import org.supla.launcher.ui.theme.Distance
import org.supla.launcher.ui.theme.SuplaLauncherTheme

@Composable
fun SettingsScreen(
  navController: NavController,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  viewModel.Load()

  val state by viewModel.viewState.collectAsState()

  Box {
    SettingsView(navController, viewModel, state)

    state.suplaUpdateAvailable?.let {
      UpdateAvailableDialog(
        result = it,
        appName = "Supla",
        onUpdateClose = viewModel::closeSuplaUpdateDialog,
        onUpdateStart = viewModel::performSuplaUpdate
      )
    }

    state.suplaunchUpdateAvailable?.let {
      UpdateAvailableDialog(
        result = it,
        appName = "Suplaunch",
        onUpdateClose = viewModel::closeSuplaunchUpdateDialog,
        onUpdateStart = viewModel::performSuplaunchUpdate
      )
    }

    if (state.suplaUpdateFailed) {
      UpdateFailedDialog(onClose = viewModel::closeSuplaUpdateFailedDialog)
    }

    if (state.suplaunchUpdateFailed) {
      UpdateFailedDialog(onClose = viewModel::closeSuplaunchUpdateFailedDialog)
    }
  }
}

@Composable
private fun SettingsView(navController: NavController, viewModel: SettingsViewModel, state: SettingsViewState) =
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(Distance.normal)
  ) {
    Row(horizontalArrangement = Arrangement.spacedBy(Distance.normal), verticalAlignment = Alignment.CenterVertically) {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Default.ArrowBackIosNew, contentDescription = stringResource(R.string.back))
      }
      Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)
    }

    Spacer(modifier = Modifier.height(Distance.normal))

    SettingsRow(
      title = stringResource(R.string.settings_network),
      text = stringResource(R.string.settings_network_subtitle),
      icon = Icons.AutoMirrored.Outlined.ArrowForward,
      iconDescription = stringResource(R.string.settings_network),
      action = viewModel::openNetworkSettings,
    )

    SettingsRow(
      title = stringResource(R.string.suplaunch_version),
      text = BuildConfig.VERSION_NAME,
      icon = Icons.Default.Refresh,
      iconDescription = stringResource(R.string.refresh),
      action = viewModel::checkSuplaunchUpdate,
      loading = state.loadingSuplaunchVersion,
      loadingPercentage = state.suplaunchDownloadProgress
    )

    SettingsRow(
      title = stringResource(R.string.supla_version),
      text = state.suplaVersion ?: stringResource(R.string.no_supla_installed),
      icon = Icons.Default.Refresh,
      iconDescription = stringResource(R.string.refresh),
      action = viewModel::checkSuplaUpdate,
      loading = state.loadingSuplaVersion,
      loadingPercentage = state.suplaDownloadProgress
    )
  }

@Composable
private fun SettingsRow(
  title: String,
  text: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  iconDescription: String? = null,
  action: (() -> Unit)? = null,
  loading: Boolean = false,
  loadingPercentage: Float? = null
) =
  Row(modifier = modifier.padding(top = Distance.small), verticalAlignment = Alignment.CenterVertically) {
    Column {
      Text(title, style = MaterialTheme.typography.titleMedium)
      Text(text, style = MaterialTheme.typography.bodyMedium)
    }

    Spacer(modifier = Modifier.weight(1f))

    if (loading) {
      Box(modifier = Modifier.minimumInteractiveComponentSize()) {
        CircularProgressIndicator(
          modifier = Modifier
            .size(24.dp)
            .align(Alignment.Center), color = MaterialTheme.colorScheme.primary
        )
      }
    } else if (loadingPercentage != null) {
      Box(modifier = Modifier.minimumInteractiveComponentSize()) {
        CircularProgressIndicator(
          progress = { loadingPercentage },
          modifier = Modifier
            .size(24.dp)
            .align(Alignment.Center),
          color = MaterialTheme.colorScheme.primary
        )
      }
    } else {
      icon?.let { vector ->
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { action?.invoke() }) {
          Icon(vector, contentDescription = iconDescription)
        }
      }
    }
  }

@Preview
@Composable
private fun Preview() {
  SuplaLauncherTheme {
    SettingsScreen(rememberNavController())
  }
}