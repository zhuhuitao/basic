package com.atoto.update.manager

import com.atoto.update.base.DownloadEngin
import com.atoto.update.base.DownloadStatus
import com.atoto.update.config.Constant
import com.atoto.update.listener.LogInterceptor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * author:huitao
 */
@Suppress("BlockingMethodInNonBlockingContext")
class HttpDownloadManager(private val path: String, private val logger: LogInterceptor) :
    DownloadEngin() {
    companion object {
        private const val TAG = "HttpDownloadManager"
    }

    private var shutdown: Boolean = false

    override fun download(url: String, apkName: String): Flow<DownloadStatus> {
        trustAllHosts()
        shutdown = false
        File(path, apkName).let {
            if (it.exists()) it.delete()
        }
        return flow {
            emit(DownloadStatus.Start)
            connectToDownload(url, apkName, this)
        }.catch {
            emit(DownloadStatus.Error(it))
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun connectToDownload(
        apkUrl: String, apkName: String, flow: FlowCollector<DownloadStatus>
    ) {
        val con = URL(apkUrl).openConnection() as HttpURLConnection
        con.apply {
            requestMethod = "GET"
            readTimeout = Constant.HTTP_TIME_OUT
            connectTimeout = Constant.HTTP_TIME_OUT
            setRequestProperty("Accept-Encoding", "identity")
        }
        if (con.responseCode == HttpURLConnection.HTTP_OK) {
            val inStream = con.inputStream
            val length = con.contentLength
            var len: Int
            var progress = 0
            val buffer = ByteArray(1024 * 2)
            val file = File(path, apkName)
            FileOutputStream(file).use { out ->
                while (inStream.read(buffer).also { len = it } != -1 && !shutdown) {
                    out.write(buffer, 0, len)
                    progress += len
                    flow.emit(DownloadStatus.Downloading(length, progress))
                }
                out.flush()
            }
            inStream.close()
            if (shutdown) {
                flow.emit(DownloadStatus.Cancel)
            } else {
                flow.emit(DownloadStatus.Done(file))
            }
        } else if (con.responseCode == HttpURLConnection.HTTP_MOVED_PERM
            || con.responseCode == HttpURLConnection.HTTP_MOVED_TEMP
        ) {
            con.disconnect()
            val locationUrl = con.getHeaderField("Location")
            logger.d(
                TAG,
                "The current url is the redirect Url, the redirected url is $locationUrl"
            )
            connectToDownload(locationUrl, apkName, flow)
        } else {
            val e = SocketTimeoutException("Error: Http response code = ${con.responseCode}")
            flow.emit(DownloadStatus.Error(e))
        }
        con.disconnect()
    }


    private fun trustAllHosts() {
        val manager: TrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                logger.d(TAG, "checkClientTrusted")
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                logger.d(TAG, "checkServerTrusted")
            }
        }
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(manager), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        } catch (e: Exception) {
            logger.e(TAG, "trustAllHosts error: $e")
        }
    }

    override fun cancel() {
        shutdown = true
    }

    override fun release() {
        cancel()
    }
}