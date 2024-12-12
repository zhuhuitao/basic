package com.huitao.basics

import android.util.Log
import com.atoto.update.listener.LogInterceptor

/**
 * author:huitao
 */
private const val TAG = "MyLoggerAdapter"
class MyLoggerAdapter : LogInterceptor() {
    override fun d(tag: String, msg: String) {
        Log.d("$TAG:$tag", msg)
    }

    override fun e(tag: String, msg: String) {
        Log.e("$TAG:$tag", msg)
    }

    override fun i(tag: String, msg: String) {
        Log.i("$TAG:$tag", msg)
    }

    override fun w(tag: String, msg: String) {
        Log.w("$TAG:$tag", msg)
    }

    override fun v(tag: String, msg: String) {
        Log.v("$TAG:$tag", msg)
    }
}