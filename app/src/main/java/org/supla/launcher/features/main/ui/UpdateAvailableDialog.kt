package org.supla.launcher.features.main.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.supla.launcher.R

@Composable
fun UpdateAvailableDialog(
  updateAvailable: Boolean,
  onUpdateClose: () -> Unit,
  onUpdateStart: () -> Unit
) =
  AlertDialog(
    onDismissRequest = onUpdateClose,
    title = { Text(stringResource(R.string.update_title)) },
    text = { Text(stringResource(if (updateAvailable) R.string.update_available else R.string.update_unavailable)) },
    confirmButton = {
      if (updateAvailable) {
        Button(onClick = onUpdateStart) {
          Text(stringResource(R.string.update_start))
        }
      }
    },
    dismissButton = {
      Button(onClick = onUpdateClose) {
        Text(stringResource(if (updateAvailable) R.string.cancel else R.string.close))
      }
    }
  )