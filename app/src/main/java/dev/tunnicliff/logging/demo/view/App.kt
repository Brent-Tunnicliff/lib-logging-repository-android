// Copyright © 2025 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.tunnicliff.logging.demo.view.helper.DemoOptionsView
import dev.tunnicliff.logging.view.logsView
import dev.tunnicliff.logging.view.navigateToLogsView
import dev.tunnicliff.ui.component.navigation.DefaultNavHost
import dev.tunnicliff.ui.component.navigation.MenuActionOptions
import dev.tunnicliff.ui.component.navigation.SimpleTopAppBar
import dev.tunnicliff.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    AppTheme {
        Scaffold(topBar = {
            SimpleTopAppBar(
                navController = navController,
                title = currentBackStackEntry?.destination?.label?.toString() ?: "",
                menuActionOptions = MenuActionOptions(
                    navHostController = navController,
                    navigateToLogs = { navController.navigateToLogsView() },
                    additionalOptions = listOf(
                        MenuActionOptions.Option(
                            title = "Show Bottom Sheet",
                            navigateToOption = { showBottomSheet = true }
                        )
                    )
                )
            )
        }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                DefaultNavHost(
                    navController = navController,
                    startDestination = START_DESTINATION
                ) {
                    logsView(context)
                    mainView(context)
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    DemoOptionsView()
                }
            }
        }
    }
}
