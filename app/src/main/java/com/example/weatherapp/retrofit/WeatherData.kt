package com.example.weatherapp.retrofit


import com.google.gson.annotations.SerializedName


class WeatherData(
    @SerializedName("name") val city:String,
    @SerializedName("main") val temp:Temp,
    @SerializedName("weather") val info:Array<Info>
)

class Temp(temp:Double)

class Info(val description:String, icon:String)