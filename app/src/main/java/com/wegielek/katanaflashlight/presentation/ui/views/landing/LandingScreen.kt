package com.wegielek.katanaflashlight.presentation.ui.views.landing

import android.Manifest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.Prefs
import com.wegielek.katanaflashlight.presentation.ui.views.Wallpaper
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LandingScreen(
    viewModel: LandingViewModel = koinViewModel(),
    navigateToAbout: () -> Unit,
) {
    val context = LocalContext.current

    var isCameraPermissionGranted by remember { mutableStateOf(false) }
    var cameraRequested by remember { mutableStateOf(false) }
    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            isCameraPermissionGranted = isGranted
            if (isGranted) {
                if (Prefs.getKatanaOn(context)) {
                    viewModel.startService(context)
                }
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ cameraRequested = true }, 100)
            }
        }

    var isNotificationPermissionGranted by remember { mutableStateOf(false) }
    var notificationRequested by remember { mutableStateOf(false) }
    val notificationLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            isNotificationPermissionGranted = isGranted
            if (isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    if (Prefs.getKatanaOn(context)) {
                        viewModel.startService(context)
                    }
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({ notificationRequested = true }, 100)
                }
            }
        }

    LaunchedEffect(key1 = true) {
        isNotificationPermissionGranted = viewModel.checkNotificationPermission(context)
        isCameraPermissionGranted = viewModel.checkCameraPermission(context)
    }

    LaunchedEffect(Unit) {
        viewModel.init(context)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Wallpaper()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (cameraRequested) {
                IntroDialog()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (notificationRequested) {
                IntroDialog()
            }
        } else {
            IntroDialog()
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (!isNotificationPermissionGranted || !isCameraPermissionGranted) {
                    RequestPermissionButton(
                        onClick = {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!isNotificationPermissionGranted) {
                    RequestPermissionButton(
                        onClick = {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                    )
                }
            }
            FlashlightStrengthSlider(viewModel)
            OnOffSwitch(viewModel)
            VibrationSwitch(viewModel)
            SlashIntensity(viewModel)
            FlashButton(viewModel)
            Spacer(modifier = Modifier.size(16.dp))
        }
        MenuIcon(navigateToAbout)
    }
}
