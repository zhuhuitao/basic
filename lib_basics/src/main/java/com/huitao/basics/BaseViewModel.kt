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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huitao.data.IDataEvent
import com.huitao.data.ViewEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 *  @author hui tao
 *
 */
abstract class BaseViewModel(private val useCase: BaseUseCase) : ViewModel() {

    private var dataEventJob: Job? = null

    init {
        subscribe()
    }

    private fun subscribe() {
        useCase.dataEvents.let { sharedFlow ->
            dataEventJob = viewModelScope.launch {
                sharedFlow.collect { dataEvent ->
                    observerDataEvent(dataEvent)
                }
            }
        }
    }

    abstract fun observerDataEvent(dataEvent: IDataEvent)

    abstract fun processUiEvent(viewEvent: ViewEvent)
}