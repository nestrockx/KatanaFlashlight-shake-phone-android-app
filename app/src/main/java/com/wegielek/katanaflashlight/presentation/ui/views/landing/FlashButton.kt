package com.wegielek.katanaflashlight.presentation.ui.views.landing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel

@Composable
fun FlashButton(viewModel: LandingViewModel) {
    val context = LocalContext.current

    Button(
        onClick = { viewModel.updateFlashlight(context, true) },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_katana_with_handle),
            contentDescription = stringResource(R.string.flash_button),
            modifier =
                Modifier
                    .wrapContentSize()
                    .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 8.dp),
        )
    }
}
