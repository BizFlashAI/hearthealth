package com.hearthealth.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hearthealth.app.ui.HeartHealthNavigation
import com.hearthealth.app.ui.theme.HeartHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeartHealthTheme {
                HeartHealthNavigation()
            }
        }
    }
}
