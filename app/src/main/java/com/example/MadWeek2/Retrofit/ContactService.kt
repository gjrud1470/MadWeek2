package com.example.MadWeek2.Retrofit

import io.reactivex.Observable
import retrofit2.http.*

interface ContactService {
    @POST("contact_upload")
    @FormUrlEncoded
    fun contact_upload (@Field("salt") salt:String,
                        @Field("id") id:String,
                        @Field("name") name:String,
                        @Field("mobile_number") mobile_number:String,
                        @Field("group") group:String?) : Observable<String>

    @GET("contact_download/{salt}")
    fun contact_download (@Path(value = "salt", encoded = true) salt: String) : Observable<String>

    @POST("contact_update_num")
    @FormUrlEncoded
    fun contact_update_number (@Field("salt") salt:String,
                               @Field("contact_number") contact_number:String) : Observable<String>

    @GET("contact_get_num/{salt}")
    fun contact_get_num (@Path(value = "salt", encoded = true) salt: String) : Observable<String>

    @GET("user_name/{salt}")
    fun get_user_name (@Path(value = "salt", encoded = true) salt: String) : Observable<String>
}