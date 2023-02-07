package com.example.weatherapp

import java.net.URL
import javax.net.ssl.HttpsURLConnection

object WeatherUtil {

    private val BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q={0}&lang=de&appid={1}"



    fun getFromServer(urlString:String):String{
        val url = URL(urlString)
        var jsonString: String = "{}"
        val connection = url.openConnection() as HttpsURLConnection
        val responseCode = connection.responseCode
        if(responseCode == HttpsURLConnection.HTTP_OK){
            jsonString = connection.inputStream.bufferedReader().use {
                it.readText()
            }
        }
        connection.disconnect()

        return  jsonString
    }
}