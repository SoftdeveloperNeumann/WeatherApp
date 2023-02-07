package com.example.weatherapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import org.json.JSONObject
import java.net.URL
import java.text.MessageFormat
import javax.net.ssl.HttpsURLConnection

object WeatherUtil {

    private val BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q={0}&lang=de&appid={1}"
    private val TAG = "json"
    private val NAME = "name"
    private val MAIN = "main"
    private val TEMP = "temp"
    private val WEATHER = "weather"
    private val DESCRIPTION = "description"
    private val ICON = "icon"


    fun getWeatherData(city: String, key: String): WeatherData {
        var name = ""
        var description = ""
        var icon = ""
        var temp = 0.0
        val jsonString = getFromServer(MessageFormat.format(BASE_URL, city, key))
        Log.d(TAG, "getWeatherData: $jsonString")
        val rootObject = JSONObject(jsonString)
        if(rootObject.has(NAME))
            name = rootObject.getString(NAME)
        if(rootObject.has(WEATHER)){
            val weatherArray = rootObject.getJSONArray(WEATHER)
            if(weatherArray.length()>0){
                val weatherObject = weatherArray.getJSONObject(0)
                if(weatherObject.has(DESCRIPTION))
                    description = weatherObject.getString(DESCRIPTION)
                if(weatherObject.has(ICON))
                    icon = weatherObject.getString(ICON)
            }

        }
        if (rootObject.has(MAIN)){
            val mainObject = rootObject.getJSONObject(MAIN)
            if (mainObject.has(TEMP))
                temp = mainObject.getDouble(TEMP)
        }
        return WeatherData(name, description, icon, temp)

    }

    private fun getFromServer(urlString: String): String {
        val url = URL(urlString)
        var jsonString: String = "{}"
        val connection = url.openConnection() as HttpsURLConnection
        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            jsonString = connection.inputStream.bufferedReader().use {
                it.readText()
            }
        }
        connection.disconnect()

        return jsonString
    }

    fun getImage(imageName:String): Bitmap? {
        val url = URL("https://openweathermap.org/img/w/$imageName.png")
        val connection = url.openConnection() as HttpsURLConnection
        var bitmap:Bitmap? = null
        if(connection.responseCode == HttpsURLConnection.HTTP_OK) {
            bitmap = BitmapFactory.decodeStream(connection.inputStream)
            connection.disconnect()
        }
        return bitmap
    }
}