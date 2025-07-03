package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import berlin.tu.snet.coinconfigurator.R
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigViewModel

@Composable
fun LogoColorSelector(
    viewModel: ConfigViewModel
) {

    val parameter by viewModel.logoColorPrintHead.collectAsState()
    val colorMap by viewModel.colorPrintHeadMap.collectAsState()

    val logoColorText = stringResource(R.string.logo_color_print_head)

    val value = try {
        parameter?.content?.getOrNull(0)?.value?.toInt() ?: 1
    } catch (e: NumberFormatException) {
        1
    }

    var selectedHead = value

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = logoColorText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.8f).height(96.dp).padding(0.dp)
        ) {
            colorMap.forEach { (head, color) ->
                ColorBlob(
                    color = color,
                    isSelected = head == selectedHead,
                    onClick = {
                        viewModel.updateParameter(parameter!!, head.toString())
                        selectedHead = head
                    }
                )
            }
        }
    }
}
