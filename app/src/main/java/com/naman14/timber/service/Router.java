package com.naman14.timber.service;

import com.naman14.timber.models.Smsdata;
import com.naman14.timber.streaming.SitusArray;
import com.naman14.timber.streaming.SitusList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Router {
    @GET("list/{id}")
    Call<SitusArray> Situslist(@Path("id") int Id);
    @POST("sms/{id}")
    Call<Smsdata> Postsms(@Body Smsdata a,
                          @Path("id") int Id);

}