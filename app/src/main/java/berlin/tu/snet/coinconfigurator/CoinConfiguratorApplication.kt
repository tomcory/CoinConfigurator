package berlin.tu.snet.coinconfigurator

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import berlin.tu.snet.coinconfigurator.data.LanguageManager
import java.util.Locale

class CoinConfiguratorApplication : Application() {
    
    override fun attachBaseContext(base: Context) {
        // Apply the saved language before the application is created
        val languageCode = LanguageManager.getCurrentLanguage(base)
        Log.d("CoinConfiguratorApp", "Setting language to: $languageCode")
        
        val locale = when (languageCode) {
            "de" -> Locale("de")
            else -> Locale("en")
        }
        
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        
        val context = base.createConfigurationContext(config)
        Log.d("CoinConfiguratorApp", "Created configuration context with locale: ${context.resources.configuration.locales[0]}")
        
        super.attachBaseContext(context)
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d("CoinConfiguratorApp", "Application created with locale: ${resources.configuration.locales[0]}")
        // Any application-level initialization can go here
    }
} 