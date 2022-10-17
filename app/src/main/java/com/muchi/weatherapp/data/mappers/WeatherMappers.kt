package com.muchi.weatherapp.data.mappers

import com.muchi.weatherapp.data.remote.WeatherDataDto
import com.muchi.weatherapp.data.remote.WeatherDto
import com.muchi.weatherapp.domain.weather.WeatherData
import com.muchi.weatherapp.domain.weather.WeatherInfo
import com.muchi.weatherapp.domain.weather.WeatherType
import java.text.SimpleDateFormat
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

private fun formatterDate(stringDate: String) : Date {
    val formatter = SimpleDateFormat("yyyy-MM-ddTHH:mm", Locale.getDefault())
    return formatter.parse(stringDate) as Date
}

/*
fun WeatherDataDto.toWeatherDataMap(): Map<Int, List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]
        IndexedWeatherData(
            index = index,
            data = WeatherData(
                time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
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
*/

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

/*
fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if(now.minute < 30) now.hour else now.hour + 1
        it.time.hour == hour
    }
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeatherData
    )
}
*/