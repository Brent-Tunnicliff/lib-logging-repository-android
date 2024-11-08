// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.view.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tunnicliff.logging.R
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.model.localisedString
import dev.tunnicliff.ui.component.card.BaseCard
import dev.tunnicliff.ui.component.card.CardVariant
import dev.tunnicliff.ui.component.list.BaseList
import dev.tunnicliff.ui.theme.PreviewerTheme
import dev.tunnicliff.ui.theme.ThemedPreviewer

@Composable
internal fun LogCardView(logEntity: LogEntity) {
    BaseCard(
        variant = logEntity.cardVariant
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(
                    text = "[${logEntity.level.localisedString()}] [${logEntity.tag}]",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = logEntity.message,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )

                if (logEntity.throwable != null) {
                    Text(
                        text = stringResource(
                            id = R.string.log_card_error,
                            logEntity.throwable.toString()
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )
                }
            }

            Column {
                Text(
                    text = logEntity.timestampCreated.toLogDate(),
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = if (logEntity.uploaded)
                        stringResource(id = R.string.log_card_uploaded)
                    else
                        stringResource(id = R.string.log_card_not_uploaded),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private val LogEntity.cardVariant: CardVariant
    get() =
        when (level) {
            LogLevel.CRITICAL, LogLevel.ERROR, LogLevel.WARNING -> CardVariant.ERROR
            LogLevel.DEBUG, LogLevel.INFO -> CardVariant.SURFACE
        }

@Preview
@Composable
private fun PreviewLightTheme() = PreviewContent(PreviewerTheme.LIGHT)

@Preview
@Composable
private fun PreviewDarkTheme() = PreviewContent(PreviewerTheme.DARK)

@Composable
private fun PreviewContent(theme: PreviewerTheme) {
    ThemedPreviewer(theme, enablePreviewScrolling = false) {
        BaseList {
            items(
                listOf(
                    LogEntity.mock(
                        level = LogLevel.DEBUG,
                        uploaded = true
                    ),
                    LogEntity.mock(
                        level = LogLevel.INFO,
                        message = "This log has a very, very long message so we can see what that looks like."
                    ),
                    LogEntity.mock(
                        level = LogLevel.WARNING,
                        message = "This log has a is so very, very, very, crazy long message so we can see what that looks like when it gets to the line limit. How how it display? Only time will tell."
                    ),
                    LogEntity.mock(
                        level = LogLevel.ERROR,
                        throwable = Throwable("This is an error that was thrown, oh no!!!!")
                    ),
                    LogEntity.mock(
                        level = LogLevel.CRITICAL,
                        throwable = Throwable("This log has a is so very, very, very, crazy long message so we can see what that looks like when it gets to the line limit. How how it display? Only time will tell.")
                    ),
                )
            ) {
                LogCardView(logEntity = it)
            }
        }
    }
}