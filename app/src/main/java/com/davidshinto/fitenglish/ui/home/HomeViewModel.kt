package com.davidshinto.fitenglish.ui.home

import androidx.lifecycle.ViewModel
import com.davidshinto.fitenglish.weather.WeatherResponse

class HomeViewModel : ViewModel() {
    var response: WeatherResponse? = null
}