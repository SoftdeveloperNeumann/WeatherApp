package com.example.weatherapp

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnShowWeather.setOnClickListener {
            MainScope().launch {
                val weatherData:WeatherData
                var bitmap:Bitmap? = null
                withContext(Dispatchers.IO){
                    weatherData = WeatherUtil.getWeatherData(binding.etCity.text.toString(),getString(R.string.api_key))
                }
                binding.tvTemp.text = getString(R.string.temp_template,(weatherData.temp-273.15).toInt())
                binding.tvDesc.text = weatherData.description

                if(weatherData.icon.isNotBlank()){
                    withContext(Dispatchers.IO){
                        bitmap = WeatherUtil.getImage(weatherData.icon)
                    }
                }

                binding.imgWeather.setImageBitmap(bitmap)
            }

        }

    }
}