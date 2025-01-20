package org.supla.launcher.features.main.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.supla.launcher.R
import org.supla.launcher.usecase.update.CheckUpdateUseCase

@Composable
fun UpdateAvailableDialog(
  result: CheckUpdateUseCase.Result,
  appName: String,
  onUpdateClose: () -> Unit,
  onUpdateStart: () -> Unit
) =
  AlertDialog(
    onDismissRequest = onUpdateClose,
    title = { Text(stringResource(R.string.update_title, appName)) },
    text = {
      Text(
        text =
        when (result) {
          CheckUpdateUseCase.Error -> stringResource(R.string.update_check_error)
          CheckUpdateUseCase.NoUpdateAvailable -> stringResource(R.string.update_unavailable)
          is CheckUpdateUseCase.VersionAvailable -> stringResource(R.string.update_available, result.version, appName)
        }
      )
    },
    confirmButton = {
      if (result is CheckUpdateUseCase.VersionAvailable) {
        Button(onClick = onUpdateStart) {
          Text(stringResource(R.string.update_start))
        }
      }
    },
    dismissButton = {
      Button(onClick = onUpdateClose) {
        Text(stringResource(if (result is CheckUpdateUseCase.VersionAvailable) R.string.cancel else R.string.close))
      }
    }
  )