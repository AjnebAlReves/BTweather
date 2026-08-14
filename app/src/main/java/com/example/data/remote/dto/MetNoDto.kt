package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetNoResponse(
    val properties: MetNoProperties?
)

@JsonClass(generateAdapter = true)
data class MetNoProperties(
    val timeseries: List<MetNoTimeseries>?
)

@JsonClass(generateAdapter = true)
data class MetNoTimeseries(
    val time: String?,
    val data: MetNoData?
)

@JsonClass(generateAdapter = true)
data class MetNoData(
    val instant: MetNoInstant?,
    @Json(name = "next_1_hours") val next1Hours: MetNoForecastPeriod?,
    @Json(name = "next_6_hours") val next6Hours: MetNoForecastPeriod?,
    @Json(name = "next_12_hours") val next12Hours: MetNoForecastPeriod?
)

@JsonClass(generateAdapter = true)
data class MetNoInstant(
    val details: MetNoInstantDetails?
)

@JsonClass(generateAdapter = true)
data class MetNoInstantDetails(
    @Json(name = "air_pressure_at_sea_level") val airPressure: Double?,
    @Json(name = "air_temperature") val airTemperature: Double?,
    @Json(name = "relative_humidity") val relativeHumidity: Double?,
    @Json(name = "wind_speed") val windSpeed: Double?,
    @Json(name = "wind_from_direction") val windDirection: Double?,
    @Json(name = "ultraviolet_index_clear_sky") val uvIndex: Double?
)

@JsonClass(generateAdapter = true)
data class MetNoForecastPeriod(
    val summary: MetNoSummary?,
    val details: MetNoPeriodDetails?
)

@JsonClass(generateAdapter = true)
data class MetNoSummary(
    @Json(name = "symbol_code") val symbolCode: String?
)

@JsonClass(generateAdapter = true)
data class MetNoPeriodDetails(
    @Json(name = "probability_of_precipitation") val probPrecipitation: Double?,
    @Json(name = "air_temperature_max") val tempMax: Double?,
    @Json(name = "air_temperature_min") val tempMin: Double?
)
