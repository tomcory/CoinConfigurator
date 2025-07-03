package berlin.tu.snet.coinconfigurator.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import berlin.tu.snet.coinconfigurator.R
import berlin.tu.snet.coinconfigurator.ui.components.CoinViewer
import berlin.tu.snet.coinconfigurator.ui.components.ConfigSelectors
import berlin.tu.snet.coinconfigurator.ui.components.PreferencesDialog


@Composable
fun ConfigScreen(
    viewModel: ConfigViewModel,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {}
) {
    LaunchedEffect(viewModel) {
        viewModel.loadConfig()
    }
    val isLoading by viewModel.isLoading.collectAsState()

    val coinWidth by viewModel.coinWidth.collectAsState()
    val coinHeight by viewModel.coinHeight.collectAsState()
    val coinColorPrintHead by viewModel.coinColorPrintHead.collectAsState()
    val logo by viewModel.logo.collectAsState()
    val logoColorPrintHead by viewModel.logoColorPrintHead.collectAsState()

    // State for showing the preferences dialog
    val showPreferencesDialog by viewModel.showPreferencesDialog.collectAsState()

    // Show preferences dialog if showPreferencesDialog is true
    if (showPreferencesDialog) {
        PreferencesDialog(
            viewModel = viewModel,
            onDismissRequest = { viewModel.togglePreferencesDialog() }
        )
    }

    // Configuration detection for UI layout only - using this approach to detect orientation
    // changes means that we only update the UI without re-fetching data
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Dark mode toggle button in top right corner
            IconButton(
                onClick = { onThemeToggle(!isDarkTheme) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.darkmode),
                    contentDescription = stringResource(R.string.toggle_dark_mode),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Print head colors settings button in top left corner
            IconButton(
                onClick = { viewModel.togglePreferencesDialog() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.print_head_colors),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isLoading && coinWidth == null && coinHeight == null && coinColorPrintHead == null && logo == null && logoColorPrintHead == null) {
                // Only show full-screen loading indicator during initial load when we have no data
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (coinWidth != null && coinHeight != null && coinColorPrintHead != null && logo != null && logoColorPrintHead != null) {
                if (isLandscape) {
                    // Landscape layout
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left 2/3 of the screen for ConfigSelectors
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .fillMaxHeight()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ConfigSelectors(viewModel)
                        }

                        // Right 1/3 of the screen for CoinViewer
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CoinViewer(viewModel)
                        }
                    }
                } else {
                    // Portrait layout
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // CoinViewer at the top
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CoinViewer(viewModel)
                        }

                        // ConfigSelectors at the bottom
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ConfigSelectors(viewModel)
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_no_internet),
                            contentDescription = stringResource(R.string.no_internet),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .size(128.dp)
                                .padding(bottom = 16.dp)
                        )
                        // You can use an icon here if you want, e.g. Icons.Default.Warning
                        Text(
                            text = stringResource(R.string.cannot_reach_api),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.check_network_and_url),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
