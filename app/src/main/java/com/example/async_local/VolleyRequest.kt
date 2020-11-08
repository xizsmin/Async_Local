// 1. Volley + Rest API

package com.example.async_local

import com.android.volley.*
import android.content.Context
import com.android.volley.toolbox.Volley

class VolleyRequest constructor(context: Context){
    companion object {
        private var instance: VolleyRequest? = null
        fun getInstance(context: Context) = instance?: synchronized(this) {
            instance ?: VolleyRequest(context).also {
                instance = it
            }
        }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun<T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}