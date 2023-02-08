package com.example.weatherapp.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

// "https://api.openweathermap.org/data/2.5/weather?q={0}&lang=de&appid={1}"

interface WeatherEndpointInterface {

    @GET("weather?")
    fun getWeatherData(
        @Query("q") city:String,
        @Query("lang") lang:String ="de",
        @Query("appid") id:String
    ) : Call<WeatherData>

    @GET
    @Streaming
    fun loadImage(@Url url:String): Call<ResponseBody>
}