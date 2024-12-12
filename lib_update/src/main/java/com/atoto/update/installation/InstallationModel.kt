package com.atoto.update.installation

import androidx.annotation.IntDef
import java.lang.annotation.RetentionPolicy

/**
 * author:huitao
 */


@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [InstallationModel.SILENT, InstallationModel.INTENT, InstallationModel.NOTHING])
annotation class InstallationModel {
    companion object {
        const val SILENT = 0 //静默安装
        const val INTENT = 1 //通过Intent安装 需要用户手动干预
        const val NOTHING = 2 //lib内部下载完成之后，不做任何处理，上层可以通过监听回调的方式自己进行业务处理
    }
}






