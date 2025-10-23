package com.wegielek.katanaflashlight.presentation.ui.views.landing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.R

@Composable
fun MenuIcon(destination: () -> Unit) {
    Icon(
        tint = Color.White,
        imageVector = Icons.Rounded.Menu,
        contentDescription = stringResource(R.string.menu_icon),
        modifier =
            Modifier
                .clickable(
                    onClick = destination,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, color = Color.White),
                ).padding(8.dp)
                .size(50.dp)
                .padding(8.dp),
    )
}
