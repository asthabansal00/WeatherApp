package com.asthabansal.weatherapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.asthabansal.weatherapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding

     var cityName:String ="dhaka,bd"
     var apiKey:String ="092851a39f8454646e0f4b767dadfba0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)


        weatherTask().execute()
    }

        inner class weatherTask:AsyncTask<String,Void,String>(){

            //this function will show error in case when data is not loaded

              override fun onPreExecute() {
                super.onPreExecute()
                activityMainBinding.loader.visibility = View.VISIBLE
                activityMainBinding.mainContainerRelativeLayout.visibility = View.GONE
                activityMainBinding.tvError.visibility = View.GONE
            }

            //this will work with the API
            override fun doInBackground(vararg p0: String?): String? {
                var response:String?
                try{
                    response = URL("https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&APPID=$apiKey").
                    readText(Charsets.UTF_8)
                }
                catch(e:Exception) {
                    response = null
                }
                System.out.println("Hello"+response)
                return response
            }


            override fun onPostExecute(result: String?){
                super.onPostExecute(result)

                try{
//                    if(result.isNullOrEmpty()){
//                        throw Exception("Is empty")
//                    }

                    //setting the Api response
                    val jsonObj = JSONObject(result)
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                    val updatedAt:Long = jsonObj.getLong("dt")
                    val updatedText = "Updated at: "+SimpleDateFormat("dd/MM/yyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                    val temp = main.getString("temp")+"°C"
                    val tempMin = "Min temp: "+main.getString("temp_min")+"°C"
                    val tempMax = "Max temp: "+main.getString("temp_max")+"°C"
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")
                    val sunrise:Long = sys.getLong("sunrise")
                    val sunset:Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val weatherDescription = weather.getString("description")
                    val address = jsonObj.getString("name")+", "+sys.getString("cityName")


                    //populating the text views with data
                    activityMainBinding.address.text = address
                    activityMainBinding.updatedAt.text = updatedText
                    activityMainBinding.status.text = weatherDescription.capitalize()
                    activityMainBinding.temp.text = temp
                    activityMainBinding.tempMin.text = tempMin
                    activityMainBinding.tempMax.text = tempMax
                    activityMainBinding.tvSunriseTime.text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunrise*1000))
                    activityMainBinding.tvSunsetTime.text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunset*1000))
                    activityMainBinding.tvWindTime.text = windSpeed
                    activityMainBinding.tvPressureTime.text = pressure
                    activityMainBinding.tvHumidityTime.text = humidity


                    //after setting the text views disable the progress bar and enable the main container
                    activityMainBinding.loader.visibility = View.GONE
                    activityMainBinding.tvError.visibility = View.GONE
                    activityMainBinding.mainContainerRelativeLayout.visibility = View.VISIBLE

                } catch (e:Exception){
                    activityMainBinding.loader.visibility = View.GONE
                    activityMainBinding.tvError.visibility = View.VISIBLE
                }
            }
    }
}