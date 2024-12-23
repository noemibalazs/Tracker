package com.noemi.tracker.providers

import android.util.Base64
import java.security.SecureRandom

object NonceProvider {

    fun getNonce(): String {
        val bytes = ByteArray(42)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE)
    }
}