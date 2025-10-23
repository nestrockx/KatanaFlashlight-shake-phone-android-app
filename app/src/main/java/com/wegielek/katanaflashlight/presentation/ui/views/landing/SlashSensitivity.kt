package com.wegielek.katanaflashlight.presentation.ui.views.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.katanaflashlight.NewPrefs.sensitivity
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@Composable
fun SlashSensitivity(viewModel: LandingViewModel) {
    val context = LocalContext.current
    val sensitivity by context.sensitivity.collectAsState(initial = 1f)

    Text(
        text = stringResource(R.string.slash_sensitivity),
        color = Color.White,
        fontSize = 20.sp,
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
                .height(60.dp) // slightly taller for breathing space
                .clip(RoundedCornerShape(12.dp))
                .background(Color(1f, 1f, 1f, 0.75f))
                .padding(horizontal = 16.dp, vertical = 8.dp), // padding inside box
    ) {
        Slider(
            value = sensitivity,
            onValueChange = { viewModel.onSensitivityChange(it) },
            valueRange = 0f..10f,
            steps = 9,
            enabled = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            // center slider inside the box
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
    Spacer(modifier = Modifier.padding(10.dp))
}
