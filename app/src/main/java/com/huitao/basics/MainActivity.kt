package com.huitao.basics

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.atoto.update.installation.InstallationModel
import com.atoto.update.listener.OnDownloadListener
import com.atoto.update.manager.DownloadManger
import com.huitao.basics.databinding.ActivityMainBinding
import com.huitao.ext.plusAssign
import com.huitao.ext.view.clickNoRepeat
import com.huitao.utils.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


private const val TAG = "MainActivity"

class MainActivity : BaseMviActivity<ActivityMainBinding>({ ActivityMainBinding.inflate(it) }),
    NetworkManager.NetworkHandler {
    //  private val mCor = CoroutineScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var count = 0
        var job1: Job? = null
        var job2: Job? = null



        binding.root.clickNoRepeat {
            Log.d(TAG, "onCreate: 点击事件")
        }

        binding.tv.setOnClickListener {
//            try {
//                DownloadManger.ConcreteBuilder(this).apkUrl("https://atoto-usa.oss-us-west-1.aliyuncs.com/2024/FILE_UPLOAD_URL_2/64504783/" +
//                        " LumiAuto_v1.0.0.12-release_2024-10-18.apk")
//                    .apkName("android_9.0.81_64.apk")
//                  //  .downloadPath("${application.externalCacheDir?.path}/apk")
//                    .smallIcon(R.mipmap.ic_launcher)
//                    .logInterceptor(MyLoggerAdapter())
//                    .installationModel(5)
//                    .build()
//                    ?.download()
//            }catch (e:Exception){
//                Log.e(TAG, "onCreate: $e", )
//            }


        }

        DownloadManger.ConcreteBuilder(this)
            .apkUrl("https://atoto-usa.oss-us-west-1.aliyuncs.com/2024/FILE_UPLOAD_URL_2/64504783/LumiAuto_v1.0.0.12-release_2024-10-18.apk")
            .apkName("LumiAuto.apk")//文件名需要指定.apk结尾，下面有校验
            .downloadPath("${application.externalCacheDir?.path}/apk")//下载路径
            .smallIcon(R.mipmap.ic_launcher)//通知栏图标
            .logInterceptor(MyLoggerAdapter())//为了方便自己管理日志，比如写入文件可以传入自定义的
            .installationModel(InstallationModel.INTENT)//安装方式，SILENT 静默安装  INTENT 通过Intent安装需要人为干预
            .onDownLoadListener(object : OnDownloadListener {
                override fun onStart() {

                }

                override fun onProgress(progress: Int, max: Int) {

                }

                override fun onDone(file: File) {

                }

                override fun onCancel() {

                }

                override fun onError(e: Throwable) {

                }
            })
            .build()
            ?.download()

    }

    override fun onNetworkUpdate(isOnline: Boolean) {

    }


}
