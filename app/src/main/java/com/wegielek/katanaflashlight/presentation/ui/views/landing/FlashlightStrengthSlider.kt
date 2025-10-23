package com.wegielek.katanaflashlight.presentation.ui.views.landing

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.katanaflashlight.NewPrefs.strength
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@Composable
fun FlashlightStrengthSlider(viewModel: LandingViewModel) {
    val hasStrengthLevels by viewModel.hasStrengthLevels.collectAsState()

    val context = LocalContext.current
    val storedStrength by context.strength.collectAsState(initial = 1)
    val maxStrength by viewModel.maxStrengthLevel.collectAsState()

    var sliderValue by remember { mutableFloatStateOf(storedStrength.toFloat()) }

    LaunchedEffect(storedStrength) {
        if (storedStrength != sliderValue.toInt()) {
            sliderValue = storedStrength.toFloat()
        }
    }

    if (hasStrengthLevels) {
        Text(
            text = stringResource(R.string.light_strength),
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Left,
            modifier =
                Modifier
                    .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.size(4.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = Color(1f, 1f, 1f, 0.75f)),
        ) {
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { viewModel.onStrengthChange(sliderValue.toInt()) },
                steps = (maxStrength - 2).coerceAtLeast(0),
                valueRange = 1f..maxStrength.toFloat(),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center),
                colors =
                    SliderDefaults.colors(
                        thumbColor = Color.Red,
                        activeTrackColor = Color(0xFFEF5350),
                        inactiveTrackColor = Color(0xFFBDBDBD),
                        activeTickColor = Color.White,
                        inactiveTickColor = Color.Gray,
                    ),
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
    }
}
