package com.finder.github_search.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _error.value = throwable.message ?: "An error occurred"
        _loading.value = false
    }

    protected fun launchWithLoading(block: suspend () -> Unit): Job {
        return viewModelScope.launch(exceptionHandler) {
            _loading.value = true
            try {
                block()
            } finally {
                _loading.value = false
            }
        }
    }
} 