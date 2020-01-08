package com.example.MadWeek2.Retrofit;

import com.example.MadWeek2.Image_Delete_Info;
import com.example.MadWeek2.Image_Download_Info;
import com.example.MadWeek2.Image_Upload_Info;

import java.util.ArrayList;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageService {
    @Multipart
    @POST("upload/{salt}")
    Call<Image_Upload_Info> uploadImages(@Path("salt") String salt, @Part MultipartBody.Part file);

    @GET("delete/{salt}/{name}")
    Call<Image_Delete_Info> deleteImages(@Path("salt") String salt, @Path("name") String name);

    @GET("download/{salt}")
    Call<ArrayList<Image_Download_Info>> downloadImages(@Path("salt") String salt);
}
