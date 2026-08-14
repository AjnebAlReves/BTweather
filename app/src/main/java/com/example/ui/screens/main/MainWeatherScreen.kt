package com.example.ui.screens.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AlertSeverity
import com.example.domain.model.SevereWeatherAlert
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.WeatherCondition
import com.example.domain.model.WeatherData
import com.example.domain.model.WorldClockItem
import com.example.ui.components.DailyForecastCard
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.HourlySplineChart
import com.example.ui.components.OneUiDualClockSection
import com.example.ui.components.WeatherCompanionArt
import com.example.ui.components.WeatherDetailGrid
import com.example.ui.theme.WeatherGradients
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWeatherScreen(
    viewModel: WeatherViewModel,
    uiState: WeatherUiState,
    onNavigateToSearch: () -> Unit,
    onNavigateToWorldClock: () -> Unit,
    onNavigateToWidgets: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCityPickerSheet by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.detectCurrentLocation(context)
        }
    }

    val weather = uiState.weatherData
    val condition = weather?.condition ?: WeatherCondition.CLEAR_DAY
    val isDay = weather?.isDay ?: true

    val gradientColors = if (uiState.settings.dynamicThemeEnabled) {
        WeatherGradients.getGradientForCondition(condition, isDay)
    } else {
        WeatherGradients.ClearDay
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp)
        ) {
            // 1. Top Bar
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showCityPickerSheet = true },
                        modifier = Modifier.testTag("city_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Cities Menu",
                            tint = Color.White
                        )
                    }

                    // City Title with Dropdown Click
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showCityPickerSheet = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = uiState.selectedCity.cityName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (uiState.selectedCity.isGpsLocation || (weather?.isGpsLocation == true)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0x554CAF50),
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.MyLocation,
                                        contentDescription = "GPS Location Active",
                                        tint = Color(0xFF81C784),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Location",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Action buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier.testTag("gps_locate_button")
                        ) {
                            if (uiState.isLocating) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.MyLocation,
                                    contentDescription = "Locate Me",
                                    tint = Color.White
                                )
                            }
                        }
                        IconButton(
                            onClick = onNavigateToSearch,
                            modifier = Modifier.testTag("search_city_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search City",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { viewModel.refreshCurrentWeather() },
                            modifier = Modifier.testTag("refresh_button")
                        ) {
                            if (uiState.isRefreshing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White
                                )
                            }
                        }
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 2. Severe Weather Alert Banner (Active Alerts)
            if (weather != null && weather.alerts.isNotEmpty()) {
                items(weather.alerts) { alert ->
                    SevereWeatherAlertBanner(
                        alert = alert,
                        onDismiss = { viewModel.dismissAlertNotification(alert.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // 3. Weather Hero Section (Temperature, Condition, Character Illustration)
            item {
                if (weather != null) {
                    WeatherHeroSection(
                        weather = weather,
                        tempUnit = uiState.settings.temperatureUnit
                    )
                } else if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Weather Outlook / Summary Card
            item {
                if (weather != null) {
                    SevereWeatherOutlookCard(summary = weather.summaryText)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 5. Hourly Spline Forecast
            item {
                if (weather != null && weather.hourlyForecast.isNotEmpty()) {
                    HourlySplineChart(
                        hourlyItems = weather.hourlyForecast,
                        tempUnit = uiState.settings.temperatureUnit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 6. 7-Day Forecast Card
            item {
                if (weather != null && weather.dailyForecast.isNotEmpty()) {
                    DailyForecastCard(
                        dailyItems = weather.dailyForecast,
                        tempUnit = uiState.settings.temperatureUnit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 7. Air & Weather Metrics Grid
            item {
                if (weather != null) {
                    WeatherDetailGrid(
                        weather = weather,
                        windUnit = uiState.settings.windSpeedUnit,
                        pressureUnit = uiState.settings.pressureUnit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 8. One UI Dual Clock Section (Interactive preview)
            item {
                OneUiDualClockSection(
                    primaryClock = uiState.primaryDualClock,
                    secondaryClock = uiState.secondaryDualClock,
                    onClockClick = { onNavigateToWorldClock() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 9. Quick Shortcuts (World Clock, Search & Widget Studio)
            item {
                QuickNavRow(
                    onWorldClockClick = onNavigateToWorldClock,
                    onSearchClick = onNavigateToSearch,
                    onWidgetsClick = onNavigateToWidgets
                )
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // City Picker Bottom Sheet
    if (showCityPickerSheet) {
        CityPickerBottomSheet(
            savedCities = uiState.savedCities,
            selectedCity = uiState.selectedCity,
            onCitySelected = { city ->
                viewModel.loadWeatherForCity(city, forceRefresh = false)
                showCityPickerSheet = false
            },
            onLocateGps = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                showCityPickerSheet = false
            },
            onAddNewCity = {
                showCityPickerSheet = false
                onNavigateToSearch()
            },
            onDismiss = { showCityPickerSheet = false }
        )
    }
}

@Composable
fun SevereWeatherAlertBanner(
    alert: SevereWeatherAlert,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bannerColor = when (alert.severity) {
        AlertSeverity.EXTREME -> Color(0xD9D32F2F)
        AlertSeverity.WARNING -> Color(0xD9E65100)
        AlertSeverity.WATCH -> Color(0xD9F57C00)
        AlertSeverity.ADVISORY -> Color(0xD91976D2)
    }

    val borderColor = when (alert.severity) {
        AlertSeverity.EXTREME -> Color(0xFFFF5252)
        AlertSeverity.WARNING -> Color(0xFFFFB74D)
        AlertSeverity.WATCH -> Color(0xFFFFD54F)
        AlertSeverity.ADVISORY -> Color(0xFF64B5F6)
    }

    GlassmorphicCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = bannerColor,
        borderColor = borderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = "Alert",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = alert.severity.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = alert.effectiveTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.95f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Issued by ${alert.sender} • Expires ${alert.expiresTime}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
fun WeatherHeroSection(
    weather: WeatherData,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val displayCurrentTemp = if (tempUnit == TemperatureUnit.FAHRENHEIT) {
        "${((weather.currentTempC * 9 / 5) + 32).toInt()}°"
    } else {
        "${weather.currentTempC.toInt()}°"
    }

    val displayFeelsLike = if (tempUnit == TemperatureUnit.FAHRENHEIT) {
        "${((weather.feelsLikeTempC * 9 / 5) + 32).toInt()}°"
    } else {
        "${weather.feelsLikeTempC.toInt()}°"
    }

    val displayMax = if (tempUnit == TemperatureUnit.FAHRENHEIT) {
        "${((weather.maxTempC * 9 / 5) + 32).toInt()}°"
    } else {
        "${weather.maxTempC.toInt()}°"
    }

    val displayMin = if (tempUnit == TemperatureUnit.FAHRENHEIT) {
        "${((weather.minTempC * 9 / 5) + 32).toInt()}°"
    } else {
        "${weather.minTempC.toInt()}°"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Large Display Temperature
            Text(
                text = displayCurrentTemp,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Light,
                color = Color.White,
                fontSize = 82.sp,
                lineHeight = 84.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Weather condition label
            Text(
                text = weather.condition.label,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Max/Min & Feels Like
            Text(
                text = "$displayMax / $displayMin  •  Feels like $displayFeelsLike",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Provider: ${weather.providerUsed}${if (weather.isGpsLocation) " • GPS Location" else ""}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.65f)
            )
        }

        // Hero Art / Character
        WeatherCompanionArt(
            size = 140.dp
        )
    }
}

@Composable
fun SevereWeatherOutlookCard(summary: String) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x35E65100),
        borderColor = Color(0x55FFA726)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Weather Outlook",
                tint = Color(0xFFFFCC80),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "WEATHER OUTLOOK",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFE0B2),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun QuickNavRow(
    onWorldClockClick: () -> Unit,
    onSearchClick: () -> Unit,
    onWidgetsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .weight(1f)
                .clickable { onWorldClockClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = "World Clock",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "World Clock",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        GlassmorphicCard(
            modifier = Modifier
                .weight(1f)
                .clickable { onSearchClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Cities",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "City Search",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        GlassmorphicCard(
            modifier = Modifier
                .weight(1f)
                .clickable { onWidgetsClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Widgets,
                            contentDescription = "Widgets",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Widgets",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPickerBottomSheet(
    savedCities: List<WorldClockItem>,
    selectedCity: WorldClockItem,
    onCitySelected: (WorldClockItem) -> Unit,
    onLocateGps: () -> Unit,
    onAddNewCity: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Locations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onAddNewCity) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Location")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Location Action in Drawer
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onLocateGps() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = "GPS Location",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Current GPS Location",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Detect and update weather automatically",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                items(savedCities) { city ->
                    val isCurrent = city.cityName == selectedCity.cityName
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrent) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onCitySelected(city) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = city.cityName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = city.countryName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${city.formattedTime} ${city.formattedAmPm}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
