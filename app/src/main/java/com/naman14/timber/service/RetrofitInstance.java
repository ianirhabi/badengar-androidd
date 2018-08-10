package com.naman14.timber.service;

/**
 * Created By Programmer Jalan on January 2018
 */

import android.content.Context;
import android.util.Log;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.naman14.timber.sesion.SessionManager;

import static com.naman14.timber.service.Config.URL;

public class RetrofitInstance  {
    private static Context mContext;
    private static Retrofit retrofit;
    private  static SessionManager sesi;
    private static  String token;
    public RetrofitInstance(){

    }

    public static Retrofit getRetrofitInstanceall() {


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {

            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .build();
                return chain.proceed(newRequest);
            }})
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        return retrofit;
    }

    public static Retrofit getRetrofitInstance() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}