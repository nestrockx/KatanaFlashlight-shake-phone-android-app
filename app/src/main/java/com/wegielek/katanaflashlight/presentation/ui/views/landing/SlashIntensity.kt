package com.wegielek.katanaflashlight.presentation.ui.views.landing

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.katanaflashlight.Prefs
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@Composable
fun SlashSensitivity(viewModel: LandingViewModel) {
    val context = LocalContext.current

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
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(padding)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = Color(1f, 1f, 1f, 0.75f)),
    ) {
        var sensitivity by remember { mutableFloatStateOf(Prefs.getThreshold(context) / 3 - 3) }

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
                sensitivity = it
                val values = listOf(9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39)
                Prefs.setThreshold(context, values[it.toInt()].toFloat())
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
