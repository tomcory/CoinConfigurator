package berlin.tu.snet.coinconfigurator.data

import android.content.Context

/**
 * Singleton provider for accessing preferences throughout the app
 */
object PreferencesManager {

    private var configPreferences: ConfigPreferences? = null

    /**
     * Get the ConfigPreferences instance
     *
     * @param context Application context
     * @return ConfigPreferences instance
     */
    fun getConfigPreferences(context: Context): ConfigPreferences {
        return configPreferences ?: synchronized(this) {
            configPreferences ?: ConfigPreferences(context.applicationContext).also {
                configPreferences = it
            }
        }
    }

    /**
     * Initialize the PreferencesManager
     * Call this in your Application class or main activity
     *
     * @param context Application context
     */
    fun initialize(context: Context) {
        if (configPreferences == null) {
            configPreferences = ConfigPreferences(context.applicationContext)
        }
    }
}