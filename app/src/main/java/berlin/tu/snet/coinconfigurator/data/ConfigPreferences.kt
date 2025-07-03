package berlin.tu.snet.coinconfigurator.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Preferences DataStore for configuration-related values
 */
class ConfigPreferences(private val context: Context) {

    // Define preference keys
    companion object {
        // DataStore instance at the Context level
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "config_preferences")

        // Key for connection
        private val BASE_URL = stringPreferencesKey("base_url")

        // Keys for configuration values
        private val SPACE_ID = stringPreferencesKey("space_id")
        private val CONFIG_CONTAINER_ID = stringPreferencesKey("config_container_id")
        private val VERSION_ID = stringPreferencesKey("version_id")

        // Color to printing head mapping keys
        private val COLOR_HEAD_1 = longPreferencesKey("color_head_1")
        private val COLOR_HEAD_2 = longPreferencesKey("color_head_2")
        private val COLOR_HEAD_3 = longPreferencesKey("color_head_3")
        private val COLOR_HEAD_4 = longPreferencesKey("color_head_4")
        private val COLOR_HEAD_5 = longPreferencesKey("color_head_5")
    }

    // Get baseUrl as a Flow
    val baseUrl: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[BASE_URL] ?: ""
        }

    // Get spaceId as a Flow
    val spaceId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SPACE_ID] ?: ""
        }

    // Get configContainerId as a Flow
    val configContainerId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CONFIG_CONTAINER_ID] ?: ""
        }

    // Get versionId as a Flow
    val versionId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[VERSION_ID] ?: ""
        }

    // Set spaceId
    suspend fun setSpaceId(value: String) {
        context.dataStore.edit { preferences ->
            preferences[SPACE_ID] = value
        }
    }

    // Set configContainerId
    suspend fun setConfigContainerId(value: String) {
        context.dataStore.edit { preferences ->
            preferences[CONFIG_CONTAINER_ID] = value
        }
    }

    // Set versionId
    suspend fun setVersionId(value: String) {
        context.dataStore.edit { preferences ->
            preferences[VERSION_ID] = value
        }
    }

    // Set baseUrl
    suspend fun setBaseUrl(value: String) {
        context.dataStore.edit { preferences ->
            preferences[BASE_URL] = value
        }
    }

    /**
     * Get the printing head number for a given color
     */
    suspend fun getColorForPrintingHead(headNumber: Int): Long {
        return when(headNumber.coerceIn(1, 5)) {
            1 -> context.dataStore.data.first()[COLOR_HEAD_1] ?: 0xFF000000
            2 -> context.dataStore.data.first()[COLOR_HEAD_2] ?: 0xFF000000
            3 -> context.dataStore.data.first()[COLOR_HEAD_3] ?: 0xFF000000
            4 -> context.dataStore.data.first()[COLOR_HEAD_4] ?: 0xFF000000
            5 -> context.dataStore.data.first()[COLOR_HEAD_5] ?: 0xFF000000
            else -> 1 // Default to first head's color
        }
    }

    /**
     * Set the printing head number for a given color
     */
    suspend fun setPrintingHeadColor(color: Long, headNumber: Int) {
        Log.d("DataStoreManager", "setPrintingHeadColor: $color, $headNumber")
        context.dataStore.edit { preferences ->
            when (headNumber.coerceIn(1, 5)) {
                1 -> preferences[COLOR_HEAD_1] = color
                2 -> preferences[COLOR_HEAD_2] = color
                3 -> preferences[COLOR_HEAD_3] = color
                4 -> preferences[COLOR_HEAD_4] = color
                5 -> preferences[COLOR_HEAD_5] = color
            }
        }
    }

    /**
     * Initialize default color to printing head mappings
     */
    suspend fun initializeDefaultColorMappings() {
        Log.d("DataStoreManager", "initializeDefaultColorMappings")
        context.dataStore.edit { preferences ->
            preferences[COLOR_HEAD_1] = 0xFFFF0000
            preferences[COLOR_HEAD_2] = 0xFF00FF00
            preferences[COLOR_HEAD_3] = 0xFF0000FF
            preferences[COLOR_HEAD_4] = 0xFFFFFF00
            preferences[COLOR_HEAD_5] = 0xFF00FFFF
        }
    }
}
