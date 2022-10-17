package com.muchi.weatherapp.domain.repository

import com.muchi.weatherapp.domain.util.Resource
import com.muchi.weatherapp.domain.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo>
}