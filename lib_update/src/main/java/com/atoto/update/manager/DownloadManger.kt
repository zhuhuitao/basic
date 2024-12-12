package com.atoto.update.manager

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.atoto.update.R
import com.atoto.update.base.BaseHttpDownloadManger
import com.atoto.update.config.Constant
import com.atoto.update.data.UpdateParameters
import com.atoto.update.exception.MissingParameterException
import com.atoto.update.exception.ParameterErrorException
import com.atoto.update.installation.InstallationModel
import com.atoto.update.listener.DefaultLogAdapter
import com.atoto.update.listener.LifecycleCallbackAdapter
import com.atoto.update.listener.LogInterceptor
import com.atoto.update.listener.OnDownloadListener
import com.atoto.update.service.DownloadService
import com.atoto.update.utils.ApkUtil
import com.atoto.update.utils.LogUtil

/**
 * author:huitao
 */
private const val TAG = "DownloadManger"

class DownloadManger private constructor(builder: ConcreteBuilder) {

    companion object {
        private var instance: DownloadManger? = null

        internal fun getInstance(builder: ConcreteBuilder? = null): DownloadManger? {
            if (instance == null) {
                if (builder == null) {
                    return null
                }
                instance = DownloadManger(builder)
            }
            return instance!!
        }
    }

    internal var application: Application = builder.application
    internal var className: String = builder.contextClassName
    internal var apkUrl: String = builder.parameters.apkUrl
    internal var apkName: String = builder.parameters.apkName
    internal var logger: LogInterceptor = builder.parameters.logger ?: DefaultLogAdapter()
    internal var onDownloadListeners: MutableList<OnDownloadListener> = builder.onDownLoadListeners
    internal var downloadPath: String =
        builder.parameters.downloadPath ?: String.format(Constant.APK_PATH, application.packageName)
    internal var notificationChannel = builder.parameters.notificationChannel
    internal var smallIcon = builder.parameters.smallIcon
    internal var notifyId = builder.parameters.notifyId
    internal var apkMD5 = builder.parameters.apkMD5
    internal var isDownloading = false
    internal var httpDownloadManger = builder.parameters.httpDownloadManger
    internal var enableToast = builder.parameters.enableToast
    internal var enableNotification = builder.parameters.enableNotification

    @InstallationModel
    internal var installModel = builder.parameters.installModel
    internal var versionCode = builder.parameters.versionCode

    init {
        application.registerActivityLifecycleCallbacks(object : LifecycleCallbackAdapter() {
            override fun onActivityDestroyed(activity: Activity) {
                super.onActivityDestroyed(activity)
                if (className == activity.javaClass.name) {
                    clearListeners()
                }
            }
        })

    }

    fun download() {
        if (!checkParams()) {
            return
        }
        if (checkVersionCode()) {
            logger.i(TAG, "Start download apk")
            application.startService(Intent(application, DownloadService::class.java))
        } else {
            Toast.makeText(application, R.string.app_update_is_last_version, Toast.LENGTH_SHORT)
                .show()
            logger.i(TAG, "The apk is up to date")
        }
    }

    fun cancel(){
        httpDownloadManger?.cancel()
    }

    fun release() {
        httpDownloadManger?.release()
        clearListeners()
        instance = null
    }


    private fun clearListeners() {
        onDownloadListeners.clear()
    }


    private fun checkParams(): Boolean {
        if (apkUrl.isEmpty()) {
            throw MissingParameterException("Apk url can not be empty!")
        }
        if (apkName.isEmpty()) {
            throw MissingParameterException("Apk name can not be empty!")
        }
        if (!apkName.endsWith(Constant.APK_SUFFIX)) {
            throw ParameterErrorException("Apk name must endsWith .apk!")
        }
        if (smallIcon == -1) {
            throw MissingParameterException("SmallIcon can not be empty!")
        }
        Constant.AUTHORITIES = "${application.packageName}.fileProvider"
        return true
    }

    private fun checkVersionCode(): Boolean {
        if (versionCode == Int.MIN_VALUE) {
            return true
        }
        if (versionCode < ApkUtil.getVersionCode(application)) {
            return true
        }
        return false
    }

    class ConcreteBuilder(activity: Activity) : Builder {
        internal var parameters = UpdateParameters()
        internal var application = activity.application
        internal var contextClassName = activity.javaClass.name
        internal var onDownLoadListeners = mutableListOf<OnDownloadListener>()

        override fun apkUrl(apkUrl: String): ConcreteBuilder {
            parameters = parameters.copy(apkUrl = apkUrl)
            return this
        }

        override fun apkName(apkName: String): ConcreteBuilder {
            parameters = parameters.copy(apkName = apkName)
            return this
        }

        override fun downloadPath(downloadPath: String): ConcreteBuilder {
            parameters = parameters.copy(downloadPath = downloadPath)
            return this
        }

        override fun smallIcon(smallIcon: Int): ConcreteBuilder {
            parameters = parameters.copy(smallIcon = smallIcon)
            return this
        }

        override fun httpManger(httpManger: BaseHttpDownloadManger): ConcreteBuilder {
            parameters = parameters.copy(httpDownloadManger = httpManger)
            return this
        }

        override fun onDownLoadListener(onDownLoadListener: OnDownloadListener): ConcreteBuilder {
            onDownLoadListeners.add(onDownLoadListener)
            return this
        }

        override fun logInterceptor(logger: LogInterceptor): ConcreteBuilder {
            parameters = parameters.copy(logger = logger)
            return this
        }

        override fun notificationChannel(notificationChannel: NotificationChannel): ConcreteBuilder {
            parameters = parameters.copy(notificationChannel = notificationChannel)
            return this
        }

        override fun notifyId(notifyId: Int): ConcreteBuilder {
            parameters = parameters.copy(notifyId = notifyId)
            return this
        }

        override fun apkMD5(apkMD5: String): ConcreteBuilder {
            parameters = parameters.copy(apkMD5 = apkMD5)
            return this
        }

        override fun enableToast(enable: Boolean): ConcreteBuilder {
            parameters = parameters.copy(enableToast = enable)
            return this
        }

        override fun enableNotification(enable: Boolean): ConcreteBuilder {
            parameters = parameters.copy(enableNotification = enable)
            return this
        }

        override fun installationModel(@InstallationModel model: Int): ConcreteBuilder {
            parameters = parameters.copy(installModel = model)
            return this
        }


        override fun build(): DownloadManger? {
            return getInstance(this)
        }

    }

    internal interface Builder {
        fun apkUrl(apkUrl: String): ConcreteBuilder

        fun apkName(apkName: String): ConcreteBuilder

        fun downloadPath(downloadPath: String): ConcreteBuilder

        fun smallIcon(smallIcon: Int): ConcreteBuilder

        fun httpManger(httpManger: BaseHttpDownloadManger): ConcreteBuilder

        fun onDownLoadListener(onDownLoadListener: OnDownloadListener): ConcreteBuilder

        fun logInterceptor(logger: LogInterceptor): ConcreteBuilder

        fun notificationChannel(notificationChannel: NotificationChannel): ConcreteBuilder

        fun notifyId(notifyId: Int): ConcreteBuilder

        fun apkMD5(apkMD5: String): ConcreteBuilder

        fun enableToast(enable: Boolean): ConcreteBuilder

        fun enableNotification(enable: Boolean): ConcreteBuilder

        fun installationModel(@InstallationModel model: Int): ConcreteBuilder


        fun build(): DownloadManger?

    }
}