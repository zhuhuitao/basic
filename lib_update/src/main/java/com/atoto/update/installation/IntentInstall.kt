package com.atoto.update.installation

import android.content.Context
import com.atoto.update.utils.ApkUtil
import java.io.File

/**
 * author:huitao
 */
class IntentInstall : InstallationEngin {
    override fun install(context: Context, authorities: String, apk: File) {
        ApkUtil.installApk(context, authorities, apk)
    }

}