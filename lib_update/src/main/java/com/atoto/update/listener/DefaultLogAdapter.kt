package com.atoto.update.listener

import android.util.Log

/**
 * author:huitao
 */

class DefaultLogAdapter : LogInterceptor() {
    override fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    override fun e(tag: String, msg: String) {
        Log.e(tag, msg)
    }

    override fun i(tag: String, msg: String) {
        Log.i(tag, msg)
    }

    override fun w(tag: String, msg: String) {
        Log.w(tag, msg)
    }

    override fun v(tag: String, msg: String) {
        Log.v(tag, msg)
    }


}