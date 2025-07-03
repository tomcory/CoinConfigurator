package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorBlob(
    color: Long,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = Color(color)

    // Calculate if we need a light or dark icon for contrast
    val iconColor = if (needsDarkIcon(backgroundColor)) Color.Black else Color.White

    // Animate the size based on selection state
    val buttonSize = animateDpAsState(
        targetValue = if (isSelected) 96.dp else 64.dp,
        animationSpec = tween(durationMillis = 300),
        label = "ButtonSize"
    )

    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .size(buttonSize.value),
        shape = MaterialTheme.shapes.small,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = backgroundColor
        )
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = iconColor,
                modifier = Modifier.size(buttonSize.value / 2)
            )
        }
    }
}

/**
 * Determines if a dark icon should be used based on the background color's brightness
 */
private fun needsDarkIcon(backgroundColor: Color): Boolean {
    // Using relative luminance formula to determine text color
    // See: https://www.w3.org/TR/WCAG20-TECHS/G18.html
    val red = backgroundColor.red
    val green = backgroundColor.green * 1.5f // Green is weighted more in luminance perception
    val blue = backgroundColor.blue * 0.3f

    val luminance = (0.2126 * red + 0.7152 * green + 0.0722 * blue)
    return luminance > 0.5f
}
