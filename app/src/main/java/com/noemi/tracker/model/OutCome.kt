package com.noemi.tracker.model

sealed class OutCome<out T> {

    data class Success<T>(val data: T) : OutCome<T>()
    data class Error(val message: String) : OutCome<Nothing>()
}