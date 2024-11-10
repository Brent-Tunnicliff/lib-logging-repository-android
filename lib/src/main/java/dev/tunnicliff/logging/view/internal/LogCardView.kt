// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.view.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tunnicliff.logging.R
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.internal.database.flatCauses
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.model.localisedString
import dev.tunnicliff.ui.component.card.BaseCard
import dev.tunnicliff.ui.component.card.CardVariant
import dev.tunnicliff.ui.component.list.BaseList
import dev.tunnicliff.ui.theme.PreviewerTheme
import dev.tunnicliff.ui.theme.ThemedPreviewer

private object LogCardViewConstants {
    const val COLLAPSED_MAX_LINES = 3
    const val EXPANDED_MAX_LINES = Int.MAX_VALUE
}

@Composable
internal fun LogCardView(logEntity: LogEntity) {
    var expandedState by remember { mutableStateOf(false) }

    BaseCard(
        onClick = { expandedState = !expandedState },
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
                    maxLines = expandedState.maxLines
                )

                if (logEntity.throwable != null) {
                    ThrowableView(logEntity.throwable, expandedState)
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

@Composable
private fun ThrowableView(
    throwable: LogEntity.Throwable,
    expandedState: Boolean
) {
    Text(
        text = stringResource(
            id = R.string.log_card_error,
            throwable.toString()
        ),
        overflow = TextOverflow.Ellipsis,
        maxLines = expandedState.maxLines
    )

    if (expandedState) {
        val causes by remember { derivedStateOf { throwable.flatCauses() } }

        Column {
            for (cause in causes) {
                Text(
                    text = stringResource(
                        id = R.string.log_card_cause,
                        cause.toString()
                    )
                )
            }
        }
    }
}

private val Boolean.maxLines: Int
    get() = if (this)
        LogCardViewConstants.EXPANDED_MAX_LINES
    else
        LogCardViewConstants.COLLAPSED_MAX_LINES

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
                        throwable = LogEntity.Throwable.mock(
                            message = "This is an error that was thrown, oh no!!!!"
                        )
                    ),
                    LogEntity.mock(
                        level = LogLevel.CRITICAL,
                        throwable = LogEntity.Throwable.mock(
                            message = "This log has a is so very, very, very, crazy long message so we can see what that looks like when it gets to the line limit. How how it display? Only time will tell."
                        )
                    ),
                    LogEntity.mock(
                        level = LogLevel.DEBUG,
                        throwable = LogEntity.Throwable.mock(
                            message = "Click me to see cause.",
                            cause = LogEntity.Throwable.mock(
                                message = "This is the cause.",
                                cause = LogEntity.Throwable.mock(message = "This is a nested cause.")
                            )
                        )
                    ),
                )
            ) {
                LogCardView(logEntity = it)
            }
        }
    }
}