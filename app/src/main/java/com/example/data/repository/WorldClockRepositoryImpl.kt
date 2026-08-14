package com.example.data.repository

import com.example.data.local.dao.ClockCityDao
import com.example.data.local.entity.ClockCityEntity
import com.example.domain.model.WeatherCondition
import com.example.domain.model.WorldClockItem
import com.example.domain.repository.WorldClockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WorldClockRepositoryImpl(
    private val clockCityDao: ClockCityDao
) : WorldClockRepository {

    override fun getSavedClockLocations(): Flow<List<WorldClockItem>> {
        return clockCityDao.getAllClockCities().map { entities ->
            if (entities.isEmpty()) {
                // Return default initial list if empty
                PredefinedCities.list.take(4).map { city ->
                    val (time, amPm, date, diff, isDay) = WeatherRepositoryImpl.calculateTimeDetails(city.timezoneId)
                    city.copy(
                        formattedTime = time,
                        formattedAmPm = amPm,
                        formattedDate = date,
                        timeDiffHours = diff,
                        isDay = isDay,
                        tempC = 21.0,
                        condition = if (isDay) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT
                    )
                }
            } else {
                entities.map { entity ->
                    val (time, amPm, date, diff, isDay) = WeatherRepositoryImpl.calculateTimeDetails(entity.timezoneId)
                    val cond = entity.lastCachedCondition?.let {
                        try { WeatherCondition.valueOf(it) } catch (e: Exception) { null }
                    } ?: (if (isDay) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT)
                    WorldClockItem(
                        id = entity.id,
                        cityName = entity.cityName,
                        countryName = entity.countryName,
                        adminArea = entity.adminArea,
                        countryCode = entity.countryCode,
                        timezoneId = entity.timezoneId,
                        latitude = entity.latitude,
                        longitude = entity.longitude,
                        formattedTime = time,
                        formattedAmPm = amPm,
                        formattedDate = date,
                        timeDiffHours = diff,
                        isDay = isDay,
                        tempC = entity.lastCachedTemp ?: 21.0,
                        condition = cond
                    )
                }
            }
        }
    }

    override suspend fun addClockLocation(item: WorldClockItem) = withContext(Dispatchers.IO) {
        val entity = ClockCityEntity(
            id = item.id,
            cityName = item.cityName,
            countryName = item.countryName,
            adminArea = item.adminArea,
            countryCode = item.countryCode,
            timezoneId = item.timezoneId,
            latitude = item.latitude,
            longitude = item.longitude,
            lastCachedTemp = item.tempC,
            lastCachedCondition = item.condition?.name
        )
        clockCityDao.insertClockCity(entity)
    }

    override suspend fun removeClockLocation(id: String) = withContext(Dispatchers.IO) {
        clockCityDao.deleteClockCityById(id)
    }

    override suspend fun refreshClockTimes(): List<WorldClockItem> = withContext(Dispatchers.IO) {
        val cities = clockCityDao.getAllClockCitiesList()
        val source = if (cities.isEmpty()) {
            PredefinedCities.list.take(4).map { city ->
                ClockCityEntity(
                    id = city.id,
                    cityName = city.cityName,
                    countryName = city.countryName,
                    timezoneId = city.timezoneId,
                    latitude = city.latitude,
                    longitude = city.longitude
                )
            }.also {
                clockCityDao.insertClockCities(it)
            }
        } else {
            cities
        }

        source.map { entity ->
            val (time, amPm, date, diff, isDay) = WeatherRepositoryImpl.calculateTimeDetails(entity.timezoneId)
            WorldClockItem(
                id = entity.id,
                cityName = entity.cityName,
                countryName = entity.countryName,
                timezoneId = entity.timezoneId,
                latitude = entity.latitude,
                longitude = entity.longitude,
                formattedTime = time,
                formattedAmPm = amPm,
                formattedDate = date,
                timeDiffHours = diff,
                isDay = isDay,
                tempC = entity.lastCachedTemp ?: 22.0,
                condition = if (isDay) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT
            )
        }
    }
}
