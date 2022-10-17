package com.muchi.weatherapp.domain.weather

import java.util.*

data class WeatherData(
    val time: Calendar,
    val temperatureCelsius: Double,
    val pressure: Double,
    val windSpeed: Double,
    val humidity: Double,
    val weatherType: WeatherType
)
