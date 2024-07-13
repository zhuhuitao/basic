package com.huitao.ext.view

import android.os.SystemClock
import android.view.View
import android.view.View.OnClickListener

/**
 * author:huitao
 */
class ClickListener(private val interval: Long, private val action: (view: View) -> Unit) :
    OnClickListener {
    private var lastClickTime = 0L
    override fun onClick(v: View) {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= interval) {
            lastClickTime = currentTime
            action.invoke(v)
        }
    }
}