package com.noemi.tracker.utils

import com.noemi.tracker.model.OutCome

fun <T> OutCome<T>.isSuccessOrNull(): T? {
    return if (this is OutCome.Success<T>) this.data else null
}

fun <T> OutCome<T>.isErrorOrNull(): String? {
    return if (this is OutCome.Error) this.message else null
}


