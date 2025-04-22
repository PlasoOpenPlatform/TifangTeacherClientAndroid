package cn.plaso.yxt.tifang.util

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager

object RequestUtil {
    private val TAG = RequestUtil::class.java.simpleName

    @JvmStatic
    fun makeRequest(
        url: String,
        method: String = "POST",
        headers: Map<String, String>? = null,
        body: Any? = null,
        onSuccess: (response: Response) -> Unit,
        onFailure: (e: IOException) -> Unit
    ) {
        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager())) // 支持credentials
            .build()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        var requestBody: RequestBody? = "".toRequestBody(mediaType) // 默认空 body

        body?.let {
            requestBody = when (it) {
                is String -> it.toRequestBody(mediaType)
                is JSONObject -> it.toString().toRequestBody(mediaType)
                is Map<*, *> -> JSONObject(it).toString().toRequestBody(mediaType)
                else -> null
            }
        }

        val request = Request.Builder()
            .url(url)
            .method(method, requestBody)
            .apply {
                headers?.forEach { (key, value) ->
                    addHeader(key, value)
                }
            }
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "请求失败", e)
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onFailure(IOException("Unexpected code $response"))
                    return
                }
                onSuccess(response)
            }
        })
    }
}