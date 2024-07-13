package com.huitao.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter


/**
 * author:huitao
 * 监听安装了某个应用或服务
 */
class PackageChangeReceiver(
    private var onPackageAdd: ((String) -> Unit)? = null,
    private var onPackageChange: ((String) -> Unit)? = null,
    private var onPackageRemove: ((String) -> Unit)? = null
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED -> onPackageAdded(intent)
            Intent.ACTION_PACKAGE_CHANGED -> onPackageChanged(intent)
            Intent.ACTION_PACKAGE_REMOVED -> onPackageRemoved(intent)
        }
    }

    fun register(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        intentFilter.addDataScheme("package")
        context.registerReceiver(this, intentFilter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }

    private fun onPackageAdded(intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        packageName ?: return
        onPackageAdd?.invoke(packageName)
    }

    private fun onPackageChanged(intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        packageName ?: return
        onPackageChange?.invoke(packageName)
    }

    private fun onPackageRemoved(intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        packageName ?: return
        onPackageRemove?.invoke(packageName)
    }
}