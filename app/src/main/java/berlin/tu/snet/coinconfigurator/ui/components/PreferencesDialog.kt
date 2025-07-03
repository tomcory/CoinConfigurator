package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import berlin.tu.snet.coinconfigurator.R
import berlin.tu.snet.coinconfigurator.data.LanguageManager
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigViewModel
import kotlinx.coroutines.launch

@Composable
fun PreferencesDialog(
    viewModel: ConfigViewModel,
    onDismissRequest: () -> Unit = {}
) {
    val preferences = viewModel.configPreferences
    val scope = rememberCoroutineScope()

    // Store color values in individual state objects to avoid delegate issues
    val colorHead1 = remember { mutableStateOf("") }
    val colorHead2 = remember { mutableStateOf("") }
    val colorHead3 = remember { mutableStateOf("") }
    val colorHead4 = remember { mutableStateOf("") }
    val colorHead5 = remember { mutableStateOf("") }

    // Create a lookup map for ease of use
    val colorHeadStates = mapOf(
        1 to colorHead1,
        2 to colorHead2,
        3 to colorHead3,
        4 to colorHead4,
        5 to colorHead5
    )

    // State for validation errors
    val errorState = remember { mutableStateMapOf<Int, String>() }
    
    // BASE_URL state
    val baseUrlFlow = preferences.baseUrl
    val baseUrlFromPrefs by baseUrlFlow.collectAsState(initial = "")
    var baseUrlState by remember { mutableStateOf("") }
    var originalBaseUrl by remember { mutableStateOf("") }
    val baseUrlError = remember { mutableStateOf<String?>(null) }

    // Language selection state
    val context = LocalContext.current
    val availableLanguages = LanguageManager.getAvailableLanguages()
    var selectedLanguage by remember { mutableStateOf(LanguageManager.getCurrentLanguage(context)) }

    // Get string resources
    val baseUrlEmptyText = stringResource(R.string.base_url_empty)
    val baseUrlMustStartWithHttpText = stringResource(R.string.base_url_must_start_with_http)
    val baseUrlMustHaveValidHostText = stringResource(R.string.base_url_must_have_valid_host)
    val baseUrlNotValidText = stringResource(R.string.base_url_not_valid)
    val colorMustBe6HexDigitsText = stringResource(R.string.color_must_be_6_hex_digits)
    val invalidColorFormatText = stringResource(R.string.invalid_color_format)

    // Load current colors and BASE_URL reactively
    LaunchedEffect(baseUrlFromPrefs) {
        val colors = viewModel.colorPrintHeadMap.value
        colorHead1.value = String.format("%06X", 0xFFFFFF and (colors[1]?.toInt() ?: 0xFF0000))
        colorHead2.value = String.format("%06X", 0xFFFFFF and (colors[2]?.toInt() ?: 0x00FF00))
        colorHead3.value = String.format("%06X", 0xFFFFFF and (colors[3]?.toInt() ?: 0x0000FF))
        colorHead4.value = String.format("%06X", 0xFFFFFF and (colors[4]?.toInt() ?: 0xFFFF00))
        colorHead5.value = String.format("%06X", 0xFFFFFF and (colors[5]?.toInt() ?: 0x00FFFF))
        baseUrlState = baseUrlFromPrefs
        originalBaseUrl = baseUrlFromPrefs
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.printing_head_colors_settings)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // BASE_URL field
                OutlinedTextField(
                    value = baseUrlState,
                    onValueChange = {
                        baseUrlState = it
                        baseUrlError.value = null
                    },
                    label = { Text(stringResource(R.string.base_url)) },
                    singleLine = true,
                    isError = baseUrlError.value != null,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Uri,
                        autoCorrectEnabled = false
                    )
                )
                if (baseUrlError.value != null) {
                    Text(
                        text = baseUrlError.value ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    stringResource(R.string.define_colors_for_printing_heads),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                for (headNumber in 1..5) {
                    val colorState = colorHeadStates[headNumber]!!

                    ColorInputRow(
                        headNumber = headNumber,
                        colorValue = colorState.value,
                        onColorChange = { colorState.value = it },
                        errorMessage = errorState[headNumber],
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Language selection section
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.language_selection), style = MaterialTheme.typography.bodyMedium)
                Column {
                    availableLanguages.forEach { language ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedLanguage == language.code,
                                onClick = {
                                    if (selectedLanguage != language.code) {
                                        selectedLanguage = language.code
                                        LanguageManager.setLanguage(context, language.code)
                                        // Immediately close dialog and restart activity
                                        onDismissRequest()
                                        LanguageManager.restartActivity(context)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(language.displayName)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Clear previous errors
                    errorState.clear()
                    baseUrlError.value = null
                    
                    // Validate and save colors
                    var allValid = true
                    
                    // Validate BASE_URL (must start with http:// or https:// and not be empty)
                    if (baseUrlState.isBlank()) {
                        baseUrlError.value = baseUrlEmptyText
                        allValid = false
                    } else if (!baseUrlState.startsWith("https://")) {
                        baseUrlError.value = baseUrlMustStartWithHttpText
                        allValid = false
                    } else {
                        // Parse the URL to check for a valid host
                        try {
                            val url = java.net.URL(if (baseUrlState.endsWith("/")) baseUrlState else "$baseUrlState/")
                            if (url.host.isNullOrBlank()) {
                                baseUrlError.value = baseUrlMustHaveValidHostText
                                allValid = false
                            }
                        } catch (e: Exception) {
                            baseUrlError.value = baseUrlNotValidText
                            allValid = false
                        }
                    }
                    
                    // Validate all colors
                    colorHeadStates.forEach { (headNumber, state) ->
                        val hexValue = state.value
                        if (hexValue.length != 6) {
                            errorState[headNumber] = colorMustBe6HexDigitsText
                            allValid = false
                        } else {
                            try {
                                hexValue.toLong(16)
                            } catch (e: Exception) {
                                errorState[headNumber] = invalidColorFormatText
                                allValid = false
                            }
                        }
                    }
                    
                    // If all values are valid, save them
                    if (allValid) {
                        scope.launch {
                            // Save BASE_URL (append '/' if missing)
                            val urlToSave = if (baseUrlState.endsWith("/")) baseUrlState else "$baseUrlState/"
                            preferences.setBaseUrl(urlToSave)
                            // Save colors
                            colorHeadStates.forEach { (headNumber, state) ->
                                val hexValue = state.value
                                val colorLong = hexValue.toLong(16) or 0xFF000000
                                preferences.setPrintingHeadColor(colorLong, headNumber)
                            }
                            // Update UI immediately
                            viewModel.updatePrintHeadColor()
                            // If BASE_URL changed, reload all parameters
                            if (baseUrlState != originalBaseUrl) {
                                viewModel.reloadConfig()
                            }
                            
                            // Check if language changed and restart if needed
                            val currentLanguage = LanguageManager.getCurrentLanguage(context)
                            if (selectedLanguage != currentLanguage) {
                                // Close dialog first, then restart
                                onDismissRequest()
                                LanguageManager.restartActivity(context)
                            } else {
                                // Just close the dialog
                                onDismissRequest()
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ColorInputRow(
    headNumber: Int,
    colorValue: String,
    onColorChange: (String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Color preview
        val color = try {
            Color("#$colorValue".toColorInt())
        } catch (e: Exception) {
            Color.Gray
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color)
                .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
        )

        Column(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = colorValue,
                onValueChange = { value ->
                    // Accept only hex characters and limit to 6 characters
                    if (value.matches(Regex("^[0-9A-Fa-f]{0,6}$"))) {
                        onColorChange(value.uppercase())
                    }
                },
                label = { Text(stringResource(R.string.printing_head_color_hex, headNumber)) },
                prefix = { Text("#") },
                singleLine = true,
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }
    }
}
