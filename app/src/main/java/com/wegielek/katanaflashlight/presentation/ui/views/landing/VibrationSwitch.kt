package com.wegielek.katanaflashlight.presentation.ui.views.landing

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.wegielek.katanaflashlight.NewPrefs.vibrationOn
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@Composable
fun VibrationSwitch(viewModel: LandingViewModel) {
    val context = LocalContext.current
    val vibrationOn by context.vibrationOn.collectAsState(initial = false)

    Text(
        text = stringResource(R.string.vibrations),
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
                .wrapContentHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color(1f, 1f, 1f, 0.75f))
                .padding(end = 8.dp),
    ) {
        Switch(
            checked = vibrationOn,
            onCheckedChange = { viewModel.onVibrationSwitch(it) },
            enabled = true,
            modifier = Modifier.align(Alignment.CenterEnd),
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFEF5350),
                    checkedTrackColor = Color(0xFF461417),
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                ),
        )
    }
    Spacer(modifier = Modifier.size(10.dp))
}
