package com.huitao.basics

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.atoto.btlock.databinding.ActivityMainBinding
import com.huitao.ext.getLauncher
import com.huitao.ext.plusAssign
import com.huitao.ext.view.clickNoRepeat
import com.huitao.utils.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

private const val TAG = "MainActivity"

class MainActivity : BaseMviActivity<ActivityMainBinding>({ ActivityMainBinding.inflate(it) }),NetworkManager.NetworkHandler{
    //  private val mCor = CoroutineScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var count = 0
        var job1: Job? = null
        var job2: Job? = null
        runBlocking {
            supervisorScope {

                job1 = launch {
                    while (true) {
                        count++

                        if (count == 10) {
                            job1?.cancel()
                        }
                        Log.d(TAG, "onCreate: job1 ")
                        delay(1000)
                    }
                }

                job2 = launch {
                    while (true) {
                        Log.d(TAG, "onCreate: job2")
                        delay(1000)
                    }
                }
            }

        }


        getLauncher {

        }.launch(Intent(this, MainActivity::class.java))


        binding.root.clickNoRepeat {

        }

        binding.root.clickNoRepeat(interval = 500, action = {

        })
    }

    override fun onNetworkUpdate(isOnline: Boolean) {
        //初始化
        val networkHandler = NetworkManager(this,this )
        val isOnline = networkHandler.isOnline
        //开启监听
        networkHandler.start()
        //移除，防止内存泄漏
        networkHandler.stop()


        val bundle = Bundle()
        bundle += "key1" to "value1"
        bundle += "key2" to "value2"


        val intent = Intent()
        intent += "key1" to "value1"
        intent += "key2" to "value2"
        intent += "key1" to "value1"

    }
}
