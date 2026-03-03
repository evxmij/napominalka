package com.napominalka.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {
    private val _elapsedSec = MutableStateFlow(0L)
    val elapsedSec: StateFlow<Long> = _elapsedSec.asStateFlow()

    private var tickingJob: Job? = null

    fun start() {
        if (tickingJob != null) return
        tickingJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                _elapsedSec.value += 1
            }
        }
    }

    fun pause() {
        tickingJob?.cancel()
        tickingJob = null
    }

    fun stop() {
        pause()
        _elapsedSec.value = 0
    }
}
