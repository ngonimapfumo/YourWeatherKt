package com.ngoni.yourweatherx


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ngoni.yourweatherx.GenUtil.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), LocationListener {

    var doubleBackToExitPressedOnce = false
    val TAG = "MainActivity.kt"
    var mLocation: Location? = null
    var locationManager: LocationManager? = null
    var genUtil: GenUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000*1,
                2f,
                this
            )
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getForecast()
        }

    }


    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onLocationChanged(location: Location?) {
      //  mLocation = location

       Toast.makeText(applicationContext,""+location?.latitude,Toast.LENGTH_LONG).show()
        println("vvv")

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onProviderEnabled(provider: String?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onProviderDisabled(provider: String?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(applicationContext, "press back to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getForecast() {
        fun updateDisplay(jsonData: JSONObject) {
            val main = jsonData.getJSONObject("main")
            val weatherobj = jsonData.getJSONArray("weather")
            for (x in 0 until weatherobj.length()) {
                val obj_temp = getJsonObject(weatherobj, x)
                summaryText.text = obj_temp.get("description").toString()
            }
            var decimalFormat = DecimalFormat("0")
            temperatureLabel.text = decimalFormat.format(main.get("temp"))
            locationText.text = jsonData.get("name").toString()
        }

        if (isNetworkAvailable(applicationContext)) {

//            val lat = mLocation!!.latitude
            //          val lon = mLocation!!.longitude

            //      Log.d(TAG, "getForecast: $lat+ $lon")

           // Toast.makeText(applicationContext, mLocation?.latitude.toString(), Toast.LENGTH_LONG)
             //   .show()

            val url =
                "https://api.openweathermap.org/data/2.5/weather?lat=-17.89189189189189&lon=30.918717878165474&appid=7337147a8504643a7cab939e6c7b6d18&units=metric"
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            val call = client.newCall(request)
            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonData = response.body!!.string()
                    val jsonObject = JSONObject(jsonData)
                    Log.d(TAG, "onResponse: $jsonData")

                    runOnUiThread(Runnable {
                        updateDisplay(jsonObject)
                    })
                }

            })

        } else {
            Toast.makeText(applicationContext, "no network available", Toast.LENGTH_LONG).show()
        }


    }


}


