package com.huitao.ext.view

import android.view.View

/**
 * author:huitao
 */

fun View.showOrGone(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

fun View.clickNoRepeat(interval: Long = 500, action: (view: View) -> Unit) = setOnClickListener {
    ClickListener(interval, action)
}
