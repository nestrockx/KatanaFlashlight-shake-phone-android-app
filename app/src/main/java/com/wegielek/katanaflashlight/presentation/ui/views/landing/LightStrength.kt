package com.wegielek.katanaflashlight.presentation.ui.views.landing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@Composable
fun LightStrength(viewModel: LandingViewModel) {
    val context = LocalContext.current

    var strength by remember { mutableFloatStateOf(viewModel.getFlashlightMaximumStrengthLevel(context).toFloat()) }

    Slider(
        colors =
            SliderDefaults.colors(
                inactiveTrackColor = Color.Black,
                activeTrackColor =
                    Color(
                        0.8f,
                        0.0f,
                        0.0f,
                        1.0f,
                    ),
                thumbColor = Color.Red,
            ),
        value = strength,
        onValueChange = {
            strength = it
            viewModel.onStrengthChange(context, it.toInt())
        },
        enabled = true,
        steps =
            if (viewModel.getFlashlightMaximumStrengthLevel(context) - 2 >
                0
            ) {
                viewModel.getFlashlightMaximumStrengthLevel(context) - 2
            } else {
                1
            },
        valueRange = 1f..viewModel.getFlashlightMaximumStrengthLevel(context).toFloat(),
        modifier =
            Modifier
                .padding(16.dp),
    )
}
