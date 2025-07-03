package berlin.tu.snet.coinconfigurator

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import berlin.tu.snet.coinconfigurator.data.ConfigPreferences
import berlin.tu.snet.coinconfigurator.data.LanguageManager
import berlin.tu.snet.coinconfigurator.data.PreferencesManager
import berlin.tu.snet.coinconfigurator.network.NetworkModule
import berlin.tu.snet.coinconfigurator.ui.components.BaseUrlEntryScreen
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigRepository
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigScreen
import berlin.tu.snet.coinconfigurator.ui.screens.ConfigViewModel
import berlin.tu.snet.coinconfigurator.ui.theme.CoinConfiguratorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    // Create dependencies
    private lateinit var configPreferences: ConfigPreferences

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun attachBaseContext(newBase: Context) {
        val languageCode = LanguageManager.getCurrentLanguage(newBase)
        val locale = Locale(languageCode)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up modern fullscreen mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Initialize preferences
        PreferencesManager.initialize(applicationContext)
        configPreferences = PreferencesManager.getConfigPreferences(applicationContext)

        // Set default configuration IDs if needed
        initializeConfigIds()

        // Initialize color to printing head mappings
        initializeColorMappings()

        enableEdgeToEdge()
        setContent {
            val isSystemDark = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(isSystemDark) }

            // Observe BASE_URL from preferences
            val baseUrlFlow = configPreferences.baseUrl
            val baseUrlRaw by baseUrlFlow.collectAsState(initial = "")

            // Always use a valid BASE_URL
            val baseUrl = if (baseUrlRaw.isBlank()) "" else if (baseUrlRaw.endsWith("/")) baseUrlRaw else "$baseUrlRaw/"

            // If baseUrlRaw is blank, show a full-screen prompt for BASE_URL entry
            if (baseUrlRaw.isBlank()) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BaseUrlEntryScreen(onBaseUrlEntered = { url ->
                        coroutineScope.launch {
                            // Save BASE_URL (append '/' if missing)
                            val urlToSave = if (url.endsWith("/")) url else "$url/"
                            configPreferences.setBaseUrl(urlToSave)
                        }
                    })
                }
            } else {
                // Hold dependencies in Compose state, update when baseUrl changes
                val configApiService by remember(baseUrl) {
                    mutableStateOf(NetworkModule.provideConfigApiService(baseUrl))
                }
                val configRepository by remember(baseUrl) {
                    mutableStateOf(ConfigRepository(configApiService, configPreferences))
                }
                val configViewModel by remember(baseUrl) {
                    mutableStateOf(ConfigViewModel(configRepository, configPreferences))
                }

                CoinConfiguratorTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ConfigScreen(
                            viewModel = configViewModel,
                            isDarkTheme = darkTheme,
                            onThemeToggle = { darkTheme = it }
                        )
                    }
                }
            }
        }
    }

    /**
     * Initialize default color to printing head mappings
     */
    private fun initializeColorMappings() {
        coroutineScope.launch {
            configPreferences.initializeDefaultColorMappings()
        }
    }

    /**
     * Set default configuration IDs for testing
     */
    private fun initializeConfigIds() {
        coroutineScope.launch {
            // Provide some default values for testing
            configPreferences.setSpaceId("proceed-default-no-iam-user")
            configPreferences.setConfigContainerId("2a106465-669c-4faa-b15f-4c8c71c82554")
            configPreferences.setVersionId("latest")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfigPreview() {
    CoinConfiguratorTheme {
        // Preview content will be implemented later
    }
}