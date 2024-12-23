package com.noemi.tracker.screens.tracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.noemi.tracker.ui.theme.TrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TrackerTheme {
                TrackerScreen()
            }
        }
    }

    companion object {

        fun newInstance(context: Context) {
            context.startActivity(Intent(context, TrackerActivity::class.java))
        }
    }
}
