package com.wegielek.katanaflashlight.presentation.ui.views.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.ui.views.Wallpaper

@Composable
fun AboutScreen(navigateToLanding: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Wallpaper()
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back_button),
                tint = Color.White,
                modifier =
                    Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false),
                            onClick = navigateToLanding,
                        ).align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(50.dp)
                        .padding(12.dp),
            )
            Column(
                modifier =
                    Modifier
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter),
            ) {
                HyperlinkText(
                    fullText = "Icon assets from https://www.onlinewebfonts.com/icon used under CC BY 4.0",
                    linkText = listOf("https://www.onlinewebfonts.com/icon"),
                    hyperlinks = listOf("https://www.onlinewebfonts.com/icon"),
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    fontSize = 16.sp,
                    linkTextColor = Color(1.0f, 0.44313726f, 0.44313726f, 1.0f),
                )
                Text(
                    text = stringResource(R.string.version),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                )
            }
        }
    }
}
