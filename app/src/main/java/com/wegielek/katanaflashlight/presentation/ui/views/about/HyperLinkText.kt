package com.wegielek.katanaflashlight.presentation.ui.views.about

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    textColor: Color = MaterialTheme.colorScheme.tertiary,
    linkText: List<String>,
    linkTextColor: Color = Color.Blue,
    linkTextFontWeight: FontWeight = FontWeight.Medium,
    linkTextDecoration: TextDecoration = TextDecoration.Underline,
    hyperlinks: List<String> = listOf(""),
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    val annotatedString =
        buildAnnotatedString {
            append(fullText)
            addStyle(
                style =
                    SpanStyle(
                        fontSize = fontSize,
                        color = textColor,
                    ),
                start = 0,
                end = fullText.length,
            )
            linkText.forEachIndexed { index, link ->
                val startIndex = fullText.indexOf(link)
                val endIndex = startIndex + link.length
                addStyle(
                    style =
                        SpanStyle(
                            color = linkTextColor,
                            fontSize = fontSize,
                            fontWeight = linkTextFontWeight,
                            textDecoration = linkTextDecoration,
                        ),
                    start = startIndex,
                    end = endIndex,
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = hyperlinks[index],
                    start = startIndex,
                    end = endIndex,
                )
            }
        }

    val uriHandler = LocalUriHandler.current

    Text(
        style = TextStyle(textAlign = TextAlign.Center),
        text = annotatedString,
        modifier =
            modifier.clickable {
                annotatedString
                    .getStringAnnotations("URL", 0, annotatedString.length)
                    .firstOrNull()
                    ?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            },
    )
}
