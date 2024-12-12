package com.atoto.update.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.SessionParams
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.atoto.update.service.InstallResultReceiver
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

/**
 * author:huitao
 */
class ApkUtil {

    companion object {
        fun installApk(context: Context, authorities: String, apk: File) {
            context.startActivity(createInstallIntent(context, authorities, apk))
        }

        fun installApkSilent(context: Context, apk: File) {
            installApkSilently(context, apk)
        }

        fun createInstallIntent(context: Context, authorities: String, apk: File): Intent {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            val uri: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, authorities, apk)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                uri = Uri.fromFile(apk)
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            return intent
        }

        fun getVersionCode(context: Context): Long {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                return packageInfo.versionCode.toLong()
            }
        }

        fun deleteOldApk(context: Context, oldApkPath: String): Boolean {
            val curVersionCode = getVersionCode(context)
            try {
                val apk = File(oldApkPath)
                if (apk.exists()) {
                    val oldVersionCode = getVersionCodeByPath(context, oldApkPath)
                    if (curVersionCode > oldVersionCode) {
                        return apk.delete()
                    }
                }
            } catch (e: Exception) {
            }
            return false
        }

        private fun getVersionCodeByPath(context: Context, path: String): Long {
            val packageInfo =
                context.packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode ?: 1
            } else {
                return packageInfo?.versionCode?.toLong() ?: 1
            }
        }


        private fun installApkSilently(context: Context, file: File): Boolean {
            val outputStream: OutputStream
            val inputStream: FileInputStream
            var success = false
            try {
                //获取PackageInstaller
                val packageInstaller: PackageInstaller = context.packageManager.packageInstaller
                val params = SessionParams(SessionParams.MODE_FULL_INSTALL)
                val sessionId: Int = packageInstaller.createSession(params)
                val session = packageInstaller.openSession(sessionId)
                //获取输出流，用于将apk写入session
                outputStream = session.openWrite(file.name, 0, -1)
                inputStream = FileInputStream(file)
                val buffer = ByteArray(65536)
                var c: Int
                while (inputStream.read(buffer).also { c = it } != -1) {
                    outputStream.write(buffer, 0, c)
                }
                session.fsync(outputStream)
                inputStream.close()
                outputStream.close()
                val installResultReceiver = InstallResultReceiver(context)
                // 发送安装请求并关闭流
                session.commit(installResultReceiver.getIntentSender())
                success = installResultReceiver.isSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return success
        }
    }
}