package org.supla.launcher.features.main.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import org.supla.launcher.features.main.Application
import org.supla.launcher.ui.theme.Distance

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppsPage(
  applications: List<Application>,
  onAppClick: ((Application) -> Unit)? = null
) {
  val scrollState = ScrollState(0)
  Row {
    Spacer(modifier = Modifier.weight(1f))
    FlowRow(
      modifier = Modifier.verticalScroll(scrollState),
    ) {
      applications.forEach { application ->
        Column(
          modifier = Modifier
            .padding(Distance.small)
            .clickable { onAppClick?.let { it(application) } }
        ) {
          application.iconLoader()?.let {
            Image(
              bitmap = it.toBitmap().asImageBitmap(),
              contentDescription = application.name,
              modifier = Modifier.size(48.dp)
            )
          }
          Text(
            text = application.name,
            modifier = Modifier.widthIn(max = 48.dp),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            textAlign = TextAlign.Center
          )
        }
      }
    }
    Spacer(modifier = Modifier.weight(1f))
  }

}