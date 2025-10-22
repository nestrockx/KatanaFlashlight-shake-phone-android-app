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
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@Composable
fun FlashlightStrengthSlider(viewModel: LandingViewModel) {
    val context = LocalContext.current

    val hasStrengthLevels by viewModel.hasStrengthLevels.collectAsState()

    val configuration = LocalConfiguration.current
    val padding =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(horizontal = 96.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }
    val paddingText =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingValues(start = 96.dp, end = 96.dp, top = 64.dp)
        } else {
            PaddingValues(horizontal = 32.dp)
        }

    if (hasStrengthLevels) {
        Text(
            text = stringResource(R.string.light_strength),
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Left,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(paddingText),
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
            LightStrength(viewModel)
        }
        Spacer(modifier = Modifier.size(10.dp))
    }
}
