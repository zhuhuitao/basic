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
package com.huitao.basics

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding


/**
 *  @author hui tao
 *
 */
open class BaseMviActivity<VB:ViewBinding>(private val block:(LayoutInflater)->VB):AppCompatActivity() {
    private  var _binding : VB? = null

    val binding: VB get() = requireNotNull(_binding) { "binding is null" }

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = block(layoutInflater)
        setContentView(_binding?.root)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}