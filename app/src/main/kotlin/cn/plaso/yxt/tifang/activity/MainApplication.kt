package cn.plaso.yxt.tifang.activity

import android.app.Application
import android.util.Log
import cn.plaso.yxt.yxtsdk.SDKInitCallback
import cn.plaso.yxt.yxtsdk.YxtSDK

class MainApplication : Application() {
    companion object {
        private val TAG : String = MainApplication::class.java.simpleName
    }
    private var mMainApplication: MainApplication? = null

    override fun onCreate() {
        super.onCreate()
        mMainApplication = this
        YxtSDK.init(this, "tifang",  object : SDKInitCallback {
            override fun onInitSuccess() {
                Log.i(TAG, "SDK init success.")
            }
            override fun onInitError(code: Int?) {
                Log.e(TAG, "SDK init failed.")

            }
        })
    }

    override fun onTerminate() {
        YxtSDK.release(this)
        super.onTerminate()
    }
}