package com.example.weatherapp

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val isNetworkAvailable: Boolean
        get() = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                val networkCapabilities = activeNetwork?: return false
                val actNw = getNetworkCapabilities(networkCapabilities)?: return false
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }else{
                activeNetworkInfo?.run {
                    type == ConnectivityManager.TYPE_WIFI ||
                            type == ConnectivityManager.TYPE_ETHERNET||
                            type == ConnectivityManager.TYPE_MOBILE

                } ?: false
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnShowWeather.setOnClickListener {

            if(!isNetworkAvailable){
                Toast.makeText(this, "Kein Netzwerk verfügbar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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