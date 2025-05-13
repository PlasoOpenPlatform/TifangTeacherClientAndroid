package cn.plaso.yxt.tifang.activity

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.plaso.yxt.commonui.toast.ToastUtil
import cn.plaso.yxt.tifang.LoginActivity
import cn.plaso.yxt.yxtsdk.SDKCallback
import cn.plaso.yxt.yxtsdk.YxtSDK
import java.util.Collections

class MainApplication : Application() {
    companion object {
        private val TAG : String = MainApplication::class.java.simpleName
    }
    private var mMainApplication: MainApplication? = null
    // 线程安全的 Activity 集合（使用同步容器）
    private val activities = Collections.synchronizedList(mutableListOf<Activity>())
    override fun onCreate() {
        super.onCreate()
        mMainApplication = this
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activities.add(activity) // 记录新创建的 Activity
            }

            override fun onActivityDestroyed(activity: Activity) {
                activities.remove(activity) // 移除已销毁的 Activity
            }

            // 其他生命周期方法留空
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        })
        YxtSDK.init(this, "tifang", object : SDKCallback {
            override fun onInitSuccess() {
                Log.i(TAG, "SDK init success.")
            }
            override fun onInitError(code: Int?) {
                Log.e(TAG, "SDK init failed.")
            }

            override fun onTokenError() {
                Log.e(TAG, "Token Error")
                YxtSDK.doLogout()
                finishAllActivities {
                    val intent = Intent(this@MainApplication, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this@MainApplication.startActivity(intent)
                    ToastUtil.showLong(this@MainApplication, "登录失效")
                }
            }
        })
    }

    /** 关闭所有 Activity（可选参数控制是否杀死进程） */
    private fun finishAllActivities(killProcess: Boolean = false, onFinished: (() -> Unit)? = null) {
        synchronized(activities) {
            activities.forEach { activity ->
                if (!activity.isFinishing && !activity.isDestroyed) {
                    activity.finish() // 逐个关闭 Activity
                }
            }
            activities.clear()
        }
        onFinished?.invoke()
        if (killProcess) android.os.Process.killProcess(android.os.Process.myPid())
    }

    override fun onTerminate() {
        YxtSDK.release(this)
        super.onTerminate()
    }
}