package org.supla.launcher.features

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import org.supla.launcher.features.main.MainScreen
import org.supla.launcher.features.settings.SettingsScreen

@Serializable
object Main

@Serializable
object Settings

@Composable
fun SuplaunchNavigationHost(
  onSuplaLaunch: () -> Unit
) {

  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = Main) {
    composable<Main> { MainScreen(navController, onSuplaClick = onSuplaLaunch) }
    composable<Settings> { SettingsScreen(navController) }
  }
}