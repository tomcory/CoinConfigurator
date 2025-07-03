package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import berlin.tu.snet.coinconfigurator.R
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigViewModel

@Composable
fun CoinViewer(
    viewModel: ConfigViewModel,
    modifier: Modifier = Modifier
) {
    // Collect states from the ViewModel
    val coinWidth by viewModel.coinWidth.collectAsState()
    val coinColorPrintHead by viewModel.coinColorPrintHead.collectAsState()
    val logo by viewModel.logo.collectAsState()
    val logoColorPrintHead by viewModel.logoColorPrintHead.collectAsState()
    val colorPrintHeadMap by viewModel.colorPrintHeadMap.collectAsState()

    // Detect orientation
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Extract actual values
    val coinWidthValue = coinWidth?.content?.getOrNull(0)?.value?.toFloatOrNull() ?: 20.0f
    val coinColorPrintHeadValue =
        coinColorPrintHead?.content?.getOrNull(0)?.value?.toIntOrNull() ?: 1
    val logoValue = logo?.content?.getOrNull(0)?.value ?: "None"
    val logoColorPrintHeadValue =
        logoColorPrintHead?.content?.getOrNull(0)?.value?.toIntOrNull() ?: 1

    // Get colors from print head map
    val coinColor = colorPrintHeadMap[coinColorPrintHeadValue]?.let { Color(it) } ?: Color.Gray
    val logoColor = colorPrintHeadMap[logoColorPrintHeadValue]?.let { Color(it) } ?: Color.Gray

    // Animated values
    val animatedCoinWidthValue by animateFloatAsState(
        targetValue = coinWidthValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "coinWidthAnimation"
    )

    val animatedCoinColor by animateColorAsState(
        targetValue = coinColor,
        animationSpec = tween(500),
        label = "coinColorAnimation"
    )

    val animatedLogoColor by animateColorAsState(
        targetValue = logoColor,
        animationSpec = tween(500),
        label = "logoColorAnimation"
    )

    // For initial animation
    val (isVisible, setVisible) = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        setVisible(true)
    }

    // Calculate size based on the coin width and orientation
    val baseSizeDp = if (isLandscape) 180.dp else 200.dp
    val referenceWidth = 23.25f // This is our 100% reference size
    val scaleFactor = animatedCoinWidthValue / referenceWidth
    val sizeDp = baseSizeDp * scaleFactor
    val logoSizeDp = baseSizeDp * 0.7f // Logo size doesn't change

    // Canvas for drawing the coin
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(if (isLandscape) 8.dp else 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Main coin body
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(500)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(500)
            )
        ) {
            Box(
                modifier = Modifier
                    .size(sizeDp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(animatedCoinColor),
                contentAlignment = Alignment.Center
            ) {
                // Draw logo if not "None"
                Crossfade(
                    targetState = logoValue,
                    animationSpec = tween(300),
                    label = "logoCrossfade"
                ) { currentLogo ->
                    when (currentLogo) {
                        "None" -> {
                            // No logo to display
                        }

                        "SNET" -> {
                            Icon(
                                painter = painterResource(id = R.drawable.snet),
                                contentDescription = "SNET Logo",
                                modifier = Modifier.size(logoSizeDp),
                                tint = animatedLogoColor
                            )
                        }

                        "TUB" -> {
                            Icon(
                                painter = painterResource(id = R.drawable.tub),
                                contentDescription = "TUB Logo",
                                modifier = Modifier.size(logoSizeDp),
                                tint = animatedLogoColor
                            )
                        }

                        "PROCEED" -> {
                            Icon(
                                painter = painterResource(id = R.drawable.proceed),
                                contentDescription = "PROCEED Logo",
                                modifier = Modifier.size(logoSizeDp),
                                tint = animatedLogoColor
                            )
                        }
                    }
                }
            }
        }
    }
}
