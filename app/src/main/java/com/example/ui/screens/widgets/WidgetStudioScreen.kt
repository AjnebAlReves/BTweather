package com.example.ui.screens.widgets

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.WeatherCondition
import com.example.domain.model.WorldClockItem
import com.example.ui.components.OneUiClockCard
import com.example.ui.components.WeatherConditionIcon
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetStudioScreen(
    viewModel: WeatherViewModel,
    uiState: WeatherUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedWidgetTab by remember { mutableIntStateOf(0) } // 0: Dual Clock 4x1, 1: Weather 2x2, 2: Full 4x2
    var widgetTransparency by remember { mutableFloatStateOf(0.85f) }
    var selectedPrimaryIndex by remember { mutableIntStateOf(0) }
    var selectedSecondaryIndex by remember { mutableIntStateOf(1) }

    val cities = uiState.savedCities.ifEmpty { listOf(uiState.primaryDualClock, uiState.secondaryDualClock) }
    val primaryCity = cities.getOrNull(selectedPrimaryIndex.coerceIn(0, cities.size - 1)) ?: uiState.primaryDualClock
    val secondaryCity = cities.getOrNull(selectedSecondaryIndex.coerceIn(0, cities.size - 1)) ?: uiState.secondaryDualClock

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "One UI Widget Studio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* Info */ }) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            // Widget Type Selector Filter Chips
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedWidgetTab == 0,
                        onClick = { selectedWidgetTab = 0 },
                        label = { Text("Dual Clock 4x1") },
                        leadingIcon = if (selectedWidgetTab == 0) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                    FilterChip(
                        selected = selectedWidgetTab == 1,
                        onClick = { selectedWidgetTab = 1 },
                        label = { Text("Weather 2x2") },
                        leadingIcon = if (selectedWidgetTab == 1) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                    FilterChip(
                        selected = selectedWidgetTab == 2,
                        onClick = { selectedWidgetTab = 2 },
                        label = { Text("Full 4x2") },
                        leadingIcon = if (selectedWidgetTab == 2) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Live Widget Preview Surface (simulating home screen wallpaper)
            item {
                Text(
                    text = "HOME SCREEN LIVE PREVIEW",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF1E293B), // Slate wallpaper simulation
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(28.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (selectedWidgetTab) {
                            0 -> {
                                // 4x1 Dual Clock Widget Preview
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OneUiClockCard(
                                        item = primaryCity,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OneUiClockCard(
                                        item = secondaryCity,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            1 -> {
                                // 2x2 Weather Widget Preview
                                WeatherWidgetPreviewCard(
                                    city = uiState.selectedCity,
                                    weather = uiState.weatherData,
                                    tempUnit = uiState.settings.temperatureUnit,
                                    alpha = widgetTransparency
                                )
                            }
                            2 -> {
                                // 4x2 Full Weather & Clock Widget Preview
                                FullWidgetPreviewCard(
                                    city = uiState.selectedCity,
                                    secondaryCity = secondaryCity,
                                    weather = uiState.weatherData,
                                    tempUnit = uiState.settings.temperatureUnit,
                                    alpha = widgetTransparency
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Widget Customization Controls
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Widget Customization",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Transparency Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Background Opacity",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${(widgetTransparency * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = widgetTransparency,
                            onValueChange = { widgetTransparency = it },
                            valueRange = 0.2f..1f
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dual Clock City Switchers
                        if (selectedWidgetTab == 0 || selectedWidgetTab == 2) {
                            Text(
                                text = "City 1 (Left Card): ${primaryCity.cityName}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                cities.take(4).forEachIndexed { idx, c ->
                                    FilterChip(
                                        selected = selectedPrimaryIndex == idx,
                                        onClick = { selectedPrimaryIndex = idx },
                                        label = { Text(c.cityName) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "City 2 (Right Card): ${secondaryCity.cityName}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                cities.take(4).forEachIndexed { idx, c ->
                                    FilterChip(
                                        selected = selectedSecondaryIndex == idx,
                                        onClick = { selectedSecondaryIndex = idx },
                                        label = { Text(c.cityName) }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Pin to Home Screen instructions
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "How to add to Home Screen",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Long press on your launcher home screen, choose Widgets, find 'Weather & Clock' and drag Dual Clock 4x1 or Weather 2x2.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun WeatherWidgetPreviewCard(
    city: WorldClockItem,
    weather: com.example.domain.model.WeatherData?,
    tempUnit: TemperatureUnit,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val currentTemp = weather?.currentTempC ?: 22.0
    val displayTemp = if (tempUnit == TemperatureUnit.FAHRENHEIT) {
        "${((currentTemp * 9 / 5) + 32).toInt()}°"
    } else {
        "${currentTemp.toInt()}°"
    }
    val condition = weather?.condition ?: WeatherCondition.CLEAR_DAY

    Surface(
        shape = RoundedCornerShape(26.dp),
        color = Color.White.copy(alpha = alpha),
        modifier = modifier
            .size(160.dp)
            .shadow(6.dp, RoundedCornerShape(26.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = city.cityName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = condition.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }
                WeatherConditionIcon(
                    condition = condition,
                    isDay = weather?.isDay ?: true,
                    size = 28.dp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = displayTemp,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontSize = 36.sp
                )
                Text(
                    text = "H:${(currentTemp + 4).toInt()}° L:${(currentTemp - 4).toInt()}°",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun FullWidgetPreviewCard(
    city: WorldClockItem,
    secondaryCity: WorldClockItem,
    weather: com.example.domain.model.WeatherData?,
    tempUnit: TemperatureUnit,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val currentTemp = weather?.currentTempC ?: 22.0
    val displayTemp = if (tempUnit == TemperatureUnit.FAHRENHEIT) {
        "${((currentTemp * 9 / 5) + 32).toInt()}°"
    } else {
        "${currentTemp.toInt()}°"
    }

    Surface(
        shape = RoundedCornerShape(26.dp),
        color = Color.White.copy(alpha = alpha),
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(26.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${city.cityName} • $displayTemp",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "${weather?.condition?.label ?: "Clear"} • Feels like $displayTemp",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                }

                WeatherConditionIcon(
                    condition = weather?.condition ?: WeatherCondition.CLEAR_DAY,
                    isDay = true,
                    size = 32.dp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mini dual clock pill row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = city.cityName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                        Text(
                            text = "${city.formattedTime} ${city.formattedAmPm}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E1B4B),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = secondaryCity.cityName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC7D2FE)
                        )
                        Text(
                            text = "${secondaryCity.formattedTime} ${secondaryCity.formattedAmPm}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC7D2FE)
                        )
                    }
                }
            }
        }
    }
}
