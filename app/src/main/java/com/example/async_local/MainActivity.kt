package com.example.async_local

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Runnable
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        job = Job()         // For coroutines

        button_clear.setOnClickListener {
            edittext_address.text.clear()
            textview_result.text = ""
        }

        val queue = VolleyRequest.getInstance(this).requestQueue

        button_request_volley.setOnClickListener {
            Toast.makeText(it.context, edittext_address.text, Toast.LENGTH_SHORT).show()
            val address = edittext_address.text.toString()
            val url = set_url_with_addr(address)
            Log.d("Async_Local", url)
            VolleyRequest.getInstance(it.context).addToRequestQueue(

                object : StringRequest(Method.GET, set_url_with_addr(address),
                    Response.Listener<String> {
                        textview_result.text = it
                        Log.d("Async_Local", it.toString())
                    }, Response.ErrorListener {
                        Log.e("Async_Local", it.toString())
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val header: MutableMap<String, String> = HashMap()
                        header["Authorization"] = "KakaoAK " + Constants.REST_API_KEY
                        return header
                    }
                }
            )
        }

        /**
         * Handler() without parameter's been deprecated:
         * You're supposed to explicitly indicate which looper to use.
         * Otherwise, it might cause the handler to terminate as it couldn't expect a new job,
         * or any other problems such as race conditions, etc.
         */
        val mHandler = Handler(Looper.getMainLooper())
        button_request_thread.setOnClickListener {
            Thread(Runnable {
                val address = edittext_address.text.toString()

                val url = URL(set_url_with_addr(address))
                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "KakaoAK " + Constants.REST_API_KEY)

                try {
                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                        val stringBuilder = StringBuilder()
                        bufferedReader.forEachLine {
                            stringBuilder.append(it)
                        }
                        val readstream = stringBuilder.toString()
                        //runOnUiThread {
                        //    textview_result.text = readstream
                        //}
                        mHandler.post(Runnable { textview_result.text = readstream })

                        Log.d("HttpURLConn", readstream)
                    }
                } finally {
                    conn.disconnect()
                }

            }).start()
        }

        button_request_retrofit.setOnClickListener {
            Thread(Runnable {
                val address = edittext_address.text.toString()
                val call = RetrofitRequest().getLocalServiceRetrofit(address)
                val response = call.execute()

                if (response.isSuccessful) {
                    Log.d("Async", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    //    runOnUiThread {
                    //        textview_result.text = response.body().toString()
                    //    }

                    mHandler.post(Runnable {
                        if (response.body()?.documents?.size == 0) {
                            textview_result.text = "Failed to get response"
                            return@Runnable
                        }

                        val res = response.body()?.documents?.get(0)
                        if (res != null) {
                            textview_result.text = if (res.road_address != null) res.road_address.address_name else res.address_name
                        } else textview_result.text = "Failed to retrieve address info with Retrofit"
                    }
                    )
                }

            }).start()
        }

        button_request_coroutine.setOnClickListener {
            val address = edittext_address.text.toString()
            val url = URL(set_url_with_addr(address))
            requestByCoroutineHttp(url)
        }

        button_request_coroutine_retrofit.setOnClickListener {
            requestByCoroutineRetrofit()
        }

        button_request_java_http.setOnClickListener {
            Thread(Runnable {
                val result = RequestJava(edittext_address.text.toString()).requestHttpJava()
                mHandler.post(Runnable { textview_result.text = result })

            }).start()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("search_result", textview_result.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val result = savedInstanceState.getString("search_result")
        textview_result.text = result
    }

    private fun requestByCoroutineHttp(url: URL) = GlobalScope.launch(Dispatchers.Main + job) {
        suspend fun request(): String = withContext(Dispatchers.IO) {
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "KakaoAK " + Constants.REST_API_KEY)

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                val stringBuilder = StringBuilder()
                bufferedReader.forEachLine {
                    stringBuilder.append(it)
                }
                stringBuilder.toString()
            } else {
                "Failed to load data"
            }
        }

        val result = request()
        textview_result.text = result
    }

    private fun requestByCoroutineRetrofit() = GlobalScope.launch (Dispatchers.Main + job) {
        suspend fun request(): String = withContext(Dispatchers.IO) {
            val address: String = edittext_address.text.toString()
            val response =
                RetrofitRequest().getLocalServiceRetrofit(address).execute()

            if (response.isSuccessful) {
                response.body().toString()
            } else "Failed to retrieve data."
        }
        textview_result.text = request()
    }
}

