package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import berlin.tu.snet.coinconfigurator.R
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigViewModel

@Composable
fun LogoSelector(
    viewModel: ConfigViewModel
) {

    val parameter by viewModel.logo.collectAsState()

    val logoText = stringResource(R.string.logo)
    val logoNoneText = stringResource(R.string.logo_none)
    val logoProceedText = stringResource(R.string.logo_proceed)
    val logoSnetText = stringResource(R.string.logo_snet)
    val logoTubText = stringResource(R.string.logo_tub)


    val value = parameter?.content?.getOrNull(0)?.value ?: "None"

    var selectedLogo = value

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = logoText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.8f).height(96.dp).padding(0.dp)
        ) {
            // None option
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val noneBackgroundColor by animateColorAsState(
                    targetValue = if (selectedLogo == "None")
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface,
                    animationSpec = tween(300),
                    label = "noneBackgroundColor"
                )

                val noneContentColor by animateColorAsState(
                    targetValue = if (selectedLogo == "None")
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        Color.Gray,
                    animationSpec = tween(300),
                    label = "noneContentColor"
                )

                val noneSize by animateDpAsState(
                    targetValue = if (selectedLogo == "None") 96.dp else 64.dp,
                    animationSpec = tween(300),
                    label = "noneSize"
                )

                FilledIconButton(
                    onClick = {
                        selectedLogo = "None"
                        parameter?.let { viewModel.updateParameter(it, selectedLogo) }
                    },
                    modifier = Modifier.size(noneSize),
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = noneBackgroundColor,
                        contentColor = noneContentColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = logoNoneText,
                        modifier = Modifier.size(noneSize / 2)
                    )
                }

                Text(
                    text = logoNoneText,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Proceed logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val proceedBackgroundColor by animateColorAsState(
                    targetValue = if (selectedLogo == "PROCEED")
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface,
                    animationSpec = tween(300),
                    label = "proceedBackgroundColor"
                )

                val proceedSize by animateDpAsState(
                    targetValue = if (selectedLogo == "PROCEED") 96.dp else 64.dp,
                    animationSpec = tween(300),
                    label = "proceedSize"
                )

                FilledIconButton(
                    onClick = {
                        selectedLogo = "PROCEED"
                        parameter?.let { viewModel.updateParameter(it, selectedLogo) }
                    },
                    modifier = Modifier.size(proceedSize),
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = proceedBackgroundColor,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.proceed),
                        contentDescription = logoProceedText,
                        modifier = Modifier.size(proceedSize * 0.875f)
                    )
                }

                Text(
                    text = logoProceedText,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // SNET logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val snetBackgroundColor by animateColorAsState(
                    targetValue = if (selectedLogo == "SNET")
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface,
                    animationSpec = tween(300),
                    label = "snetBackgroundColor"
                )

                val snetSize by animateDpAsState(
                    targetValue = if (selectedLogo == "SNET") 96.dp else 64.dp,
                    animationSpec = tween(300),
                    label = "snetSize"
                )

                FilledIconButton(
                    onClick = {
                        selectedLogo = "SNET"
                        parameter?.let { viewModel.updateParameter(it, selectedLogo) }
                    },
                    modifier = Modifier.size(snetSize),
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = snetBackgroundColor,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.snet),
                        contentDescription = logoSnetText,
                        modifier = Modifier.size(snetSize * 0.875f)
                    )
                }

                Text(
                    text = logoSnetText,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // TUB logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val tubBackgroundColor by animateColorAsState(
                    targetValue = if (selectedLogo == "TUB")
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface,
                    animationSpec = tween(300),
                    label = "tubBackgroundColor"
                )

                val tubSize by animateDpAsState(
                    targetValue = if (selectedLogo == "TUB") 96.dp else 64.dp,
                    animationSpec = tween(300),
                    label = "tubSize"
                )

                FilledIconButton(
                    onClick = {
                        selectedLogo = "TUB"
                        parameter?.let { viewModel.updateParameter(it, selectedLogo) }
                    },
                    modifier = Modifier.size(tubSize),
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = tubBackgroundColor,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.tub),
                        contentDescription = logoTubText,
                        modifier = Modifier.size(tubSize * 0.875f)
                    )
                }

                Text(
                    text = logoTubText,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
