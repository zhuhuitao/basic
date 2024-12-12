package com.atoto.update.listener

import java.io.File

/**
 * author:huitao
 */
interface OnDownloadListener {

    fun onStart()

    fun onProgress(progress: Int, max: Int)

    fun onDone(file: File)

    fun onCancel()

    fun onError(e: Throwable)
}