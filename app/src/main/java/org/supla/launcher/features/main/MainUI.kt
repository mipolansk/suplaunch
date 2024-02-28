package org.supla.launcher.features.main

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.supla.launcher.BuildConfig
import org.supla.launcher.R
import org.supla.launcher.ui.theme.Distance
import org.supla.launcher.ui.theme.SuplaLauncherTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private val hourFormatter = DateTimeFormatter.ofPattern("HH")
private val minuteFormatter = DateTimeFormatter.ofPattern("mm")
private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd LLLL")

@Composable
fun MainUI(
  onSuplaClick: (() -> Unit)? = null,
  onDownloadClick: (() -> Unit)? = null,
  onOffClick: (() -> Unit)? = null,
  sensorManager: SensorManager? = null
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.primary)
  ) {
    DistanceSensorValue(sensorManager, modifier = Modifier.align(Alignment.TopEnd))

    DayAndHour(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = Distance.big, top = 80.dp)
    )

    Logo(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 80.dp)
        .clickable(
          interactionSource = MutableInteractionSource(),
          indication = rememberRipple(),
          onClick = { onSuplaClick?.let { it() } }
        )
    )

    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(all = Distance.normal)
    ) {
//      DownloadButton(modifier = Modifier.align(Alignment.CenterStart)) { onDownloadClick?.let { it() } }
      Version(modifier = Modifier.align(Alignment.Center))
      OffButton(modifier = Modifier.align(Alignment.CenterEnd)) { onOffClick?.let { it() } }
    }
  }
}

@Composable
private fun Logo(modifier: Modifier = Modifier) =
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(Distance.small),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      painter = painterResource(id = R.drawable.logo),
      contentDescription = stringResource(id = R.string.brand_name),
      modifier = Modifier.size(50.dp)
    )
    Image(
      painter = painterResource(id = R.drawable.brand_name),
      contentDescription = null,
      modifier = Modifier.width(96.dp)
    )
  }

@Composable
private fun DayAndHour(modifier: Modifier = Modifier) {
  var hourString by remember { mutableStateOf("") }
  var minuteString by remember { mutableStateOf("") }
  var dateString by remember { mutableStateOf("") }
  var counter by remember { mutableStateOf(0) }
  var semicolonVisible by remember { mutableStateOf(true) }

  LaunchedEffect(Any()) {
    while (true) {
      val currentTime = LocalDateTime.now()

      hourString = currentTime.format(hourFormatter)
      minuteString = currentTime.format(minuteFormatter)
      dateString = currentTime.format(dateFormatter)
      semicolonVisible = counter < 5

      counter = if (counter < 10) counter + 1 else 0
      delay(100)
    }
  }

  Column(modifier = modifier) {
    Row {
      Text(
        text = hourString,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onPrimary,
      )
      if (semicolonVisible) {
        Text(
          text = ":",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onPrimary,
          textAlign = TextAlign.Center,
          modifier = Modifier.width(8.dp)
        )
      } else {
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = minuteString,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onPrimary,
      )
    }

    Text(
      text = dateString,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier.padding(bottom = 20.dp)
    )
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
private fun DownloadButton(modifier: Modifier = Modifier, onClick: () -> Unit) =
  IconButton(onClick = onClick, modifier = modifier) {
    Icon(
      painter = painterResource(id = R.drawable.ic_download),
      contentDescription = stringResource(id = R.string.update_check),
      tint = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier.size(48.dp)
    )
  }

@Composable
private fun OffButton(modifier: Modifier = Modifier, onClick: () -> Unit) =
  IconButton(onClick = onClick, modifier = modifier) {
    Icon(
      painter = painterResource(id = R.drawable.ic_off),
      contentDescription = stringResource(id = R.string.turn_screen_off),
      tint = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier.size(48.dp)
    )
  }

@Composable
private fun DistanceSensorValue(sensorManager: SensorManager?, modifier: Modifier = Modifier) {
  if (BuildConfig.DEBUG) {
    var position by remember { mutableStateOf(0f) }

    remember {
      val listener = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
          p0?.values?.firstOrNull()?.let { position = it }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
      }
      sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.let { sensor ->
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
      }
      listener
    }


    Text(
      text = "S: $position",
      style = MaterialTheme.typography.bodyMedium,
      modifier = modifier
        .padding(all = Distance.big)
    )
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  SuplaLauncherTheme {
    MainUI()
  }
}