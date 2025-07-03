package berlin.tu.snet.coinconfigurator.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import berlin.tu.snet.coinconfigurator.data.ConfigPreferences
import berlin.tu.snet.coinconfigurator.model.Parameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for handling configuration data and user interactions
 */
class ConfigViewModel(
    private val repository: ConfigRepository,
    val configPreferences: ConfigPreferences
) : ViewModel() {

    // Track if data has been loaded already to prevent redundant reloads
    private var dataLoaded = false

    // State for machine config data
    private val _coinWidth = MutableStateFlow<Parameter?>(null)
    val coinWidth: StateFlow<Parameter?> = _coinWidth.asStateFlow()

    private val _coinHeight = MutableStateFlow<Parameter?>(null)
    val coinHeight: StateFlow<Parameter?> = _coinHeight.asStateFlow()

    private val _coinColorPrintHead = MutableStateFlow<Parameter?>(null)
    val coinColorPrintHead: StateFlow<Parameter?> = _coinColorPrintHead.asStateFlow()

    private val _logo = MutableStateFlow<Parameter?>(null)
    val logo: StateFlow<Parameter?> = _logo.asStateFlow()

    private val _logoColorPrintHead = MutableStateFlow<Parameter?>(null)
    val logoColorPrintHead: StateFlow<Parameter?> = _logoColorPrintHead.asStateFlow()

    // State for loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // State for success messages
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // State for preferences dialog
    private val _showPreferencesDialog = MutableStateFlow(false)
    val showPreferencesDialog: StateFlow<Boolean> = _showPreferencesDialog.asStateFlow()

    private val coinWidthId = "d9f2f73b-1ce1-4951-a377-0a6f4ca0b5a3"
    private val coinHeightId = "378a4533-e4e1-4e5d-96b0-2bf2acb3bdfb"
    private val coinColorPrintHeadId = "95ee9a73-2c1e-484c-bf42-a8b22c031966"
    private val logoId = "34867e05-9c0b-4031-b81c-16aed8bbbcd0"
    private val logoColorPrintHeadId = "4902ccfc-d8fb-453e-8ef8-34b06bbdce44"

    val coinWidthRange = listOf(20.0f, 23.25f, 25.75f, 30.0f)
    val coinHeightRange = listOf(2.1f, 4.0f)

    private val parameterFlowMap = mapOf(
        coinWidthId to _coinWidth,
        coinHeightId to _coinHeight,
        coinColorPrintHeadId to _coinColorPrintHead,
        logoId to _logo,
        logoColorPrintHeadId to _logoColorPrintHead
    )

    private val _colorPrintHeadMap = MutableStateFlow<Map<Int, Long>>(mapOf())
    val colorPrintHeadMap: StateFlow<Map<Int, Long>> = _colorPrintHeadMap.asStateFlow()

    suspend fun loadConfig() {
        // Only load data if it hasn't been loaded before
        if (dataLoaded && !_isLoading.value &&
            _coinWidth.value != null && _coinHeight.value != null &&
            _coinColorPrintHead.value != null && _logo.value != null &&
            _logoColorPrintHead.value != null
        ) {
            return
        }

        _isLoading.value = true
        parameterFlowMap.forEach { (parameterId, _) ->
            loadParameter(parameterId)
        }

        updatePrintHeadColor()

        _isLoading.value = false
        dataLoaded = true
    }

    private suspend fun getColorsForPrintHeads(): Map<Int, Long> {
        Log.d("getColorsForPrintHeads", "Getting colors for print heads")
        return mapOf(
            1 to configPreferences.getColorForPrintingHead(1),
            2 to configPreferences.getColorForPrintingHead(2),
            3 to configPreferences.getColorForPrintingHead(3),
            4 to configPreferences.getColorForPrintingHead(4),
            5 to configPreferences.getColorForPrintingHead(5)
        )
    }

    /**
     * Load the machine configuration data
     */
    private fun loadParameter(parameterId: String) {
        viewModelScope.launch {
            _errorMessage.value = null

            repository.getParameterConfig(parameterId)
                .catch { e ->
                    _errorMessage.value = "Failed to load configuration: ${e.message}"
                }
                .collect { result ->
                    result.onSuccess { newValue ->
                        parameterFlowMap[parameterId]?.value = newValue
                    }.onFailure { error ->
                        _errorMessage.value = "Failed to load configuration: ${error.message}"
                    }
                }
        }
    }

    /**
     * Update a parameter value
     *
     * @param parameter The parameter to update
     */
    fun updateParameter(parameter: Parameter, newValue: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            repository.updateParameter(
                parameter.copy(content = listOf(parameter.content.first().copy(value = newValue)))
            )
                .catch { e ->
                    Log.e("ParameterUpdate", "Failed to update parameter", e)
                    _errorMessage.value = "Failed to update parameter: ${e.message}"
                    _isLoading.value = false
                }
                .collect { result ->
                    result.onSuccess {
                        loadParameter(parameter.id)
                        Log.d("ParameterUpdate", "Parameter updated successfully")
                        _successMessage.value = "Parameter updated successfully"
                        _isLoading.value = false
                    }.onFailure { error ->
                        Log.e("ParameterUpdate", "Failed to update parameter", error)
                        _errorMessage.value = "Failed to update parameter: ${error.message}"
                        _isLoading.value = false
                    }
                }
        }
    }

    /**
     * Toggle the visibility of the preferences dialog
     */
    fun togglePreferencesDialog() {
        _showPreferencesDialog.value = !_showPreferencesDialog.value

        // When closing the dialog, refresh the color map
        if (!_showPreferencesDialog.value) {
            viewModelScope.launch {
                updatePrintHeadColor()
            }
        }
    }

    /**
     * Update colorPrintHeadMap with a new color for a specific print head
     * Used for immediate UI updates when colors change in the preferences dialog
     */
    suspend fun updatePrintHeadColor() {
        _colorPrintHeadMap.value = getColorsForPrintHeads()
    }

    // Reload all config data (e.g., after BASE_URL change)
    suspend fun reloadConfig() {
        dataLoaded = false
        loadConfig()
    }
}
