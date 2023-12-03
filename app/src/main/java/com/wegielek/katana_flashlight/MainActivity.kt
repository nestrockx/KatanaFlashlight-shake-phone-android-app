package com.wegielek.katana_flashlight

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wegielek.katana_flashlight.ui.theme.KatanaFlashlightTheme

class MainActivity : ComponentActivity() {

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun IntroDialog() {
        val openDialog = remember { mutableStateOf(true) }

        if (openDialog.value) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onDismissRequest.
                    openDialog.value = false
                }
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Wykonaj ruch dwukrotnego cięcia")
                    }
                }
            }
        }
    }

    @Composable
    fun Wallpaper() {
        Image(
            painter = painterResource(id = R.drawable.untitled),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .blur(5.dp)
                .alpha(0.5f)
        )
    }

    @Composable
    fun SlashIntensity() {
        var intensity by remember { mutableFloatStateOf(0f) }

        Slider(
            colors = SliderDefaults.colors(inactiveTrackColor = Color.Black, activeTrackColor = Color(
                0.8f,
                0.0f,
                0.0f,
                1.0f
            ), thumbColor = Color.Red),
            value = intensity,
            onValueChange = {
                intensity = it
                onIntensityChange(it)
                startService()
            },
            enabled = true,
            steps = 9,
            valueRange = -5f..5f,
            modifier = Modifier
                .padding(16.dp)
        )
    }

    @Composable
    fun MenuIcon(destination: () -> Unit) {
        Box (
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                tint = Color(0.7f, 0.0f, 0.0f, 1.0f),
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = "",
                modifier = Modifier
                    .clickable(
                        onClick = destination,
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(bounded = false)
                    )
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(50.dp)
                    .padding(8.dp)
            )
        }
    }

    @Composable
    fun VibrationSwitch() {
        var isVibrationOn by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 32.dp, end = 32.dp)
                .background(shape = RoundedCornerShape(8.dp), color = Color(1f, 1f, 1f, 0.75f))
                .padding(end = 8.dp)
        )
        {
            Switch(
                checked = isVibrationOn,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0.7f, 0f, 0f)
                ),
                onCheckedChange = {
                    isVibrationOn = it
                    onVibrationSwitch(it)
                },
                enabled = true,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }

    @Composable
    fun LightStrength() {
        var strength by remember { mutableFloatStateOf(getFlashlightMaximumStrengthLevel().toFloat()) }

        Slider(
            colors = SliderDefaults.colors(inactiveTrackColor = Color.Black, activeTrackColor = Color(
                0.8f,
                0.0f,
                0.0f,
                1.0f
            ), thumbColor = Color.Red),
            value = strength,
            onValueChange = {
                strength = it
                onStrengthChange(it.toInt())
                startService()
            },
            enabled = true,
            steps = getFlashlightMaximumStrengthLevel() - 1,
            valueRange = 0f..getFlashlightMaximumStrengthLevel().toFloat(),
            modifier = Modifier
                .padding(16.dp)
        )
    }

    @Composable
    fun ScreenOne(
        navigateToScreenTwo: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
        ) {
            IntroDialog()

            Wallpaper()
            MenuIcon(navigateToScreenTwo)
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                if (hasFlashlightStrengthLevels()) {
                    Text(
                        text = getString(R.string.light_strength),
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp, end = 32.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(start = 32.dp, end = 32.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(color = Color(1f, 1f, 1f, 0.75f))
                    ) {
                        LightStrength()
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }

                Text(
                    text = getString(R.string.vibrations),
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp)
                )
                VibrationSwitch()
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = getString(R.string.slash_intensity), color = Color.White, fontSize = 20.sp, textAlign = TextAlign.Left, modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp)
                    .fillMaxWidth())
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 32.dp, end = 32.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = Color(1f, 1f, 1f, 0.75f))
                ) {
                    SlashIntensity()
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Button(
                    onClick = { turnFlashlight() }
                ) {
                    Image(painter = painterResource(id = R.drawable.ic_katana_with_handle), contentDescription = "", modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 8.dp))
                }
            }
        }
    }

    @Composable
    fun ScreenTwo(
        navigateToScreenOne: () -> Unit,
        navigateBack: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            )
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = rememberRipple(bounded = false),
                            onClick = navigateBack
                        )
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(50.dp)
                        .padding(12.dp)
                )
                Text(
                    text = getString(R.string.version),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
            Wallpaper()
        }
    }

    object Route {
        const val SCREEN_ONE = "screenOne"
        const val SCREEN_TWO = "screenTwo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KatanaFlashlightTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Route.SCREEN_ONE) {
                    composable(route = Route.SCREEN_ONE) {
                        ScreenOne(navigateToScreenTwo = {
                            navController.navigate(Route.SCREEN_TWO)
                        })
                    }
                    composable(route = Route.SCREEN_TWO) {
                        ScreenTwo (
                            navigateToScreenOne = {
                                navController.navigate(Route.SCREEN_ONE)
                            },
                            navigateBack = {
                                onBackPressedDispatcher.onBackPressed()
                            }
                        )
                    }
                }
            }
        }

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            try {
                cameraId = cameraManager?.cameraIdList?.get(0)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, getString(R.string.flashlight_not_available), Toast.LENGTH_SHORT).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }

    override fun onResume() {
        super.onResume()
        startService()
    }

    private fun startService() {
        if (!isMyServiceRunning(FlashlightForegroundService::class.java)) {
            val serviceIntent = Intent(this, FlashlightForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    private fun onIntensityChange(intensity: Float) {
        Prefs.setThreshold(this, intensity * 1.2f + 10f)
    }

    private fun onStrengthChange(strength: Int) {
        Prefs.setStrength(this, strength)
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningServices = manager.runningAppProcesses
        if (runningServices != null) {
            for (processInfo in runningServices) {
                if (processInfo.pkgList != null) {
                    for (packageName in processInfo.pkgList) {
                        if (serviceClass.name == packageName) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun hasFlashlightStrengthLevels(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val x: Int? = cameraManager?.getCameraCharacteristics(cameraId!!)?.get(
                CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL
            )
            if (x != null) {
                if (x > 1) {
                    return true
                }
            }
        }
        return false
    }

    private fun getFlashlightMaximumStrengthLevel(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val x: Int? = cameraManager?.getCameraCharacteristics(cameraId!!)?.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL)
            Prefs.setMaximumStrength(this, x!!)
            x
        } else {
            1
        }
    }

    private fun turnFlashlight() {
        startService()
        if (!Prefs.getFlashOn(this)) {
            try {
                if (hasFlashlightStrengthLevels()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        cameraManager?.turnOnTorchWithStrengthLevel(cameraId!!, Prefs.getStrength(this))
                    }
                } else {
                    cameraManager?.setTorchMode(cameraId!!, true)
                }
                Prefs.setFlashOn(this, !Prefs.getFlashOn(this))
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {
            try {
                cameraManager?.setTorchMode(cameraId!!, false)
                Prefs.setFlashOn(this, !Prefs.getFlashOn(this))
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun onVibrationSwitch(boolean: Boolean) {
        Prefs.setVibrationOn(this, boolean)
    }

    private fun requestIgnoreFromBatteryOptimizations() {
        startActivity(
            Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse(
                    "package:$packageName"
                )
            )
        )
    }
}