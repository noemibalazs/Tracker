package com.noemi.tracker.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noemi.tracker.model.UIAuthState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel<Event> : ViewModel() {

    private val event = MutableSharedFlow<Event>()

    private var _uiState = MutableStateFlow(UIAuthState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    protected abstract suspend fun handleEvent(event: Event)

    fun onEvent(uiEvent: Event) {
        viewModelScope.launch {
            event.emit(uiEvent)
        }
    }

    fun updateUIState(state: UIAuthState.() -> UIAuthState) {
        viewModelScope.launch {
            _uiState.update { it.state() }
        }
    }
}