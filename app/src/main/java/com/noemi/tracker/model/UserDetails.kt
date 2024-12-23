package com.noemi.tracker.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDetails(
    val email: String? = null,
    val avatar: String? = null,
    val name: String? = null
)