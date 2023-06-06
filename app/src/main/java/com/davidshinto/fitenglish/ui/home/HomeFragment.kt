package com.davidshinto.fitenglish.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.davidshinto.fitenglish.MainActivity
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.databinding.FragmentHomeBinding
import com.davidshinto.fitenglish.utils.startAnimation
import com.davidshinto.fitenglish.weather.Main
import com.davidshinto.fitenglish.weather.OpenWeatherMapService
import com.davidshinto.fitenglish.weather.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel


    private lateinit var openWeatherMapService: OpenWeatherMapService
    private var response: WeatherResponse? = null
    private val apiKey: String = "f865bdba4d9f60097d33af706da74cd7"
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val celsiusIcon = "\u2103"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupPlayButton()
        lifecycleScope.launch {
            setupWeather()
        }
        return root
    }

    private fun setupPlayButton() {
        val explosionAnim = getExplosionAnim()
        binding.fab.setOnClickListener {
            binding.fab.isVisible = false
            binding.elipse.isVisible = true
            binding.elipse.startAnimation(explosionAnim) {
                this.context?.let { _ ->
                    it.findNavController()
                        .navigate(R.id.action_navigation_home_to_navigation_game_conf)
                }
            }
        }
    }

    private fun getExplosionAnim(): Animation {
        val animation =
            AnimationUtils.loadAnimation(this.context, R.anim.elipse_explosion_anim).apply {
                duration = 400
                interpolator = AccelerateDecelerateInterpolator()
            }
        return animation
    }

    private suspend fun setupWeather() {
        withContext(Dispatchers.Main) {
            val weatherData = homeViewModel.response
            if (weatherData != null) {
                setDataInCvWeather(weatherData)
            } else {
                getWeatherFromApi()
            }
        }
    }

    private fun setDataInCvWeather(weatherData: WeatherResponse){
        binding.tvTemp.text =
            weatherData.main.temp.minus(273.15).roundToInt().toString() + " $celsiusIcon"
        binding.tvDescription.text =
            weatherData.weather[0].description
        binding.cvWeather.visibility = View.VISIBLE
    }

    private suspend fun getWeatherFromApi() {
        var location = (activity as MainActivity).getLocationFromApi()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        openWeatherMapService = retrofit.create(OpenWeatherMapService::class.java)

        response = openWeatherMapService.getWeather(
            location!!.latitude,
            location!!.longitude,
            apiKey
        ).body()

        withContext(Dispatchers.Main) {
            response?.let { setDataInCvWeather(it) }
        }
        homeViewModel.response = response
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}