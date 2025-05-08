package cn.plaso.yxt.tifang

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.plaso.yxt.tifang.activity.MainActivity
import cn.plaso.yxt.tifang.databinding.TifangActivityLoginBinding
import cn.plaso.yxt.tifang.util.GetTokenHelper
import cn.plaso.yxt.tifang.util.LoginUtil
import cn.plaso.yxt.tifang.util.SignHelper
import cn.plaso.yxt.yxtsdk.EnvManager
import cn.plaso.yxt.yxtsdk.SDKInitCallback
import cn.plaso.yxt.yxtsdk.YxtSDK
import okhttp3.OkHttpClient
import org.json.JSONObject

/**
 * 登录页
 */
class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.simpleName
    private lateinit var mBinding: TifangActivityLoginBinding
    private val client = OkHttpClient()
    private lateinit var handler: Handler
    private var mUserType: String = LoginUtil.USER_TYPE_TEACHER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = TifangActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.checkBoxTeacher.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            mBinding.checkBoxStudent.isChecked = !checked
        }

        mBinding.checkBoxStudent.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            mBinding.checkBoxTeacher.isChecked = !checked
        }
        mBinding.btLogin.text = EnvManager.getEnvString(this)

        mBinding.btLogin.setOnClickListener {
            val username = mBinding.etUsername.text.toString()
//            if (EnvManager.isEnv(username)) {
//                EnvManager.setEnv(username)
//                Log.d(TAG, "切换环境: $username")
//                System.exit(0)
//            } else {
//                // 测试账号
//                if (mBinding.etPwd.text.isNullOrEmpty()) {
//                    mUserType =
//                        if (mBinding.checkBoxTeacher.isChecked) LoginUtil.USER_TYPE_TEACHER else LoginUtil.USER_TYPE_STUDENT
//                    loginGetToken()
//                } else {
//                    // 梯方账号
                    login(username, mBinding.etPwd.text.toString())
//                }
//            }
        }

    }


    private fun login(loginId: String, password: String){
        LoginUtil.login(
            loginId = loginId,
            password = password,
            onSuccess = {
                it.body?.let { responseBody ->
                    val responseData = responseBody.string()
                    Log.d(TAG, "响应数据: $responseData")

                    try {
                        val jsonResponse = JSONObject(responseData)
                        if (jsonResponse.getBoolean("success")) {
                            val data = jsonResponse.getJSONObject("data")
                            val tifangToken = data.getString("token")
                            val showName = data.getString("showName")
                            val bsToken = data.getString("bstoken")
                            val userId = data.getString("bsid")

                            runOnUiThread {
                                Log.d(TAG, "登录成功，用户名: $showName")
                                YxtSDK.updateToken(bsToken, mUserType, object : SDKInitCallback {
                                    override fun onInitSuccess() {
//                                        Toast.makeText(this@LoginActivity, "onInitSuccess", Toast.LENGTH_SHORT).show()
                                        startActivity(
                                            Intent(this@LoginActivity, MainActivity::class.java)
                                                .putExtra(LoginUtil.TOKEN_BS, bsToken)
                                                .putExtra(LoginUtil.TOKEN_TIFANG, tifangToken)
                                                .putExtra(LoginUtil.LOGIN_SHOW_NAME, showName)
                                                .putExtra(LoginUtil.USER_ID, userId)
                                        )
                                    }

                                    override fun onInitError(code: Int?) {
                                        Toast.makeText(this@LoginActivity, R.string.err_user_info, Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        } else {
                            val message = jsonResponse.getString("message")
                            runOnUiThread {
                                // 显示错误信息
                                Log.e(TAG, "登录失败: $message")
                                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "解析响应数据失败", e)
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, R.string.err_login, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onFailure = {
                Log.e(TAG, "请求失败.", it)
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, R.string.err_login, Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * 待用户直接传token，此方法可以删除
     */
    private fun loginGetToken(){
        handler = Handler(Looper.getMainLooper())
        Thread {
            val queryMap = mutableMapOf<String, Any>()
            queryMap["appId"] = EnvManager.getAppId()
            queryMap["validTime"] = 1200
            queryMap["validBegin"] = System.currentTimeMillis() / 1000
            queryMap["loginName"] = mBinding.etUsername.text.toString()
            queryMap["userType"] = mUserType
            val signedQuery = SignHelper.sign(queryMap, EnvManager.getSignKey())

            println("signedQuery: $signedQuery")

            val resp =
                GetTokenHelper.getToken(
                    url = "https://${EnvManager.getServerUrl()}/dataentry/user/getToken",
                    queryMap
                )

            if (resp.isSuccessful) {
                val data = resp.body?.string()

                Log.d(TAG, "MainActivity $data")

                try {
                    if (data != null) {
                        JSONObject(data).run {
                            val obj = optJSONObject("obj")
                            val code = obj?.optInt("code")
                            val token = obj?.optString("token")
//                            Log.d(TAG, "code :$code token $token")
                            if (code != 0) {
                                handler.post {
                                    Toast.makeText(this@LoginActivity, R.string.err_user_info, Toast.LENGTH_SHORT
                                    ).show()
                                }
                                Log.e(TAG, "error: code: $code, data: ${data}")

                            } else {
                                handler.post {
                                    Log.d(TAG, "initToken: $token")
                                    YxtSDK.updateToken( token!!, mUserType, object : SDKInitCallback {
                                        override fun onInitSuccess() {
                                            startActivity(
                                                Intent(this@LoginActivity, MainActivity::class.java)
                                                    .putExtra(LoginUtil.TOKEN_BS, token)
                                                    .putExtra(LoginUtil.LOGIN_NAME, mBinding.etUsername.text.toString())
                                                    .putExtra(LoginUtil.USER_TYPE, mUserType)
                                            )
                                        }

                                        override fun onInitError(code: Int?) {
                                            Toast.makeText(this@LoginActivity, R.string.err_user_info, Toast.LENGTH_SHORT).show()
                                        }
                                    })
//                                    initToken(token)
                                }
                            }

                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            } else {

            }
        }.start()
    }
}