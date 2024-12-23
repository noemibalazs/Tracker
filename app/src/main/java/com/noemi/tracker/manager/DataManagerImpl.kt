package com.noemi.tracker.manager

import android.content.SharedPreferences
import com.noemi.tracker.model.UserDetails
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DataManagerImpl @Inject constructor(private val sharedPreferences: SharedPreferences) : DataManager {

    override suspend fun setUserDetails(userDetails: UserDetails) {
        val json = Json.encodeToString<UserDetails>(userDetails)
        sharedPreferences.edit().putString(USER_KEY, json).apply()
    }

    override suspend fun getUserDetails(): UserDetails {
        val user = sharedPreferences.getString(USER_KEY, "") ?: ""
        return when (user.isEmpty()) {
            true -> UserDetails()
            else -> Json.decodeFromString<UserDetails>(user)
        }
    }

    companion object {
        private const val USER_KEY = "user details key"
    }
}