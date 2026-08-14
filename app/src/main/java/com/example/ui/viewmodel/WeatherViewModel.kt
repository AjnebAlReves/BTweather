package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WeatherDatabase
import com.example.data.local.datastore.UserSettingsDataStore
import com.example.data.repository.PredefinedCities
import com.example.data.repository.UserSettingsRepositoryImpl
import com.example.data.repository.WeatherRepositoryImpl
import com.example.data.repository.WorldClockRepositoryImpl
import com.example.domain.model.PressureUnit
import com.example.domain.model.SevereWeatherAlert
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.TimeFormatPreference
import com.example.domain.model.UserSettings
import com.example.domain.model.WeatherData
import com.example.domain.model.WeatherProviderType
import com.example.domain.model.WindSpeedUnit
import com.example.domain.model.WorldClockItem
import com.example.util.LocationResult
import com.example.util.LocationServiceManager
import com.example.util.WeatherNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLocating: Boolean = false,
    val locationErrorMessage: String? = null,
    val selectedCity: WorldClockItem = PredefinedCities.list.first { it.cityName == "Olathe" },
    val weatherData: WeatherData? = null,
    val savedCities: List<WorldClockItem> = emptyList(),
    val searchResults: List<WorldClockItem> = emptyList(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val settings: UserSettings = UserSettings(),
    val primaryDualClock: WorldClockItem = PredefinedCities.list.first { it.cityName == "Seoul" },
    val secondaryDualClock: WorldClockItem = PredefinedCities.list.first { it.cityName == "London" }
)

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WeatherDatabase.getInstance(application)
    private val dataStore = UserSettingsDataStore(application)
    private val weatherRepo = WeatherRepositoryImpl(db.weatherDao(), dataStore)
    private val clockRepo = WorldClockRepositoryImpl(db.clockCityDao())
    private val settingsRepo = UserSettingsRepositoryImpl(dataStore)

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Observe settings
        viewModelScope.launch {
            settingsRepo.getSettings().collectLatest { newSettings ->
                _uiState.update { it.copy(settings = newSettings) }
            }
        }

        // Observe saved clock cities
        viewModelScope.launch {
            clockRepo.getSavedClockLocations().collectLatest { cities ->
                val primary = cities.getOrNull(0) ?: _uiState.value.primaryDualClock
                val secondary = cities.getOrNull(1) ?: _uiState.value.secondaryDualClock
                _uiState.update {
                    it.copy(
                        savedCities = cities,
                        primaryDualClock = primary,
                        secondaryDualClock = secondary
                    )
                }
            }
        }

        // Check if location permission is already granted and autoLocate is on
        if (LocationServiceManager.hasLocationPermission(application) && LocationServiceManager.isLocationServiceEnabled(application)) {
            detectCurrentLocation(application)
        } else {
            // Load initial weather for default selected city
            loadWeatherForCity(_uiState.value.selectedCity, forceRefresh = false)
        }

        // Periodic time tick
        viewModelScope.launch {
            while (true) {
                delay(30000L) // 30 seconds clock update
                refreshClockCalculations()
            }
        }
    }

    fun detectCurrentLocation(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocating = true, locationErrorMessage = null) }

            val result = LocationServiceManager.getCurrentLocationAndCity(context)
            when (result) {
                is LocationResult.Success -> {
                    val gpsCity = result.cityItem
                    _uiState.update {
                        it.copy(
                            selectedCity = gpsCity,
                            isLocating = false,
                            locationErrorMessage = null
                        )
                    }
                    loadWeatherForCity(gpsCity, forceRefresh = true)
                }
                is LocationResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLocating = false,
                            locationErrorMessage = result.message
                        )
                    }
                    // Fallback to loading default city if no weather loaded yet
                    if (_uiState.value.weatherData == null) {
                        loadWeatherForCity(_uiState.value.selectedCity, forceRefresh = false)
                    }
                }
            }
        }
    }

    fun clearLocationError() {
        _uiState.update { it.copy(locationErrorMessage = null) }
    }

    fun loadWeatherForCity(city: WorldClockItem, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedCity = city,
                    isLoading = it.weatherData == null,
                    isRefreshing = forceRefresh,
                    errorMessage = null
                )
            }

            val result = weatherRepo.getWeatherData(
                latitude = city.latitude,
                longitude = city.longitude,
                cityName = city.cityName,
                countryName = city.countryName,
                forceRefresh = forceRefresh
            )

            result.onSuccess { data ->
                val finalData = data.copy(isGpsLocation = city.isGpsLocation)
                _uiState.update {
                    it.copy(
                        weatherData = finalData,
                        isLoading = false,
                        isRefreshing = false
                    )
                }

                // Check and trigger notifications if severe alerts exist and notifications are enabled
                if (_uiState.value.settings.severeWeatherAlertsEnabled && finalData.alerts.isNotEmpty()) {
                    for (alert in finalData.alerts) {
                        WeatherNotificationManager.showSevereWeatherAlert(
                            context = getApplication(),
                            alert = alert,
                            soundAndVibrate = _uiState.value.settings.notificationSoundEnabled
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = error.localizedMessage ?: "Failed to update weather"
                    )
                }
            }
        }
    }

    fun refreshCurrentWeather() {
        if (_uiState.value.selectedCity.isGpsLocation && LocationServiceManager.hasLocationPermission(getApplication())) {
            detectCurrentLocation(getApplication())
        } else {
            loadWeatherForCity(_uiState.value.selectedCity, forceRefresh = true)
        }
        viewModelScope.launch {
            clockRepo.refreshClockTimes()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.trim().length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            delay(350L) // debounce
            val res = weatherRepo.searchLocations(query)
            res.onSuccess { list ->
                _uiState.update { it.copy(searchResults = list, isSearching = false) }
            }.onFailure {
                _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            }
        }
    }

    fun selectCityAndAdd(item: WorldClockItem) {
        viewModelScope.launch {
            clockRepo.addClockLocation(item)
            loadWeatherForCity(item, forceRefresh = true)
            _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) }
        }
    }

    fun addCityToTracked(item: WorldClockItem) {
        viewModelScope.launch {
            clockRepo.addClockLocation(item)
        }
    }

    fun removeCity(id: String) {
        viewModelScope.launch {
            clockRepo.removeClockLocation(id)
        }
    }

    fun sendTestAlertNotification() {
        WeatherNotificationManager.showTestAlertNotification(getApplication())
    }

    fun dismissAlertNotification(alertId: String) {
        WeatherNotificationManager.dismissAlert(getApplication(), alertId)
    }

    fun setDualClockSelection(primary: WorldClockItem, secondary: WorldClockItem) {
        _uiState.update {
            it.copy(
                primaryDualClock = primary,
                secondaryDualClock = secondary
            )
        }
    }

    fun updateTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch { settingsRepo.updateTemperatureUnit(unit) }
    }

    fun updateTimeFormat(format: TimeFormatPreference) {
        viewModelScope.launch { settingsRepo.updateTimeFormat(format) }
    }

    fun updateProvider(provider: WeatherProviderType, apiKey: String = "") {
        viewModelScope.launch { settingsRepo.updateProvider(provider, apiKey) }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch { settingsRepo.updateSettings(settings) }
    }

    private fun refreshClockCalculations() {
        val updatedCities = _uiState.value.savedCities.map { city ->
            val (time, amPm, date, diff, isDay) = WeatherRepositoryImpl.calculateTimeDetails(city.timezoneId)
            city.copy(
                formattedTime = time,
                formattedAmPm = amPm,
                formattedDate = date,
                timeDiffHours = diff,
                isDay = isDay
            )
        }
        val (pTime, pAmPm, pDate, pDiff, pIsDay) = WeatherRepositoryImpl.calculateTimeDetails(_uiState.value.primaryDualClock.timezoneId)
        val (sTime, sAmPm, sDate, sDiff, sIsDay) = WeatherRepositoryImpl.calculateTimeDetails(_uiState.value.secondaryDualClock.timezoneId)

        _uiState.update {
            it.copy(
                savedCities = updatedCities,
                primaryDualClock = it.primaryDualClock.copy(
                    formattedTime = pTime,
                    formattedAmPm = pAmPm,
                    formattedDate = pDate,
                    timeDiffHours = pDiff,
                    isDay = pIsDay
                ),
                secondaryDualClock = it.secondaryDualClock.copy(
                    formattedTime = sTime,
                    formattedAmPm = sAmPm,
                    formattedDate = sDate,
                    timeDiffHours = sDiff,
                    isDay = sIsDay
                )
            )
        }
    }
}
