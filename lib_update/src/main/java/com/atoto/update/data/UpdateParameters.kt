package com.atoto.update.data

import android.app.NotificationChannel
import com.atoto.update.base.BaseHttpDownloadManger
import com.atoto.update.installation.InstallationModel
import com.atoto.update.listener.LogInterceptor

/**
 * author:huitao
 */
data class UpdateParameters(
    val apkUrl: String = "",
    val apkName: String = "",
    val downloadPath: String? = null,
    val smallIcon: Int = -1,
    val httpDownloadManger: BaseHttpDownloadManger? = null,
    val logger: LogInterceptor? = null,
    val notificationChannel: NotificationChannel? = null,
    val apkMD5: String = "",
    val enableToast: Boolean = true,
    val enableNotification: Boolean = true,
    val notifyId: Int = -1,
    val versionCode: Int = Int.MIN_VALUE,
    @InstallationModel val installModel: Int = InstallationModel.NOTHING,
    val autoInstallPage: Boolean = true
)
