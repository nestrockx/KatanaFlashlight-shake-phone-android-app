package com.wegielek.katanaflashlight.presentation.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.R

@Composable
fun Wallpaper() {
    Image(
        painter = painterResource(id = R.drawable.katana),
        contentDescription = stringResource(R.string.katana_background),
        contentScale = ContentScale.Fit,
        modifier =
            Modifier
                .fillMaxSize()
                .blur(5.dp)
                .alpha(0.5f),
    )
}
