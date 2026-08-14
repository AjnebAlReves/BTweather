package com.example.data.repository

import com.example.domain.model.WorldClockItem

object PredefinedCities {
    val list = listOf(
        WorldClockItem(
            id = "asu",
            cityName = "Asunción",
            countryName = "Paraguay",
            timezoneId = "America/Asuncion",
            latitude = -25.2637,
            longitude = -57.5759
        ),
        WorldClockItem(
            id = "seoul",
            cityName = "Seoul",
            countryName = "South Korea",
            timezoneId = "Asia/Seoul",
            latitude = 37.5665,
            longitude = 126.9780
        ),
        WorldClockItem(
            id = "london",
            cityName = "London",
            countryName = "United Kingdom",
            timezoneId = "Europe/London",
            latitude = 51.5074,
            longitude = -0.1278
        ),
        WorldClockItem(
            id = "olathe",
            cityName = "Olathe",
            countryName = "United States",
            timezoneId = "America/Chicago",
            latitude = 38.8814,
            longitude = -94.8191
        ),
        WorldClockItem(
            id = "newyork",
            cityName = "New York",
            countryName = "United States",
            timezoneId = "America/New_York",
            latitude = 40.7128,
            longitude = -74.0060
        ),
        WorldClockItem(
            id = "tokyo",
            cityName = "Tokyo",
            countryName = "Japan",
            timezoneId = "Asia/Tokyo",
            latitude = 35.6762,
            longitude = 139.6503
        ),
        WorldClockItem(
            id = "paris",
            cityName = "Paris",
            countryName = "France",
            timezoneId = "Europe/Paris",
            latitude = 48.8566,
            longitude = 2.3522
        ),
        WorldClockItem(
            id = "sydney",
            cityName = "Sydney",
            countryName = "Australia",
            timezoneId = "Australia/Sydney",
            latitude = -33.8688,
            longitude = 151.2093
        ),
        WorldClockItem(
            id = "dubai",
            cityName = "Dubai",
            countryName = "United Arab Emirates",
            timezoneId = "Asia/Dubai",
            latitude = 25.2048,
            longitude = 55.2708
        ),
        WorldClockItem(
            id = "madrid",
            cityName = "Madrid",
            countryName = "Spain",
            timezoneId = "Europe/Madrid",
            latitude = 40.4168,
            longitude = -3.7038
        ),
        WorldClockItem(
            id = "buenosaires",
            cityName = "Buenos Aires",
            countryName = "Argentina",
            timezoneId = "America/Argentina/Buenos_Aires",
            latitude = -34.6037,
            longitude = -58.3816
        )
    )
}
