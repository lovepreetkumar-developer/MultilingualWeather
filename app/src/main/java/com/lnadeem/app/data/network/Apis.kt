package com.lnadeem.app.data.network

import com.google.gson.GsonBuilder
import com.lnadeem.app.models.HourlyWeatherResponse
import com.lnadeem.app.models.WeatherResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

interface Apis {

    @GET("weather")
    suspend fun getTodayWeather(
        @QueryMap queryMap: HashMap<String, String>
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getHourlyWeather(
        @QueryMap queryMap: HashMap<String, String>
    ): Response<HourlyWeatherResponse>

    companion object {
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): Apis {

            val okHttpClient =
                OkHttpClient.Builder()
                    .addInterceptor(networkConnectionInterceptor)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .build()

            val gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder().client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
                .create(Apis::class.java)
        }
    }
}