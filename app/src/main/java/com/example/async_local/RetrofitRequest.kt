package com.example.async_local

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

val BASE_URL = "https://dapi.kakao.com/"

data class RoadAddress (
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val road_name: String,
    val underground_yn: String,
    val main_building_no: String,
    val sub_building_no: String,
    val building_name: String,
    val zone_no: String,
    val x: Double,
    val y: Double
)

data class Documents (
    val address: Any,
    val address_name: String,
    val address_type: String,
    val road_address: RoadAddress,
    val x: Double,
    val y: Double
)

data class LocalServiceResponse (
    val documents: List<Documents>,
    val meta: Any//Dictionary<Any, Any>
)
/*
interface LocalService {
    @Headers("Authorization: KakaoAK " + Constants.REST_API_KEY)
    @GET("v2/local/search/address.json")
    fun getAddrInfo(@Query("query") keyword:String): Call<LocalServiceResponse>
}
*/

interface RetrofitAPI {
    @GET("v2/local/search/address.json")
    fun getInfo(@Header("Authorization") api_key: String, @Query("query") keyword: String): Call<LocalServiceResponse>
}

class RetrofitRequest constructor() {
    //var localService: LocalService
    var localService: RetrofitAPI
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        localService = retrofit.create(RetrofitAPI::class.java)
    }

    fun getLocalServiceRetrofit(param: String): Call<LocalServiceResponse> {
        return localService.getInfo("KakaoAK " + Constants.REST_API_KEY, param)
    }
}


