package com.ngoni.yourweatherx

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getForecast()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun getForecast() {
        //todo: url still here for testing
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
                val jsonObject =JSONObject(jsonData)

                runOnUiThread(Runnable {
                    updateDisplay(jsonObject)
                })
            }

        })

    }

}
