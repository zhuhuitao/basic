package com.atoto.update.base

import kotlinx.coroutines.flow.Flow


/**
 * author:huitao
 */
abstract class DownloadEngin {
    abstract fun download(url: String, apkName: String): Flow<DownloadStatus>

    abstract fun cancel()

    abstract fun release()
}