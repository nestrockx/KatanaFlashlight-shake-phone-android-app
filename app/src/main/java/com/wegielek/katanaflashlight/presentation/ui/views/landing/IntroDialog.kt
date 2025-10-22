package com.wegielek.katanaflashlight.presentation.ui.views.landing

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.Prefs
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroDialog(viewModel: LandingViewModel) {
    val context = LocalContext.current

    val value by rememberInfiniteTransition(label = "slash animation").animateFloat(
        initialValue = 25f,
        targetValue = -25f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 600,
                        easing = LinearEasing,
                    ),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "slash animation value",
    )
    val openDialog = remember { mutableStateOf(!Prefs.getIntroDone(context)) }

    if (openDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                Prefs.setIntroDone(context, true)
                openDialog.value = false
            },
        ) {
            Surface(
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_katana_with_handle),
                        contentDescription = stringResource(R.string.animated_katana),
                        modifier =
                            Modifier
                                .size(50.dp)
                                .graphicsLayer(
                                    transformOrigin =
                                        TransformOrigin(
                                            pivotFractionX = 1.0f,
                                            pivotFractionY = 1.0f,
                                        ),
                                    rotationZ = value,
                                ),
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = stringResource(R.string.instruction), textAlign = TextAlign.Center)
                }
            }
        }
    }
}
