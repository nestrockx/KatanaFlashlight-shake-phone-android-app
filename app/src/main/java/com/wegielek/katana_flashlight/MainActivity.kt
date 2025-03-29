package com.wegielek.katana_flashlight

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wegielek.katana_flashlight.ui.theme.KatanaFlashlightTheme

class MainActivity : ComponentActivity() {

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var foregroundService: Intent? = null

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

        Text(
            style = TextStyle(textAlign = TextAlign.Center),
            text = annotatedString,
            modifier = modifier.clickable {
                annotatedString
                    .getStringAnnotations("URL", 0, annotatedString.length)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun IntroDialog() {
        val value by rememberInfiniteTransition(label = "slash animation").animateFloat(
            initialValue = 25f,
            targetValue = -25f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ), label = "slash animation value"
        )
        val openDialog = remember { mutableStateOf(!Prefs.getIntroDone(applicationContext)) }

        if (openDialog.value) {
            BasicAlertDialog(onDismissRequest = {
                Prefs.setIntroDone(applicationContext, true)
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
                            contentDescription = getString(R.string.animated_katana),
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
            contentDescription = getString(R.string.katana_background),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .blur(5.dp)
                .alpha(0.5f)
        )
    }

    @Composable
    fun SlashIntensity() {
        val configuration = LocalConfiguration.current
        val padding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(horizontal = 96.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }

        Text(text = getString(R.string.slash_intensity), color = Color.White, fontSize = 20.sp, textAlign = TextAlign.Left, modifier = Modifier
            .padding(padding)
            .fillMaxWidth())
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(padding)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = Color(1f, 1f, 1f, 0.75f))
        ) {
            var intensity by remember { mutableFloatStateOf(5f) }

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
                },
                enabled = true,
                steps = 9,
                valueRange = 0f..10f,
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
                contentDescription = getString(R.string.menu_icon),
                modifier = Modifier
                    .clickable(
                        onClick = destination,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false)
                    )
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(50.dp)
                    .padding(8.dp)
            )
        }
    }

    @Composable
    fun OnOffSwitch() {
        var isOn by remember { mutableStateOf(Prefs.getKatanaOn(applicationContext)) }

        val configuration = LocalConfiguration.current
        val padding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(horizontal = 96.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }
        val paddingText = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(start = 96.dp, end = 96.dp, top = 64.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }

        Text(
            text = getString(R.string.on_off),
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(if(hasFlashlightStrengthLevels()) {padding} else {paddingText})
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(padding)
                .background(shape = RoundedCornerShape(8.dp), color = Color(1f, 1f, 1f, 0.75f))
                .padding(end = 8.dp)
        )
        {
            Switch(
                checked = isOn,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0.7f, 0f, 0f)
                ),
                onCheckedChange = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && checkCameraPermission(applicationContext) && checkNotificationPermission(applicationContext)) {
                        isOn = it
                        onKatanaSwitch(it)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkNotificationPermission(applicationContext)) {
                        isOn = it
                        onKatanaSwitch(it)
                    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        isOn = it
                        onKatanaSwitch(it)
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.lack_permissions), Toast.LENGTH_SHORT).show()
                        return@Switch
                    }

                    if (isOn) {
                        startService()
                    } else {
                        if (foregroundService != null) {
                            stopService(foregroundService)
                        }
                    }
                },
                enabled = true,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
    }

    private fun onKatanaSwitch(value: Boolean) {
        Prefs.setKatanaOn(applicationContext, value)
    }

    @Composable
    fun VibrationSwitch() {
        var isVibrationOn by remember { mutableStateOf(Prefs.getVibrationOn(applicationContext)) }
        val configuration = LocalConfiguration.current
        val padding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(horizontal = 96.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }

        Text(
            text = getString(R.string.vibrations),
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(padding)
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
            },
            enabled = true,
            steps = if (getFlashlightMaximumStrengthLevel() - 2 > 0) getFlashlightMaximumStrengthLevel() - 2 else 1,
            valueRange = 1f..getFlashlightMaximumStrengthLevel().toFloat(),
            modifier = Modifier
                .padding(16.dp)
        )
    }

    @Composable
    fun FlashlightStrengthSlider() {
        val configuration = LocalConfiguration.current
        val padding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(horizontal = 96.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }
        val paddingText = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(start = 96.dp, end = 96.dp, top = 64.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }

        if (hasFlashlightStrengthLevels()) {
            Text(
                text = getString(R.string.light_strength),
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingText)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(padding)
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
            onClick = { updateFlashlight(true) },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_katana_with_handle),
                contentDescription = getString(R.string.flash_button),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 8.dp)
            )
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
    fun LandingPage(
        navigateToScreenTwo: () -> Unit
    ) {
        val context = LocalContext.current

        var isCameraPermissionGranted by remember { mutableStateOf(false) }
        var cameraRequested by remember { mutableStateOf(false) }
        val cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isCameraPermissionGranted = isGranted
            if (isGranted) {
                if (Prefs.getKatanaOn(applicationContext)) {
                    startService()
                }
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ cameraRequested = true }, 100)
            }
        }

        var isNotificationPermissionGranted by remember { mutableStateOf(false) }
        var notificationRequested by remember { mutableStateOf(false) }
        val notificationLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isNotificationPermissionGranted = isGranted
            if (isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    if (Prefs.getKatanaOn(applicationContext)) {
                        startService()
                    }
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({ notificationRequested = true }, 100)
                }
            }
        }

        LaunchedEffect(key1 = true) {
            isNotificationPermissionGranted = checkNotificationPermission(context)
            isCameraPermissionGranted = checkCameraPermission(context)
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
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
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    if (!isNotificationPermissionGranted || !isCameraPermissionGranted) {
                        RequestPermissionButton(
                            onClick = {
                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        )
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!isNotificationPermissionGranted) {
                        RequestPermissionButton(
                            onClick = {
                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        )
                    }
                }
                FlashlightStrengthSlider()
                OnOffSwitch()
                VibrationSwitch()
                SlashIntensity()
                FlashButton()
                Spacer(modifier = Modifier.size(16.dp))
            }
            MenuIcon(navigateToScreenTwo)
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
                    contentDescription = getString(R.string.back_button),
                    tint = Color.White,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false),
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
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
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

        var time = true
        installSplashScreen()
            .setKeepOnScreenCondition {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({time = false}, 100)
                return@setKeepOnScreenCondition time
            }



        setContent {
            KatanaFlashlightTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        val navController = rememberNavController()
                        NavHost(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            navController = navController,
                            startDestination = Route.SCREEN_ONE
                        ) {
                            composable(route = Route.SCREEN_ONE) {
                                LandingPage(navigateToScreenTwo = {
                                    navController.navigate(Route.SCREEN_TWO)
                                })
                            }
                            composable(route = Route.SCREEN_TWO) {
                                ScreenTwo(
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
            }
        }

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager?
            try {
                cameraId = cameraManager?.cameraIdList?.get(0)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(applicationContext, getString(R.string.flashlight_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        if (!checkNotificationPermission(applicationContext)) {
            stopService(foregroundService)
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

    private fun checkCameraPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isMyServiceRunning(FlashlightForegroundService::class.java)) {
                foregroundService = Intent(applicationContext, FlashlightForegroundService::class.java)
                foregroundService?.let { ContextCompat.startForegroundService(applicationContext, it) }
            }
            return
        }
        if (!isMyServiceRunning(FlashlightForegroundService::class.java)) {
            foregroundService = Intent(applicationContext, FlashlightForegroundService::class.java)
            foregroundService?.let { ContextCompat.startForegroundService(applicationContext, it) }
        }
    }

    private fun onIntensityChange(intensity: Float) {
        val values = listOf(5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20)
        Prefs.setThreshold(applicationContext, values[intensity.toInt()].toFloat())
    }

    private fun onStrengthChange(strength: Int) {
        Prefs.setStrength(applicationContext, strength)
        updateFlashlight(false)
//        Toast.makeText(applicationContext, strength.toString(), Toast.LENGTH_SHORT).show()
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
            Prefs.setMaximumStrength(applicationContext, x!!)
            x
        } else {
            1
        }
    }

    private fun updateFlashlight(toggle: Boolean) {
        if  (toggle) {
            if (!Prefs.getFlashOn(applicationContext)) {
                try {
                    if (hasFlashlightStrengthLevels()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            try {
                                cameraManager?.turnOnTorchWithStrengthLevel(
                                    cameraId!!,
                                    Prefs.getStrength(this)
                                )
                            } catch (e: IllegalArgumentException) {
                                cameraManager?.setTorchMode(cameraId!!, true)
                                e.printStackTrace()
                            }
                        }
                    } else {
                        cameraManager?.setTorchMode(cameraId!!, true)
                    }
                    Prefs.setFlashOn(applicationContext, !Prefs.getFlashOn(applicationContext))
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    cameraManager?.setTorchMode(cameraId!!, false)
                    Prefs.setFlashOn(applicationContext, !Prefs.getFlashOn(applicationContext))
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        } else if (Prefs.getFlashOn(applicationContext)) {
            try {
                if (hasFlashlightStrengthLevels()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            cameraManager?.turnOnTorchWithStrengthLevel(
                                cameraId!!,
                                Prefs.getStrength(this)
                            )
                        } catch (e: IllegalArgumentException) {
                            cameraManager?.setTorchMode(cameraId!!, true)
                            e.printStackTrace()
                        }
                    }
                } else {
                    cameraManager?.setTorchMode(cameraId!!, true)
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun onVibrationSwitch(boolean: Boolean) {
        Prefs.setVibrationOn(applicationContext, boolean)
    }
}