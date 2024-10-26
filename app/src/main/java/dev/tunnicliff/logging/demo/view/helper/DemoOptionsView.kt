// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.view.helper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.ui.component.button.SimpleButton
import dev.tunnicliff.ui.component.card.BaseCard
import dev.tunnicliff.ui.component.picker.BasicPickerValue
import dev.tunnicliff.ui.component.picker.SimplePicker
import dev.tunnicliff.ui.component.toggle.LabeledSwitch
import dev.tunnicliff.ui.theme.PreviewerTheme
import dev.tunnicliff.ui.theme.ThemedPreviewer

@Composable
fun DemoOptionsView(viewModel: DemoOptionsViewModel = viewModel()) {
    var logLevel by remember { mutableStateOf(LogLevel.DEBUG) }
    var includeThrowable by remember { mutableStateOf(false) }
    var numberToSend by remember { mutableIntStateOf(1) }

    BaseCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Log")

            SimplePicker(
                values = LogLevel.entries.map {
                    BasicPickerValue(
                        description = it.name,
                        value = it
                    )
                },
                initialValue = logLevel.name,
                onValueChanged = {
                    logLevel = it.value
                }
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Text(text = "Send log")

            SimplePicker(
                values = LogLevel.entries.map {
                    BasicPickerValue(
                        description = it.name,
                        value = it
                    )
                },
                initialValue = logLevel.name,
                onValueChanged = {
                    logLevel = it.value
                }
            )

            LabeledSwitch(
                label = "Include throwable",
                checked = includeThrowable,
                onCheckedChange = {
                    includeThrowable = it
                }
            )

            SimplePicker(
                values = listOf(1, 5, 10, 50).map {
                    BasicPickerValue(
                        description = it.toString(),
                        value = it
                    )
                },
                initialValue = numberToSend.toString(),
                onValueChanged = {
                    numberToSend = it.value
                }
            )

            SimpleButton(
                text = if (numberToSend == 1) "Send log" else "Send logs",
                onClick = {
                    viewModel.sendLogsClicked(
                        logLevel = logLevel,
                        includeThrowable = includeThrowable,
                        numberToSend = numberToSend
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLightTheme() = PreviewContent(PreviewerTheme.LIGHT)

@Preview
@Composable
private fun PreviewDarkTheme() = PreviewContent(PreviewerTheme.DARK)

@Composable
private fun PreviewContent(theme: PreviewerTheme) {
    ThemedPreviewer(theme) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            DemoOptionsView()
        }
    }
}