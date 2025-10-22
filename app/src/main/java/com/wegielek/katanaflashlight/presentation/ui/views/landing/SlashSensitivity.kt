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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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

    val configuration = LocalConfiguration.current
    val padding =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(horizontal = 96.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }

    Text(
        text = stringResource(R.string.slash_sensitivity),
        color = Color.White,
        fontSize = 20.sp,
        textAlign = TextAlign.Left,
        modifier =
            Modifier
                .padding(padding)
                .fillMaxWidth(),
    )
    Spacer(modifier = Modifier.size(4.dp))
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(padding)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = Color(1f, 1f, 1f, 0.75f)),
    ) {
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
            value = sensitivity,
            onValueChange = {
                viewModel.onSensitivityChange(it)
            },
            enabled = true,
            steps = 9,
            valueRange = 0f..10f,
            modifier =
                Modifier
                    .padding(16.dp),
        )
    }
    Spacer(modifier = Modifier.padding(10.dp))
}
