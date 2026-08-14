package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NominatimSearchResult(
    @Json(name = "place_id") val placeId: Long?,
    val lat: String?,
    val lon: String?,
    @Json(name = "display_name") val displayName: String?,
    val name: String?,
    val address: NominatimAddress?,
    val type: String?,
    val importance: Double?
)

@JsonClass(generateAdapter = true)
data class NominatimAddress(
    val city: String?,
    val town: String?,
    val village: String?,
    val municipality: String?,
    val county: String?,
    val state: String?,
    @Json(name = "state_district") val stateDistrict: String?,
    val country: String?,
    @Json(name = "country_code") val countryCode: String?,
    val postcode: String?
)
