package com.example.workwithmenu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.util.Locale


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main
            , menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("Locale", "when")
        return when (item.itemId) {
            R.id.action_language_english-> {
                Log.d("Locale", "English language selected")
                setLocale("en")
                true
            }
            R.id.action_language_russian -> {
                Log.d("Locale", "Russian language selected")
                setLocale("ru")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


//    private fun setLocale(languageCode: String) {
//        Log.d("Locale", "Changing locale to $languageCode")
//        val locale = Locale(languageCode)
//        Locale.setDefault(locale)
//        val configuration = Configuration(resources.configuration)
//        configuration.setLocale(locale)
//        resources.updateConfiguration(configuration, resources.displayMetrics)
//        Log.d("Locale", "Locale changed to ${locale.language}")
//        recreate()
//    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.setLocale(locale)
        baseContext.resources.updateConfiguration(
            configuration,
            baseContext.resources.displayMetrics
        )
        recreate()
    }


    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val apiKey = "0cae94396adb869c5bc9e251426292be"
                val weatherURL =
                    "https://api.openweathermap.org/data/2.5/weather?q=Irkutsk&appid=$apiKey&units=metric"
                val stream = URL(weatherURL).openStream()
                val data = stream.bufferedReader().use { it.readText() }

                val gson = Gson()
                val weatherData = gson.fromJson(data, WeatherData::class.java)


                val temperature = weatherData.main.temp
                val feelsLike = weatherData.main.feels_like
                val windSpeed = weatherData.wind.speed
                val description = weatherData.weather.firstOrNull()?.description

                val temperatureTextView: TextView = findViewById(R.id.temperatureTextView)
                val feelsLikeTextView: TextView = findViewById(R.id.feelsLikeTextView)
                val windSpeedTextView: TextView = findViewById(R.id.windSpeedTextView)
                val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)

                val windSpeedText = "$windSpeed ${getString(R.string.speed)}"
                val temperatureText = "$temperature °C"
                val feelsLikeText = "$feelsLike °C"

                val iconCode = when (description) {
                    "thunderstorm with light rain",
                    "thunderstorm with rain",
                    "thunderstorm with heavy rain",
                    "light thunderstorm",
                    "thunderstorm",
                    "heavy thunderstorm",
                    "ragged thunderstorm",
                    "thunderstorm with light drizzle",
                    "thunderstorm with drizzle",
                    "thunderstorm with heavy drizzle" -> "11d"

                    "light intensity drizzle",
                    "drizzle",
                    "heavy intensity drizzle",
                    "light intensity drizzle rain",
                    "drizzle rain",
                    "heavy intensity drizzle rain",
                    "shower rain and drizzle",
                    "heavy shower rain and drizzle",
                    "shower drizzle",
                    "light intensity shower rain",
                    "shower rain",
                    "heavy intensity shower rain",
                    "ragged shower rain" -> "09d"

                    "light rain",
                    "moderate rain",
                    "heavy intensity rain",
                    "very heavy rain",
                    "extreme rain" -> "10d"

                    "freezing rain",
                    "light snow",
                    "snow",
                    "heavy snow",
                    "sleet",
                    "light shower sleet",
                    "shower sleet",
                    "light rain and snow",
                    "rain and snow",
                    "light shower snow",
                    "shower snow",
                    "heavy shower snow" -> "13d"

                    "mist",
                    "smoke",
                    "haze",
                    "sand/dust whirls",
                    "fog",
                    "sand",
                    "dust",
                    "volcanic ash",
                    "squalls",
                    "tornado" -> "50d"

                    "clear sky" -> "01d"
                    "few clouds" -> "02d"
                    "scattered clouds" -> "03d"
                    "broken clouds",
                    "overcast clouds" -> "04d"

                    else -> {
                        "01d"
                    }
                }
                val imageUrl = "https://openweathermap.org/img/w/$iconCode.png"

                val imageView: ImageView = findViewById(R.id.imageView)

                runOnUiThread {
                    temperatureTextView.text = temperatureText
                    feelsLikeTextView.text = feelsLikeText
                    windSpeedTextView.text = windSpeedText
                    descriptionTextView.text = description

                    Glide.with(this@MainActivity)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}