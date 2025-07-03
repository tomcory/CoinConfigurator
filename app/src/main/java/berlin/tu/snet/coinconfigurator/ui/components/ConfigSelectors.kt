package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigViewModel

@Composable
fun ConfigSelectors(
    viewModel: ConfigViewModel
) {
    // Detect orientation
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (isLandscape) 8.dp else 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoinWidthSelector(viewModel)
        CoinHeightSelector(viewModel)
        CoinColorSelector(viewModel)
        LogoSelector(viewModel)
        LogoColorSelector(viewModel)
    }
}
