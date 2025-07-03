package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import berlin.tu.snet.coinconfigurator.R
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigViewModel
import kotlin.math.roundToInt

@Composable
fun CoinWidthSelector(
    viewModel: ConfigViewModel
) {
    val parameter by viewModel.coinWidth.collectAsState()

    val coinWidthText = stringResource(id = R.string.coin_width)

    val value = try {
        parameter?.content?.getOrNull(0)?.value?.toFloat() ?: 0.0f
    } catch (e: NumberFormatException) {
        0.0f
    }
    val unit = parameter?.content?.getOrNull(0)?.unit ?: "mm"
    val valueRange = viewModel.coinWidthRange
    val selectedIndex = valueRange.indexOf(value)
    var sliderPosition by remember { mutableFloatStateOf(value) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$coinWidthText: ${"%.1f".format(sliderPosition)} $unit",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ValueListSlider(
            values = valueRange,
            selectedIndex = selectedIndex,
            onValueSelected = { sliderPosition = it },
            onValueSelectionFinished = {
                viewModel.updateParameter(parameter!!, sliderPosition.toString())
            }
        )
    }
}

@Composable
fun ValueListSlider(
    values: List<Float>,
    selectedIndex: Int = 0,
    onValueSelected: (Float) -> Unit = {},
    onValueSelectionFinished: (Float) -> Unit,
    formatter: (Float) -> String = { "%.2f".format(it) }
) {
    require(values.size >= 2) { "Slider needs at least two values" }
    require(selectedIndex in values.indices) { "selectedIndex is out of bounds: $selectedIndex" }

    // Slider works with a Float; we map it to an index.
    var sliderPosition by remember { mutableFloatStateOf(selectedIndex.toFloat()) }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onValueSelected(values[it.roundToInt()])
            },               // live updates (float index)
            valueRange = 0f..(values.lastIndex.toFloat()),
            steps = values.size - 2,                                // show ticks at every entry
            onValueChangeFinished = {
                // Snap to the nearest entry
                val roundedIndex = sliderPosition.roundToInt()
                    .coerceIn(values.indices)
                sliderPosition = roundedIndex.toFloat()

                // Report back the *actual* Float the user picked
                onValueSelectionFinished(values[roundedIndex])
            }
        )

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier
                .fillMaxWidth()
                // Slider’s track starts and ends one thumb-radius in,
                // which is ~12 dp in Material-3.  Mirror that here so
                // labels line up visually with the tick marks.
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            values.forEach { v ->
                Text(
                    text = formatter(v),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}