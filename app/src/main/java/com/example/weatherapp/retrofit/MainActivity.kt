package com.example.weatherapp.retrofit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val BASE_URL_STRING = "https://api.openweathermap.org/data/2.5/"
    val IMAGE_URL_STRING = "https://openweathermap.org/img/w/"
    val FILE_EXT = ".png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var weatherDataDef : Deferred<WeatherData>?
        var weatherData : WeatherData?
        var image: Bitmap?
        var imageCall: Call<ResponseBody>

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_STRING)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        binding.btnShowWeather.setOnClickListener {
            val apiService = retrofit.create(WeatherEndpointInterface::class.java)

            val city = binding.etCity.text.toString()
            val appid = getString(R.string.api_key)
            val call = apiService.getWeatherData(city, id =  appid)

            call.enqueue(object : Callback<WeatherData> {
                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                   runBlocking {
                       weatherDataDef = async{response.body()!!}
                       weatherData = weatherDataDef!!.await()

                       imageCall = apiService.loadImage("$IMAGE_URL_STRING${weatherData!!.info[0].icon}$FILE_EXT")
                       binding.tvDesc.text = weatherData?.info?.get(0)?.description ?: "keine Beschreibung"
                       binding.tvTemp.text = weatherData?.temp?.temp?.minus(273.15)?.toInt().toString()

                       imageCall.enqueue(object : Callback <ResponseBody>{
                           override fun onResponse(
                               call: Call<ResponseBody>,
                               response: Response<ResponseBody>
                           ) {
                               runBlocking {
                                   var defImage = async {
                                       BitmapFactory.decodeStream(response.body()?.byteStream())
                                   }
                                   image = defImage.await()
                                   binding.imgWeather.setImageBitmap(image)
                               }
                           }

                           override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                               Toast.makeText(this@MainActivity, "Fehler beim Abruf des Bildes", Toast.LENGTH_SHORT).show()
                           }
                       })
                   }

                }

                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Fehler beim Abruf der Daten", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "onFailure: ", t)
                }
            })
        }
    }
}