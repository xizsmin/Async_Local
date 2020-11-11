package com.example.async_local

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

val BASE_URL = "https://dapi.kakao.com/"

data class LocalServiceResponse (
    val documents: List<Any>,
    val meta: Any//Dictionary<Any, Any>
)

interface LocalService {
    @Headers("Authorization: KakaoAK " + Constants.REST_API_KEY)
    @GET("v2/local/search/address.json")
    fun getAddrInfo(@Query("query") keyword:String): Call<LocalServiceResponse>
}


class RetrofitRequest {
    lateinit var localService: LocalService
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        localService = retrofit.create(LocalService::class.java)

    }

    fun getLocalServiceRetrofit(param: String): Call<LocalServiceResponse> {
        return localService.getAddrInfo(param)
    }
}


