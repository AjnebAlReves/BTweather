package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CachedWeatherEntity
import com.example.data.local.entity.ClockCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM cached_weather WHERE cityName = :cityName LIMIT 1")
    fun getWeatherForCity(cityName: String): Flow<CachedWeatherEntity?>

    @Query("SELECT * FROM cached_weather WHERE cityName = :cityName LIMIT 1")
    suspend fun getWeatherForCityOnce(cityName: String): CachedWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: CachedWeatherEntity)

    @Query("DELETE FROM cached_weather WHERE cityName = :cityName")
    suspend fun deleteWeather(cityName: String)
}

@Dao
interface ClockCityDao {
    @Query("SELECT * FROM clock_cities ORDER BY displayOrder ASC, cityName ASC")
    fun getAllClockCities(): Flow<List<ClockCityEntity>>

    @Query("SELECT * FROM clock_cities ORDER BY displayOrder ASC, cityName ASC")
    suspend fun getAllClockCitiesList(): List<ClockCityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClockCity(city: ClockCityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClockCities(cities: List<ClockCityEntity>)

    @Query("DELETE FROM clock_cities WHERE id = :id")
    suspend fun deleteClockCityById(id: String)

    @Update
    suspend fun updateClockCity(city: ClockCityEntity)
}
