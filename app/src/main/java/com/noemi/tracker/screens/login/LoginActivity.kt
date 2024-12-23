package com.noemi.tracker.screens.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.noemi.tracker.ui.theme.TrackerTheme
import dagger.hilt.android.AndroidEntryPoint

// https://developer.android.com/codelabs/camerax-getting-started#0
// https://developer.android.com/media/camera/camerax
// https://developers.google.com/ml-kit/vision/text-recognition/v2/android  ///  done

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TrackerTheme {
                LoginScreen()
            }
        }
    }

    companion object {
        fun newInstance(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}