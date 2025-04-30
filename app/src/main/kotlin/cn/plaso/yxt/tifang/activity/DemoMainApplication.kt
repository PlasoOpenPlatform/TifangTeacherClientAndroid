package cn.plaso.yxt.tifang.activity

import android.app.Application
import android.util.Log
import cn.plaso.yxt.yxtsdk.SDKInitCallback
import cn.plaso.yxt.yxtsdk.YxtSDK

class DemoMainApplication : Application() {
    companion object {
        private val TAG : String = DemoMainApplication::class.java.simpleName
    }
    private var mDemoMainApplication: DemoMainApplication? = null

    override fun onCreate() {
        super.onCreate()
        mDemoMainApplication = this
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