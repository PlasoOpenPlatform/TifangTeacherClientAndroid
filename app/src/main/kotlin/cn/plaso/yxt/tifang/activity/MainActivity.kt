package cn.plaso.yxt.tifang.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import cn.plaso.yxt.base.event.SyncEvent
import cn.plaso.yxt.tifang.LoginActivity
import cn.plaso.yxt.tifang.R
import cn.plaso.yxt.tifang.databinding.TifangActivityMainBinding
import cn.plaso.yxt.tifang.util.GetTokenHelper
import cn.plaso.yxt.tifang.util.LoginUtil
import cn.plaso.yxt.tifang.util.SignHelper
import cn.plaso.yxt.util.logger.YXTLog
import cn.plaso.yxt.yxtsdk.AccountManager
import cn.plaso.yxt.yxtsdk.YxtSDK
import org.json.JSONObject

/**
 * 有4个场景按钮的功能页，需继承base
 */
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var mBinding: TifangActivityMainBinding

    companion object {
        const val LOGINNAME = "1866yyxs"
    }

    private var loginName: String? = null
    private var userType: String? = null
    private var mTifangatoken: String? = null
    private var mBsToken: String? = null
    private var mShowName: String? = null
    private var mUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginName = intent.getStringExtra(LoginUtil.LOGIN_NAME) ?: LOGINNAME

        userType = intent.getStringExtra(LoginUtil.USER_TYPE) ?: LoginUtil.USER_TYPE_TEACHER
        mTifangatoken = intent.getStringExtra(LoginUtil.TOKEN_TIFANG) ?: ""
        mBsToken = intent.getStringExtra(LoginUtil.TOKEN_BS) ?: ""
        mShowName = intent.getStringExtra(LoginUtil.LOGIN_SHOW_NAME) ?: ""
        mUserId = intent.getStringExtra(LoginUtil.USER_ID) ?: ""

        // TODO: 待用户直接传token，取消注释
//        if (mTifangatoken.isNullOrEmpty()) {
//            Log.d(TAG, "mTifangatoken is null, MainActivity finish. ")
//            finish()
//        } else if (mBsToken.isNullOrEmpty()) {
//            Log.d(TAG, "bsToken is null, MainActivity finish. ")
//            finish()
//        }

        mBinding = TifangActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()

//        YxtSDK.updateToken( mBsToken!!, userType!!)

    }

    private fun initView() {
        mBinding.rlLive.setOnClickListener {
            YxtSDK.startLiveClass(this)
        }

        mBinding.rlHomework.setOnClickListener {
            YxtSDK.startHomework(this)
        }
        mBinding.tvLogout.setOnClickListener {
            LoginUtil.logout(
                token = mTifangatoken!!,
                onSuccess = {
                    it.body?.let { responseBody ->
                        val responseData = responseBody.string()
                        Log.d(TAG, "响应数据: $responseData")

                        try {
                            val jsonResponse = JSONObject(responseData)
                            if (jsonResponse.getBoolean("success")) {
                                Log.i(TAG, "退出登录成功")
                                runOnUiThread {
                                    YxtSDK.doLogout()
                                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                }
                            } else {
                                val message = jsonResponse.getString("message")
                                runOnUiThread {
                                    // 显示错误信息
                                    Log.e(TAG, "退出登录失败: $message")
                                    Toast.makeText(this@MainActivity, R.string.err_logout, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "解析退出登录数据失败", e)
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, R.string.err_logout, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                onFailure = {

                })

        }
        mBinding.tvUserName.text = mShowName

    }

    /**
     * 仅可通过退出登录返回到登录页面
     */
    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }
}