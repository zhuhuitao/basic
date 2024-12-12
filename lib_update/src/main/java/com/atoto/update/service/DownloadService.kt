package com.atoto.update.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.atoto.update.R
import com.atoto.update.base.DownloadStatus
import com.atoto.update.config.Constant
import com.atoto.update.installation.InstallationModel
import com.atoto.update.installation.InstallationEngin
import com.atoto.update.installation.IntentInstall
import com.atoto.update.installation.SilentInstall
import com.atoto.update.listener.OnDownloadListener
import com.atoto.update.manager.DownloadManger
import com.atoto.update.manager.HttpDownloadManager
import com.atoto.update.utils.ApkUtil
import com.atoto.update.utils.FileUtil
import com.atoto.update.utils.NotificationUtil
import com.atoto.update.utils.getDownloadManger
import com.atoto.update.utils.id2String
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File


/**
 * author:huitao
 */
private const val TAG = "DownloadService"

class DownloadService : Service(), OnDownloadListener {
    private lateinit var downloadManger: DownloadManger
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var lastProgress = 0
    override fun onBind(intent: Intent?): IBinder? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY
        }
        init()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun init() {
        try {
            downloadManger = getDownloadManger()
            FileUtil.createDirDirectory(downloadManger.downloadPath)
            val enable = NotificationUtil.notificationEnable(this@DownloadService)
            downloadManger.logger.d(TAG, "Notification switch status opened:$enable")
            if (checkApkMd5()) {
                downloadManger.logger.i(TAG, "Apk is already exit,then install it directly")
                //apk is exit
                onDone(File(downloadManger.downloadPath, downloadManger.apkName))
            } else {
                downloadManger.logger.i(TAG, "Apk is not exit,then start download it")
                startDownload()
            }
        } catch (e: Exception) {
            downloadManger.logger.e(TAG, "$e")
        }

    }

    @Synchronized
    private fun startDownload() {
        if (downloadManger.isDownloading) {
            downloadManger.logger.i(
                TAG,
                "Please don't download again,the state is already downloading"
            )
            return
        }
        if (downloadManger.httpDownloadManger == null) {
            downloadManger.httpDownloadManger =
                HttpDownloadManager(downloadManger.downloadPath, downloadManger.logger)
        }
        scope.launch(Dispatchers.Main) {
            downloadManger.httpDownloadManger?.download(
                downloadManger.apkUrl,
                downloadManger.apkName
            )?.collect { state ->
                when (state) {
                    DownloadStatus.Cancel -> onCancel()
                    is DownloadStatus.Done -> onDone(state.apk)
                    is DownloadStatus.Downloading -> onProgress(state.progress, state.max)
                    is DownloadStatus.Error -> onError(state.e)
                    DownloadStatus.Start -> onStart()
                }
            }
        }
        downloadManger.isDownloading = true


    }

    private fun checkApkMd5(): Boolean {
        if (downloadManger.apkMD5.isBlank()) {
            return false
        }
        val file = File(downloadManger.downloadPath, downloadManger.apkName)
        if (file.exists()) {
            return FileUtil.md5(file).equals(downloadManger.apkMD5, ignoreCase = true)
        }
        return false
    }

    override fun onStart() {
        if (downloadManger.enableToast) {
            Toast.makeText(this, R.string.app_update_on_background, Toast.LENGTH_SHORT)
                .show()
        }
        if (downloadManger.enableNotification) {
            NotificationUtil.showNotification(
                this,
                downloadManger.smallIcon,
                R.string.app_update_start_download.id2String(),
                R.string.app_update_start_download_hint.id2String()
            )
        }
        downloadManger.onDownloadListeners.forEach {
            it.onStart()
        }
    }

    override fun onProgress(progress: Int, max: Int) {
        if (downloadManger.enableNotification) {
            val curr = (progress / max.toDouble() * 100.0).toInt()
            if (curr == lastProgress) return
            downloadManger.logger.i(TAG, "downloading max: $max --- progress: $progress")
            lastProgress = curr
            val content = if (curr < 0) "" else "$curr%"
            NotificationUtil.showProgressNotification(
                this,
                downloadManger.smallIcon,
                R.string.app_update_downloading.id2String(),
                content,
                if (max == -1) -1 else 100, curr
            )
        }
        downloadManger.onDownloadListeners.forEach {
            it.onProgress(progress, max)
        }
    }

    override fun onDone(file: File) {
        downloadManger.logger.i(TAG, "apk downloaded to ${file.path}")
        downloadManger.isDownloading = false
        if (downloadManger.enableNotification || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NotificationUtil.showDoneNotification(
                this@DownloadService, downloadManger.smallIcon,
                resources.getString(R.string.app_update_download_completed),
                resources.getString(R.string.app_update_click_hint),
                Constant.AUTHORITIES, file
            )
        }
        val installEngin: InstallationEngin? = when (downloadManger.installModel) {
            InstallationModel.SILENT -> {
                downloadManger.logger.i(
                    TAG,
                    "You chose silent model,so install it when download completed."
                )
                SilentInstall()
            }

            InstallationModel.INTENT -> {
                downloadManger.logger.i(
                    TAG,
                    "You chose intent model,so install it when download completed."
                )
                IntentInstall()
            }

            InstallationModel.NOTHING -> {
                downloadManger.logger.i(
                    TAG,
                    """
                    You chose nothing model,so don't install it when download completed,
                    you can listener onDownloadListener to install it.
                    """.trimIndent()
                )
                null
            }

            else -> throw IllegalArgumentException("Not support model: ${downloadManger.installModel}")
        }
        installEngin?.install(this, Constant.AUTHORITIES, file)
        downloadManger.onDownloadListeners.forEach {
            it.onDone(file)
        }
        releaseResources()
    }

    override fun onCancel() {
        downloadManger.logger.i(TAG, "download canceled")
        downloadManger.isDownloading = false
        if (downloadManger.enableNotification) {
            NotificationUtil.cancelNotification(this)
        }

        downloadManger.onDownloadListeners.forEach {
            it.onCancel()
        }
    }

    override fun onError(e: Throwable) {
        downloadManger.logger.i(TAG, "download error: $e")
        downloadManger.isDownloading = false
        if (downloadManger.enableNotification) {
            NotificationUtil.showErrorNotification(
                this@DownloadService, downloadManger.smallIcon,
                resources.getString(R.string.app_update_download_error),
                resources.getString(R.string.app_update_click_download_again)
            )
        }
        downloadManger.onDownloadListeners.forEach {
            it.onError(e)
        }
    }

    private fun releaseResources() {
        downloadManger.release()
        scope.cancel()
        stopSelf()
    }
}