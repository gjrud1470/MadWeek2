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

    @GET("contact_download/:")
    @FormUrlEncoded
    fun contact_download (@Url url: String) : Observable<String>

    @POST("contact_update_num")
    @FormUrlEncoded
    fun contact_update_number (@Field("salt") salt:String,
                               @Field("contact_number") contact_number:String) : Observable<String>
}