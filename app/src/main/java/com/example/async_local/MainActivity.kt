package com.example.async_local

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val queue = VolleyRequest.getInstance(this).requestQueue

        button_request.setOnClickListener {
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
                        header["Authorization"] = "KakaoAK " + REST_API_KEY
                        return header
                    }

                }

            )

        }
    }
}