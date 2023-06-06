package com.davidshinto.fitenglish.weather

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)