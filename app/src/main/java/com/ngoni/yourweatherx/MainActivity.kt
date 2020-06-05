package com.ngoni.yourweatherx


import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getForecast()
        }
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

        if (GenUtil.isNetworkAvailable(applicationContext)) {

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

                fun updateDisplay(jsonData: JSONObject) {
                    val main = jsonData.getJSONObject("main")
                    temperatureLabel.setText(main.get("temp").toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonData = response.body!!.string()
                    val jsonObject = JSONObject(jsonData)

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


