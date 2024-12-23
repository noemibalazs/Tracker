package com.noemi.tracker.screens.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.noemi.tracker.ui.theme.TrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TrackerTheme {
                RegisterScreen()
            }
        }
    }

    companion object {
        fun newInstance(context: Context) {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }
    }
}

