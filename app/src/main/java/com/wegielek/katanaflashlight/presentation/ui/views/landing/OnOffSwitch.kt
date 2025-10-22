package com.wegielek.katanaflashlight.presentation.ui.views.landing

import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun OnOffSwitch(viewModel: LandingViewModel) {
    val context = LocalContext.current

    var isOn by remember { mutableStateOf(Prefs.getKatanaOn(context)) }

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

    Text(
        text = stringResource(R.string.on_off),
        fontSize = 20.sp,
        color = Color.White,
        textAlign = TextAlign.Left,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    if (viewModel.hasStrengthLevels()) {
                        padding
                    } else {
                        paddingText
                    },
                ),
    )
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(padding)
                .background(shape = RoundedCornerShape(8.dp), color = Color(1f, 1f, 1f, 0.75f))
                .padding(end = 8.dp),
    ) {
        Switch(
            checked = isOn,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = Color(0.7f, 0f, 0f),
                ),
            onCheckedChange = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && viewModel.hasCameraPermission() &&
                    viewModel.hasNotificationPermission()
                ) {
                    isOn = it
                    viewModel.onKatanaSwitch(it)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && viewModel.hasNotificationPermission()) {
                    isOn = it
                    viewModel.onKatanaSwitch(it)
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    isOn = it
                    viewModel.onKatanaSwitch(it)
                } else {
                    Toast.makeText(context, context.getString(R.string.lack_permissions), Toast.LENGTH_SHORT).show()
                    return@Switch
                }

                if (isOn) {
                    viewModel.startService()
                } else {
                    if (viewModel.isServiceRunning()) {
                        viewModel.stopService()
                    }
                }
            },
            enabled = true,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
    Spacer(modifier = Modifier.size(10.dp))
}
