package com.example.data.remote

import com.example.data.remote.dto.MetNoResponse
import com.example.data.remote.dto.OpenMeteoGeocodingResponse
import com.example.data.remote.dto.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OpenMeteoApiService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m,wind_direction_10m,surface_pressure",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,precipitation_probability,is_day",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_probability_max,uv_index_max",
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse

    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") cityName: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): OpenMeteoGeocodingResponse
}

interface MetNoApiService {
    @GET("weatherapi/locationforecast/2.0/compact")
    suspend fun getLocationForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("User-Agent") userAgent: String = "OneWeatherApp/1.0 (ajnebalreves@gmail.com)"
    ): MetNoResponse
}

interface NominatimApiService {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 15,
        @Header("User-Agent") userAgent: String = "OneWeatherApp/1.0 (ajnebalreves@gmail.com)"
    ): List<com.example.data.remote.dto.NominatimSearchResult>

    @GET("reverse")
    suspend fun reverse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Header("User-Agent") userAgent: String = "OneWeatherApp/1.0 (ajnebalreves@gmail.com)"
    ): com.example.data.remote.dto.NominatimSearchResult
}
