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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.presentation.ui.views.Wallpaper
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LandingScreen(
    viewModel: LandingViewModel = koinViewModel(),
    navigateToAbout: () -> Unit,
) {
    val context = LocalContext.current

    val isCameraPermissionGranted by viewModel.hasCameraPermission.collectAsState()
    var cameraRequested by remember { mutableStateOf(false) }
    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            viewModel.setHasCameraPermission(isGranted)
            if (isGranted) {
                viewModel.startService()
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ cameraRequested = true }, 100)
            }
        }

    val isNotificationPermissionGranted by viewModel.hasNotificationPermission.collectAsState()
    var notificationRequested by remember { mutableStateOf(false) }
    val notificationLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            viewModel.setHasNotificationPermission(isGranted)
            if (isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    viewModel.startService()
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({ notificationRequested = true }, 100)
                }
            }
        }

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Wallpaper()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (cameraRequested) {
                IntroDialog(viewModel)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (notificationRequested) {
                IntroDialog(viewModel)
            }
        } else {
            IntroDialog(viewModel)
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
                        viewModel,
                        onClick = {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!isNotificationPermissionGranted) {
                    RequestPermissionButton(
                        viewModel,
                        onClick = {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                    )
                }
            }
            FlashlightStrengthSlider(viewModel)
            Spacer(Modifier.padding(4.dp))
            OnOffSwitch(viewModel)
            Spacer(Modifier.padding(4.dp))
            VibrationSwitch(viewModel)
            Spacer(Modifier.padding(4.dp))
            SlashSensitivity(viewModel)
            Spacer(Modifier.padding(4.dp))
            FlashButton(viewModel)
            Spacer(modifier = Modifier.size(16.dp))
        }
        MenuIcon(navigateToAbout)
    }
}
