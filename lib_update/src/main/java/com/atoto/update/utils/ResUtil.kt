package com.atoto.update.utils

import android.app.Application
import com.atoto.update.manager.DownloadManger

/**
 * author:huitao
 */

internal fun Int.id2String(): String {
    return getApplication().getString(this)
}

internal fun getDownloadManger(): DownloadManger {
    return DownloadManger.getInstance(null)
        ?: throw NullPointerException("DownloadManger don't init")
}

internal fun getApplication(): Application {
    return getDownloadManger().application
}


