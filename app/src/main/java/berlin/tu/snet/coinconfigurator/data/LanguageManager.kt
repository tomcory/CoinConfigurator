package berlin.tu.snet.coinconfigurator.data

import android.content.Context
import android.util.Log

/**
 * Manager for handling language switching between English and German
 */
object LanguageManager {
    
    private const val LANGUAGE_PREF_KEY = "selected_language"
    private const val ENGLISH = "en"
    private const val GERMAN = "de"
    
    /**
     * Set the app language
     */
    fun setLanguage(context: Context, languageCode: String) {
        Log.d("LanguageManager", "Setting language to: $languageCode")
        // Save the selected language
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .edit()
            .putString(LANGUAGE_PREF_KEY, languageCode)
            .apply()
        Log.d("LanguageManager", "Language saved to preferences")
    }
    
    /**
     * Get the current language code
     */
    fun getCurrentLanguage(context: Context): String {
        val language = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .getString(LANGUAGE_PREF_KEY, ENGLISH) ?: ENGLISH
        Log.d("LanguageManager", "Retrieved language from preferences: $language")
        return language
    }
    
    /**
     * Apply the saved language on app startup
     */
    fun applySavedLanguage(context: Context) {
        val languageCode = getCurrentLanguage(context)
        setLanguage(context, languageCode)
    }
    
    /**
     * Restart the activity to apply language changes
     */
    fun restartActivity(context: Context) {
        Log.d("LanguageManager", "Restarting activity to apply language changes")
        if (context is android.app.Activity) {
            val intent = context.intent
            context.finish()
            context.startActivity(intent)
            @Suppress("DEPRECATION")
            context.overridePendingTransition(0, 0)
        }
    }
    
    /**
     * Get available languages
     */
    fun getAvailableLanguages(): List<Language> {
        return listOf(
            Language(ENGLISH, "English"),
            Language(GERMAN, "Deutsch")
        )
    }
    
    data class Language(
        val code: String,
        val displayName: String
    )
} 