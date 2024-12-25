package com.lnadeem.app.data.respositories

import android.content.Context
import com.lnadeem.app.data.network.Apis
import com.lnadeem.app.data.network.SafeApiRequest
import com.lnadeem.app.models.HourlyWeatherResponse
import com.lnadeem.app.models.WeatherResponse

class CommonRepository(
    context: Context,
    private val api: Apis
) : SafeApiRequest(context) {

    suspend fun getTodayWeather(
        queryMap: HashMap<String, String>
    ): WeatherResponse {
        return apiRequest { api.getTodayWeather(queryMap) }
    }

    suspend fun getHourlyWeather(
        queryMap: HashMap<String, String>
    ): HourlyWeatherResponse {
        return apiRequest { api.getHourlyWeather(queryMap) }
    }

}