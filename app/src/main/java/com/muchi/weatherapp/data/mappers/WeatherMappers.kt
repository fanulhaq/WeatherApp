package com.muchi.weatherapp.data.mappers

import com.muchi.weatherapp.data.remote.WeatherDataDto
import com.muchi.weatherapp.data.remote.WeatherDto
import com.muchi.weatherapp.domain.weather.WeatherData
import com.muchi.weatherapp.domain.weather.WeatherInfo
import com.muchi.weatherapp.domain.weather.WeatherType
import com.muchi.weatherapp.util.formatterDate
import java.util.*

private data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData,
)

fun WeatherDataDto.toWeatherDataMap(): Map<Int, List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = formatterDate(time)

        IndexedWeatherData(
            index = index,
            data = WeatherData(
                time = calendar,
                temperatureCelsius = temperature,
                pressure = pressure,
                windSpeed = windSpeed,
                humidity = humidity,
                weatherType = WeatherType.fromWMO(weatherCode)
            )
        )
    }.groupBy {
        it.index / 24
    }.mapValues { mv ->
        mv.value.map { it.data }
    }
}

fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val calendar = Calendar.getInstance(Locale.getDefault())
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = when {
            calendar.get(Calendar.MINUTE) < 30 -> calendar.get(Calendar.HOUR_OF_DAY)
            calendar.get(Calendar.HOUR_OF_DAY) == 23 -> 12
            else -> calendar.get(Calendar.HOUR_OF_DAY) + 1
        }
        it.time.get(Calendar.HOUR_OF_DAY) == hour
    }
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeatherData
    )
}