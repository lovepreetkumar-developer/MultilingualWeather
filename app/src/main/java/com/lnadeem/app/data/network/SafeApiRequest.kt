package com.lnadeem.app.data.network

import android.content.Context
import com.lnadeem.app.util.MyApiException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class SafeApiRequest(
    private val context: Context
) {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val error = response.errorBody()?.string()
            val message = StringBuilder()
            error?.let {
                try {
                    message.append(JSONObject(it).getString("message"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            throw MyApiException(message.toString())
        }
    }
}