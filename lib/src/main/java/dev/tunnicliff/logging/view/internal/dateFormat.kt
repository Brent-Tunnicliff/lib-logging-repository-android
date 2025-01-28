// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.view.internal

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun Instant.toLogDate(): String =
    DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss:SSS")
        .withZone(ZoneId.systemDefault())
        .format(this)


@Preview
@Composable
private fun Preview() = Text(text = Instant.parse("2024-01-02T03:04:05.06Z").toLogDate())
