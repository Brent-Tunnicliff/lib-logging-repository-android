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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tunnicliff.logging.demo.container.ViewModelFactory
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.ui.component.button.SimpleButton
import dev.tunnicliff.ui.component.card.BaseCard
import dev.tunnicliff.ui.component.picker.BasicPickerValue
import dev.tunnicliff.ui.component.picker.SimplePicker
import dev.tunnicliff.ui.component.toggle.LabeledSwitch
import dev.tunnicliff.ui.theme.PreviewerTheme
import dev.tunnicliff.ui.theme.ThemedPreviewer

@Composable
fun DemoOptionsView(viewModel: DemoOptionsViewModel = viewModel(factory = ViewModelFactory)) {
    BaseCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Send log")

            SimplePicker(
                values = LogLevel.entries.map {
                    BasicPickerValue(
                        description = it.name,
                        value = it
                    )
                },
                initialValue = viewModel.logLevel.collectAsState().value.name,
                onValueChanged = {
                    viewModel.updateLogLevel(it.value)
                }
            )

            LabeledSwitch(
                label = "Include throwable",
                checked = viewModel.includeThrowable.collectAsState().value,
                onCheckedChange = {
                    viewModel.updateIncludeThrowable(it)
                }
            )

            SimpleButton(
                text = "Send log",
                onClick = {
                    viewModel.sendLogsClicked()
                }
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            LabeledSwitch(
                label = "Keep sending random logs",
                checked = viewModel.sendRandomLogs.collectAsState().value,
                onCheckedChange = {
                    viewModel.updateSendRandomLogs(it)
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
            DemoOptionsView(
                viewModel = PreviewDemoOptionsViewModel(
                    logLevel = LogLevel.DEBUG,
                    includeThrowable = false,
                    sendRandomLogs = false
                )
            )
        }
    }
}