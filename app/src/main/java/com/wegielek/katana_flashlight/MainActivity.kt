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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
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

    @Composable
    fun HyperlinkText(
        modifier: Modifier = Modifier,
        fullText: String,
        textColor: Color = MaterialTheme.colorScheme.tertiary,
        linkText: List<String>,
        linkTextColor: Color = Color.Blue,
        linkTextFontWeight: FontWeight = FontWeight.Medium,
        linkTextDecoration: TextDecoration = TextDecoration.Underline,
        hyperlinks: List<String> = listOf("https://stevdza-san.com"),
        fontSize: TextUnit = TextUnit.Unspecified
    ) {
        val annotatedString = buildAnnotatedString {
            append(fullText)
            addStyle(
                style = SpanStyle(
                    fontSize = fontSize,
                    color = textColor
                ),
                start = 0,
                end = fullText.length
            )
            linkText.forEachIndexed { index, link ->
                val startIndex = fullText.indexOf(link)
                val endIndex = startIndex + link.length
                addStyle(
                    style = SpanStyle(
                        color = linkTextColor,
                        fontSize = fontSize,
                        fontWeight = linkTextFontWeight,
                        textDecoration = linkTextDecoration
                    ),
                    start = startIndex,
                    end = endIndex
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = hyperlinks[index],
                    start = startIndex,
                    end = endIndex
                )
            }
        }

        val uriHandler = LocalUriHandler.current

        ClickableText(
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = modifier,
            text = annotatedString,
            onClick = {
                annotatedString
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun IntroDialog() {
        val value by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 25f,
            targetValue = -25f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        val openDialog = remember { mutableStateOf(!Prefs.getIntroDone(this)) }

        if (openDialog.value) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    Prefs.setIntroDone(this, true)
                    openDialog.value = false
                }
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_katana_with_handle),
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .graphicsLayer(
                                    transformOrigin = TransformOrigin(
                                        pivotFractionX = 1.0f,
                                        pivotFractionY = 1.0f,
                                    ),
                                    rotationZ = value
                                )
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = getString(R.string.instruction), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }

    @Composable
    fun Wallpaper() {
        Image(
            painter = painterResource(id = R.drawable.katana),
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
        Spacer(modifier = Modifier.padding(10.dp))
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
        var isVibrationOn by remember { mutableStateOf(Prefs.getVibrationOn(this)) }
        Text(
            text = getString(R.string.vibrations),
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
                    startService()
                },
                enabled = true,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
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
    fun FlashlightStrengthSlider() {
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
            Spacer(modifier = Modifier.size(10.dp))
        }
    }

    @Composable
    fun FlashButton() {
        Button(
            onClick = { turnFlashlight() },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Image(painter = painterResource(id = R.drawable.ic_katana_with_handle), contentDescription = "", modifier = Modifier
                .wrapContentSize()
                .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 8.dp))
        }
    }

    @Composable
    fun RequestPermissionButton(onClick: () -> Unit) {
        Button(onClick = onClick, border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)) {
            Text(text = getString(R.string.allow_notification), color = Color.White)
        }
        Spacer(modifier = Modifier.size(10.dp))
    }

    @Composable
    fun ScreenOne(
        navigateToScreenTwo: () -> Unit
    ) {
        val context = LocalContext.current
        var isPermissionGranted by remember { mutableStateOf(false) }

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isPermissionGranted = isGranted
        }

        LaunchedEffect(key1 = true) {
            isPermissionGranted = checkNotificationPermission(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            IntroDialog()
            Wallpaper()
            MenuIcon(navigateToScreenTwo)
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.size(16.dp))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!isPermissionGranted) {
                        RequestPermissionButton(
                            onClick = {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) -> { }
                                    else -> {
                                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            }
                        )
                    }
                }
                FlashlightStrengthSlider()
                VibrationSwitch()
                SlashIntensity()
                FlashButton()
                
                Spacer(modifier = Modifier.size(16.dp))
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
            Wallpaper()
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
                Column (
                    modifier = Modifier
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                ) {
                    HyperlinkText(
                        fullText = "Icons made from https://www.onlinewebfonts.com/icon svg icons is licensed by CC BY 4.0",
                        linkText = listOf("https://www.onlinewebfonts.com/icon"),
                        hyperlinks = listOf("https://www.onlinewebfonts.com/icon"),
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        fontSize = 16.sp,
                        linkTextColor = Color(0.0f, 0.184f, 0.733f, 1.0f)
                    )
                    Text(
                        text = getString(R.string.version),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
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
    }

    override fun onResume() {
        super.onResume()
        startService()
    }

    override fun onStop() {
        super.onStop()
        if (!checkNotificationPermission(this)) {
            stopService(Intent(this, FlashlightForegroundService::class.java))
        }
    }

    private fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isMyServiceRunning(FlashlightForegroundService::class.java)) {
                ContextCompat.startForegroundService(this,
                    Intent(this, FlashlightForegroundService::class.java)
                )
            }
            return
        }
        if (!isMyServiceRunning(FlashlightForegroundService::class.java)) {
            ContextCompat.startForegroundService(this,
                Intent(this, FlashlightForegroundService::class.java)
            )
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

    private fun requestIgnoreBatteryOptimizations() {
        startActivity(
            Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse(
                    "package:$packageName"
                )
            )
        )
    }
}