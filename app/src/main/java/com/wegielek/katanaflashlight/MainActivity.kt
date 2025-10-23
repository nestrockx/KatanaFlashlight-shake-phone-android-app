package com.wegielek.katanaflashlight

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wegielek.katanaflashlight.presentation.ui.theme.KatanaFlashlightTheme
import com.wegielek.katanaflashlight.presentation.ui.views.MainScreen

class MainActivity : ComponentActivity() {
    object Route {
        const val LANDING_SCREEN = "landingScreen"
        const val ABOUT_SCREEN = "aboutScreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var time = true
        installSplashScreen()
            .setKeepOnScreenCondition {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ time = false }, 100)
                return@setKeepOnScreenCondition time
            }

        enableEdgeToEdge()
        setContent {
            KatanaFlashlightTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(innerPadding)
                }
            }
        }
    }
}
