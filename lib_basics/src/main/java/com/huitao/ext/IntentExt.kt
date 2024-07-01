/*
 * Designed and developed by 2022 Huitao (Huitao Pig)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huitao.ext

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.huitao.exception.UnSupportException


/**
 *  @author hui tao
 *
 */

fun AppCompatActivity.getLauncher(block: (result: ActivityResult) -> Unit): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult(), block)
}

fun Fragment.getLauncher(block: (result: ActivityResult) -> Unit): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult(), block)
}

fun <T> AppCompatActivity.setResultOk(pair: Pair<String, T>) {
    val (k, v) = pair
    intent += when (v) {
        is Int -> k to v
        is Boolean -> k to v
        is String -> k to v
        is Parcelable -> k to v
        else -> throw UnSupportException("Only support Int,Boolean,String,implementation parcelable,$v un support")
    }
    setResult(Activity.RESULT_OK, intent)
}
