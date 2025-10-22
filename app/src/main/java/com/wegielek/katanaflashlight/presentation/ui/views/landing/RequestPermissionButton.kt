package com.wegielek.katanaflashlight.presentation.ui.views.landing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wegielek.katanaflashlight.R

@Composable
fun RequestPermissionButton(onClick: () -> Unit) {
    Button(onClick = onClick, border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)) {
        Text(text = stringResource(R.string.allow_notification), color = Color.White)
    }
    Spacer(modifier = Modifier.size(10.dp))
}
