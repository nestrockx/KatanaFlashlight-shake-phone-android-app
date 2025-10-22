package com.wegielek.katanaflashlight.presentation.ui.views.landing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.NewPrefs.strength
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@Composable
fun LightStrength(viewModel: LandingViewModel) {
    val context = LocalContext.current
    val storedStrength by context.strength.collectAsState(initial = 1)
    val maxStrength by viewModel.maxStrengthLevel.collectAsState()

    // Keep local copy for smooth slider movement
    var sliderValue by remember { mutableFloatStateOf(storedStrength.toFloat()) }

    // Sync DataStore updates into local state when they change externally
    LaunchedEffect(storedStrength) {
        if (storedStrength != sliderValue.toInt()) {
            sliderValue = storedStrength.toFloat()
        }
    }

    Slider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        onValueChangeFinished = {
            viewModel.onStrengthChange(sliderValue.toInt())
        },
        steps = (maxStrength - 2).coerceAtLeast(0),
        valueRange = 1f..maxStrength.toFloat(),
        colors =
            SliderDefaults.colors(
                inactiveTrackColor = Color.Black,
                activeTrackColor = Color(0.8f, 0.0f, 0.0f, 1.0f),
                thumbColor = Color.Red,
            ),
        modifier = Modifier.padding(16.dp),
    )
}
