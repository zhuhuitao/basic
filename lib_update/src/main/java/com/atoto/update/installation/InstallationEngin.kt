package com.atoto.update.installation

import android.content.Context
import java.io.File

/**
 * author:huitao
 */
interface InstallationEngin {
    fun install(context: Context, authorities: String, apk: File)
}