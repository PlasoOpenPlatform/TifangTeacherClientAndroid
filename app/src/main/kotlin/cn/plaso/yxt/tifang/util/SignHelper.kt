package cn.plaso.yxt.tifang.util

import java.math.BigInteger
import java.nio.charset.Charset
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SignHelper {

    /**
     * Sign the [params] with specific [signKey]
     */
    fun sign(params: MutableMap<String, Any>, signKey: String): String {
        val keys = params.keys.sorted()
        val sortedParams = StringBuilder()
        for ((index, key) in keys.withIndex()) {
            if (index != 0) {
                sortedParams.append("&")
            }
            sortedParams.append("$key=${params[key]}")
        }

        val signature = encrypt(sortedParams.toString(), signKey)

        sortedParams.append("&signature=$signature")
        params["signature"] = signature;
        return sortedParams.toString()

    }

    private fun encrypt(encryptText: String, signKey: String): String {
        val algorithm = "HmacSHA1"
        val charset = Charset.forName("UTF-8")
        val data = signKey.toByteArray(charset)

        val secretKey = SecretKeySpec(data, algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(secretKey)
        val rst = mac.doFinal(encryptText.toByteArray(charset))

        return BigInteger(1, rst).toString(16).uppercase()

    }

}