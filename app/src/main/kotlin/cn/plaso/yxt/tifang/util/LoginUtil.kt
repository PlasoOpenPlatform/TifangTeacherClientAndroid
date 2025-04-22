package cn.plaso.yxt.tifang.util

import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object LoginUtil {
    private val TAG = LoginUtil::class.simpleName
    const val LOGIN_NAME = "loginName"
    const val LOGIN_SHOW_NAME = "showName"
    const val TOKEN = "token"
    const val TOKEN_TIFANG = "token_tifang"
    const val TOKEN_BS = "token_bs"
    const val USER_TYPE = "userType"
    const val USER_ID = "userId"
    const val USER_TYPE_TEACHER = "teacher"
    const val USER_TYPE_STUDENT = "student"

    fun login(loginId: String, password: String, onSuccess: (Response) -> Unit, onFailure: (IOException) -> Unit) {
        val url = "https://teacher-api.tifangedu.com/user/pwdLoginv2"
        val body = JSONObject().apply {
            put("loginId", loginId)
            put("password", password)
        }

        val headers = mapOf(
            "accept" to "application/json, text/plain, */*",
            "client" to "teacher",
            "content-type" to "application/json",
            "source" to "teacher"
        )

        RequestUtil.makeRequest(
            url,
            body = body,
            headers = headers,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun logout(token: String, onSuccess: (Response) -> Unit, onFailure: (IOException) -> Unit) {
        val url = "https://teacher-api.tifangedu.com/user/logout"

        val headers = mapOf(
            "accept" to "application/json, text/plain, */*",
            "authorization" to "Bearer $token",
            "client" to "teacher",
            "source" to "teacher"
        )

        RequestUtil.makeRequest(
            url,
            method = "POST",
            body = null,
            headers = headers,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

}