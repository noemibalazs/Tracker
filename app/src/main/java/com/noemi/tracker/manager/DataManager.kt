package com.noemi.tracker.manager

import com.noemi.tracker.model.UserDetails

interface DataManager {

    suspend fun setUserDetails(userDetails: UserDetails)
    suspend fun getUserDetails(): UserDetails
}