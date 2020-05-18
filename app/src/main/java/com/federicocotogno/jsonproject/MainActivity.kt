package com.federicocotogno.jsonproject

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.federicocotogno.jsonproject.Constants.Companion.API_KEY
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

import java.net.URL


class MainActivity : AppCompatActivity() {

    private val TAG = "Main"
    private var cityEntered = "Copenhagen"

    private var weatherImageCode = "04d"
    private var descriptionWeather = ""
    private var tempMax = ""
    private var tempMin = ""
    private var mainWeather = ""
    private var currentTemp = ""
    private var feelsLikeTemp = ""
    var apiResult = "https://api.openweathermap.org/data/2.5/weather?q=copenhagen&units=metric&appid=$API_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isConnected()) {

            returnJsonWeather()
            updateUI()
            updateCityName()

        } else {
            Toast.makeText(this, "You need internet to use this app", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isConnected() : Boolean{

        return true
    }

    private fun updateCityName() {

        btn_confirm_city.setOnClickListener {

            if (et_enter_city != null && et_enter_city.text.isNotEmpty()) {
                et_enter_city.hideKeyboard()
                try {
                    cityEntered = et_enter_city.text.toString()
                    returnJsonWeather()


                } catch (e: Exception) {
                    Toast.makeText(this, "City not recognised", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun returnJsonWeather() {

            GlobalScope.launch(Dispatchers.IO) {

                try {

                    val apiResult =
                        URL("https://api.openweathermap.org/data/2.5/weather?q=$cityEntered&units=metric&appid=$API_KEY").readText()
                    Log.d("Request", apiResult)

                    val jsonObject = JSONObject(apiResult)
                    val main = jsonObject.getJSONObject("main")
                    tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                    tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                    currentTemp = "Current Temp: " + main.getString("temp") + "°C"
                    feelsLikeTemp = "Feels like: " + main.getString("feels_like") + "°C"

                    val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
                    mainWeather = "Now: " + weather.getString("main")
                    descriptionWeather = weather.getString("description")
                    weatherImageCode = weather.getString("icon")

                    Log.i(TAG, main.toString())
                    Log.i(TAG, weather.toString())
                    Log.i(TAG, "---------------------------------------------------------------")
                    Log.i(TAG, tempMax)
                    Log.i(TAG, tempMin)
                    Log.i(TAG, currentTemp)
                    Log.i(TAG, mainWeather)
                    Log.i(TAG, descriptionWeather)
                    Log.i(TAG, weatherImageCode)
                    Log.i(TAG, "---------------------------------------------------------------")

                withContext(Dispatchers.Main) {
                    updateUI()
                }

                }  catch (e: Exception) {
                    apiResult = "https://api.openweathermap.org/data/2.5/weather?q=copenhagen&units=metric&appid=$API_KEY"
                    Log.e(TAG, "Error in returnJsonWeather()")
                }
            }

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {

        try {
            Glide.with(this).load("https://openweathermap.org/img/wn/$weatherImageCode@2x.png")
                .into(iv_status_icon)
            tv_weather_status.text =
                "$mainWeather ($descriptionWeather)\n" + "\n$currentTemp" + "\n$feelsLikeTemp"

            tv_city_title.text = cityEntered

        } catch (e: Exception) {
            Log.e(TAG, "Error in updateUI()")
        }
    }
}




