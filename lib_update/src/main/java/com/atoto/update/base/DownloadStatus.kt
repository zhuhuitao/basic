package com.atoto.update.base

import java.io.File

/**
 * author:huitao
 */
sealed class DownloadStatus{
    data object Start : DownloadStatus()

    data class Downloading(val max: Int, val progress: Int) : DownloadStatus()

    class Done(val apk: File) : DownloadStatus()

    data object Cancel : DownloadStatus()

    data class Error(val e: Throwable) : DownloadStatus()
}
