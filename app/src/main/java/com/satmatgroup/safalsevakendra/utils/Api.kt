package com.satmatgroup.safalsevakendra.utils

import com.satmatgroup.safalsevakendra.authentication.SendOTPResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @POST("send_otp")
    fun sendOTPService(@Query("number") number: String,@Query("opt") opt: String,@Query("userid") userid: String): Call<SendOTPResponse?>?


    companion object {
        //this is the URL of the paytm folder that we added in the server
        //make sure you are using your ip else it will not work
        const val BASE_URL = "https://spindiapan.com/api/"
    }
}
