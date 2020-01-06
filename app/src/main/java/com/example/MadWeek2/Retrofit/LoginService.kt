package com.example.MadWeek2.Retrofit

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {
    @POST("register")
    @FormUrlEncoded
    fun registerUser (@Field ("email") email:String,
                      @Field ("password") password:String,
                      @Field ("name") name:String) : Observable<String>

    @POST("login")
    @FormUrlEncoded
    fun loginUser (@Field ("email") email:String,
                      @Field ("password") password:String) : Observable<String>

    @POST("pre-login")
    @FormUrlEncoded
    fun preloginUser (@Field ("salt") salt:String) : Observable<String>
}