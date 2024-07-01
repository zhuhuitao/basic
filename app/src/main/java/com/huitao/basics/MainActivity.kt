package com.huitao.basics

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.atoto.btlock.databinding.ActivityMainBinding
import com.huitao.ext.getLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

private const val TAG = "MainActivity"

class MainActivity : BaseMviActivity<ActivityMainBinding>({ ActivityMainBinding.inflate(it) }) {
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


    }
}
