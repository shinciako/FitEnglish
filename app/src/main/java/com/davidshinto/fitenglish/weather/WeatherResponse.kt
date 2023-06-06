package com.davidshinto.fitenglish.weather

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
)