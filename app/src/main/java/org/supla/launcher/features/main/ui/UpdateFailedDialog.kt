package org.supla.launcher.features.main.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.supla.launcher.R

@Composable
fun UpdateFailedDialog(
  onClose: () -> Unit,
) =
  AlertDialog(
    onDismissRequest = onClose,
    title = { Text(stringResource(R.string.update_title)) },
    text = { Text(stringResource(R.string.update_failed)) },
    confirmButton = {
      Button(onClick = onClose) {
        Text(stringResource(R.string.close))
      }
    },
  )