package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import berlin.tu.snet.coinconfigurator.R
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigViewModel

@Composable
fun CoinHeightSelector(
    viewModel: ConfigViewModel
) {
    val parameter by viewModel.coinHeight.collectAsState()

    val coinHeightText = stringResource(id = R.string.coin_height)

    val value = try {
        parameter?.content?.getOrNull(0)?.value?.toFloat() ?: 0.0f
    } catch (e: NumberFormatException) {
        0.0f
    }
    val unit = parameter?.content?.getOrNull(0)?.unit ?: "mm"
    val valueRange = viewModel.coinHeightRange
    var sliderPosition by remember { mutableFloatStateOf(value) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$coinHeightText: ${"%.1f".format(sliderPosition)} $unit",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = valueRange.first() .. valueRange.last(),
            onValueChangeFinished = {
                viewModel.updateParameter(parameter!!, sliderPosition.toString())
            }
        )
    }
}