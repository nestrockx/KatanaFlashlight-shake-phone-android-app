package com.wegielek.katanaflashlight.presentation.ui.views.landing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.R

@Composable
fun MenuIcon(destination: () -> Unit) {
    Icon(
        tint = Color(0.7f, 0.0f, 0.0f, 1.0f),
        painter = painterResource(id = R.drawable.ic_menu),
        contentDescription = stringResource(R.string.menu_icon),
        modifier =
            Modifier
                .clickable(
                    onClick = destination,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false),
                ).padding(8.dp)
                .size(50.dp)
                .padding(8.dp),
    )
}
