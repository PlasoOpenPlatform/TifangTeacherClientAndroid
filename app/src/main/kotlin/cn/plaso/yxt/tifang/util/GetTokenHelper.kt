package cn.plaso.yxt.tifang.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

object GetTokenHelper {

    private val okHttpClient = OkHttpClient.Builder().build()

    fun getToken(
        url: String,
        queryMap: MutableMap<String, Any>
    ): Response {
        val json = JSONObject()
        try {
            json.put("loginName", queryMap["loginName"])
            json.put("userType", queryMap["userType"])
            json.put("appId", queryMap["appId"])
            json.put("validTime", queryMap["validTime"])
            json.put("validBegin", queryMap["validBegin"])
            json.put("signature", queryMap["signature"])

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        return okHttpClient.newCall(request).execute()
    }
}