package com.atoto.update.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.os.Build
import android.os.SystemClock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class InstallResultReceiver(context: Context?) : BroadcastReceiver() {

    companion object {
        private val ACTION: String =
            InstallResultReceiver::class.java.getName() + SystemClock.elapsedRealtimeNanos()
    }

    private var mContext: Context? = null


    /**
     * 用于将异步转同步
     */
    private val mCountDownLatch: CountDownLatch = CountDownLatch(1)
    private var mSuccess = false
    fun getIntentSender(): IntentSender {
        return PendingIntent.getBroadcast(
            mContext,
            ACTION.hashCode(),
            Intent(ACTION).setPackage(mContext?.packageName),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_ONE_SHOT
        ).intentSender
    }

    init {
        mContext = context!!.applicationContext
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        intentFilter.addDataScheme("package")
        mContext?.registerReceiver(this, intentFilter)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (intent == null) {
                mSuccess = false
            } else {
                val packageName = intent.data?.schemeSpecificPart
                mSuccess = packageName != null
            }
        } finally {
            mCountDownLatch.countDown()
        }
    }


    @Throws(InterruptedException::class)
    fun isSuccess(): Boolean {
        return try {
            //安装最长等待2分钟.
            mCountDownLatch.await(2L, TimeUnit.MINUTES)
            mSuccess
        } finally {
            mContext!!.unregisterReceiver(this)
        }
    }

}