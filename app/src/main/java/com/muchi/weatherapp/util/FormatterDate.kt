package com.muchi.weatherapp.util

import java.text.SimpleDateFormat
import java.util.*

fun formatterDate(stringDate: String) : Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    return formatter.parse(stringDate) as Date
}

fun formatterDate(date: Date, format: String): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(date)
}