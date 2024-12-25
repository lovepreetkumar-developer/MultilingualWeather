package com.lnadeem.app.view_models

import androidx.lifecycle.ViewModel
import com.lnadeem.app.data.respositories.CommonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val mainRepository: CommonRepository
) : ViewModel() {

    suspend fun getTodayWeather(
        queryMap: HashMap<String, String>
    ) = withContext(Dispatchers.IO) {
        mainRepository.getTodayWeather(queryMap)
    }

    suspend fun getHourlyWeather(
        queryMap: HashMap<String, String>
    ) = withContext(Dispatchers.IO) {
        mainRepository.getHourlyWeather(queryMap)
    }

}