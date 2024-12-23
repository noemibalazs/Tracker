package com.noemi.tracker.screens.verifyemail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.noemi.tracker.ui.theme.TrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailVerificationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val email = intent.getStringExtra(SIGN_UP_EMAIL_KEY)

            TrackerTheme {
                EmailVerificationScreen(email ?: "")
            }
        }
    }

    companion object {
        const val SIGN_UP_EMAIL_KEY = "email_key"
        fun newInstance(context: Context, email: String) {
            val intent = Intent(context, EmailVerificationActivity::class.java)
            intent.putExtra(SIGN_UP_EMAIL_KEY, email)
            context.startActivity(intent)
        }
    }
}