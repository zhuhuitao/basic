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

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.huitao.exception.UnSupportException


/**
 *  @author hui tao
 *
 */
operator fun <E> Bundle.plusAssign(pair: Pair<String, E?>) {
    val (k, v) = pair
    when (v) {
        is Int -> putInt(k, v)
        is Boolean -> putBoolean(k, v)
        is String -> putString(k, v)
        else -> throw UnSupportException("Only support Int,Boolean,String,$v un support")
    }
}

operator fun <E> Intent.plusAssign(pair: Pair<String, E>) {
    val (k, v) = pair
    when (v) {
        is Int -> putExtra(k, v)
        is Boolean -> putExtra(k, v)
        is String -> putExtra(k, v)
        is Parcelable -> putExtra(k, v)
        else -> throw UnSupportException("Only support Int,Boolean,String,implementation parcelable,$v un support")
    }
}

operator fun <E> MutableList<E>.plusAssign(data: E) {
    add(data)
}

operator fun <E> ArrayList<E>.plusAssign(data: E) {
    this.add(data)
}

operator fun <K, V> MutableMap<K, V>.plusAssign(pair: Pair<K, V>) {
    val (k, v) = pair
    put(k, v)
}

operator fun <K, V> HashMap<K, V>.plusAssign(pair: Pair<K, V>) {
    val (k, v) = pair
    put(k, v)
}



