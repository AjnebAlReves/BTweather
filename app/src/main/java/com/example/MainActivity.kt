package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.main.MainWeatherScreen
import com.example.ui.screens.search.AdvancedCitySearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.widgets.WidgetStudioScreen
import com.example.ui.screens.worldclock.WorldClockScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WeatherViewModel

object AppRoutes {
    const val WEATHER_MAIN = "weather_main"
    const val CITY_SEARCH = "city_search"
    const val WORLD_CLOCK = "world_clock"
    const val WIDGET_STUDIO = "widget_studio"
    const val SETTINGS = "settings"
}

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WeatherAppNavigation(viewModel = weatherViewModel)
            }
        }
    }
}

@Composable
fun WeatherAppNavigation(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.WEATHER_MAIN,
        modifier = modifier.fillMaxSize(),
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
        }
    ) {
        composable(AppRoutes.WEATHER_MAIN) {
            MainWeatherScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateToSearch = { navController.navigate(AppRoutes.CITY_SEARCH) },
                onNavigateToWorldClock = { navController.navigate(AppRoutes.WORLD_CLOCK) },
                onNavigateToWidgets = { navController.navigate(AppRoutes.WIDGET_STUDIO) },
                onNavigateToSettings = { navController.navigate(AppRoutes.SETTINGS) }
            )
        }

        composable(AppRoutes.CITY_SEARCH) {
            AdvancedCitySearchScreen(
                viewModel = viewModel,
                uiState = uiState,
                onCitySelected = { city ->
                    viewModel.selectCityAndAdd(city)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.WORLD_CLOCK) {
            WorldClockScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateToSearch = { navController.navigate(AppRoutes.CITY_SEARCH) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.WIDGET_STUDIO) {
            WidgetStudioScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
