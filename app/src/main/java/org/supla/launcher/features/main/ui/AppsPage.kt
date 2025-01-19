package org.supla.launcher.features.main.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.supla.launcher.BuildConfig
import org.supla.launcher.R
import org.supla.launcher.features.Settings
import org.supla.launcher.features.main.Application
import org.supla.launcher.ui.theme.Distance
import org.supla.launcher.ui.theme.SuplaLauncherTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppsPage(
  navController: NavController,
  applications: List<Application>,
  onAppClick: ((Application) -> Unit)? = null
) {
  val scrollState = ScrollState(0)
  Box(modifier = Modifier.fillMaxSize()) {
    FlowRow(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(bottom = 80.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      applications.forEach { ApplicationView(it, onAppClick) }
    }

    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(Distance.normal)
    ) {
      Version(modifier = Modifier.align(Alignment.Center))

      Icon(
        Icons.Filled.Settings,
        contentDescription = stringResource(R.string.settings),
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .clickable { navController.navigate(Settings) },
        tint = MaterialTheme.colorScheme.onPrimary
      )
    }

  }
}

@Composable
private fun Version(modifier: Modifier = Modifier) =
  Text(
    text = stringResource(id = R.string.version, BuildConfig.VERSION_NAME),
    modifier = modifier,
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onPrimary,
  )

@Composable
private fun ApplicationView(application: Application, onAppClick: ((Application) -> Unit)?) =
  Column(
    modifier = Modifier
      .padding(Distance.small)
      .clickable { onAppClick?.let { it(application) } },
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    application.iconLoader()?.let {
      Image(
        bitmap = it.toBitmap().asImageBitmap(),
        contentDescription = application.name,
        modifier = Modifier
          .size(48.dp)
          .padding(4.dp)
      )
    } ?: Icon(
      imageVector = Icons.Outlined.HideSource,
      contentDescription = application.name,
      modifier = Modifier
        .size(48.dp)
        .padding(4.dp)
    )

    Text(
      text = application.name,
      modifier = Modifier.widthIn(max = 48.dp),
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.bodySmall,
      maxLines = 1,
      textAlign = TextAlign.Center
    )
  }

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF12A71E)
@Composable
private fun Preview() {
  SuplaLauncherTheme {
    AppsPage(
      rememberNavController(),
      listOf(
        Application("", "App1") { null },
        Application("", "App2") { null }
      )
    )
  }
}