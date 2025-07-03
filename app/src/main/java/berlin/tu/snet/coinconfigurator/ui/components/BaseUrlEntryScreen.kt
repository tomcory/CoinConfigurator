package berlin.tu.snet.coinconfigurator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import berlin.tu.snet.coinconfigurator.R

@Composable
fun BaseUrlEntryScreen(onBaseUrlEntered: (String) -> Unit) {
    val baseUrlState = remember { mutableStateOf("") }
    val baseUrlError = remember { mutableStateOf<String?>(null) }

    // Get string resources
    val enterApiBaseUrlText = stringResource(R.string.enter_api_base_url)
    val baseUrlDescriptionText = stringResource(R.string.base_url_description)
    val baseUrlLabelText = stringResource(R.string.base_url)
    val continueText = stringResource(R.string.cont)
    val baseUrlEmptyText = stringResource(R.string.base_url_empty)
    val baseUrlMustStartWithHttpText = stringResource(R.string.base_url_must_start_with_http)
    val baseUrlMustHaveValidHostText = stringResource(R.string.base_url_must_have_valid_host)
    val baseUrlNotValidText = stringResource(R.string.base_url_not_valid)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(enterApiBaseUrlText, style = MaterialTheme.typography.headlineSmall)
        Text(baseUrlDescriptionText, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = baseUrlState.value,
            onValueChange = {
                baseUrlState.value = it
                baseUrlError.value = null
            },
            label = { Text(baseUrlLabelText) },
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
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            val url = baseUrlState.value
            // Validation (same as PreferencesDialog)
            if (url.isBlank()) {
                baseUrlError.value = baseUrlEmptyText
            } else if (!url.startsWith("https://")) {
                baseUrlError.value = baseUrlMustStartWithHttpText
            } else {
                try {
                    val urlObj = java.net.URL(if (url.endsWith("/")) url else "$url/")
                    if (urlObj.host.isNullOrBlank()) {
                        baseUrlError.value = baseUrlMustHaveValidHostText
                    } else {
                        onBaseUrlEntered(url)
                    }
                } catch (e: Exception) {
                    baseUrlError.value = baseUrlNotValidText
                }
            }
        }) {
            Text(continueText)
        }
    }
}