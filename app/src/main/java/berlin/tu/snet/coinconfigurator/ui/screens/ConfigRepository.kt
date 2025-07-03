package berlin.tu.snet.coinconfigurator.ui.screens

import android.util.Log
import berlin.tu.snet.coinconfigurator.data.ConfigPreferences
import berlin.tu.snet.coinconfigurator.model.Parameter
import berlin.tu.snet.coinconfigurator.network.ConfigApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * Repository for handling configuration data operations
 */
class ConfigRepository(
    private val apiService: ConfigApiService,
    private val configPreferences: ConfigPreferences
) {
    private val TAG = "ConfigRepository"

    fun getParameterConfig(
        parameterId: String
    ): Flow<Result<Parameter>> = flow {
        try {
            val spaceId = configPreferences.spaceId.first()
            val configContainerId = configPreferences.configContainerId.first()
            val versionId = configPreferences.versionId.first()

            Log.d(TAG, "Fetching parameter: $parameterId")
            Log.d(TAG, "spaces/$spaceId/configurations/$configContainerId/$versionId/$parameterId")

            if (spaceId.isEmpty() || configContainerId.isEmpty() || versionId.isEmpty()) {
                emit(Result.failure(IllegalStateException("Configuration IDs not set")))
                return@flow
            }

            val response =
                apiService.getParameter(spaceId, configContainerId, versionId, parameterId)
            if (response.isSuccessful) {
                val parameter = response.body()
                if (parameter != null) {
                    emit(Result.success(parameter))
                } else {
                    emit(Result.failure(IllegalStateException("Empty response body")))
                }
            } else {
                emit(Result.failure(RuntimeException("Error: ${response.code()} - ${response.message()}")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching parameter", e)
            emit(Result.failure(e))
        }
    }

    /**
     * Update a parameter in the configuration
     *
     * @param parameter Updated parameter data
     * @return Flow indicating success or failure
     */
    fun updateParameter(parameter: Parameter): Flow<Result<Unit>> = flow {
        try {
            val spaceId = configPreferences.spaceId.first()
            val configContainerId = configPreferences.configContainerId.first()
            val versionId = configPreferences.versionId.first()

            Log.d(TAG, "Updating parameter: $parameter")
            Log.d(TAG, "spaces/$spaceId/configurations/$configContainerId/$versionId/${parameter.id}")

            Log.d(TAG, parameter.toString())

            if (spaceId.isEmpty() || configContainerId.isEmpty() || versionId.isEmpty()) {
                emit(Result.failure(IllegalStateException("Configuration IDs not set")))
                return@flow
            }

            val response = apiService.updateParameter(
                spaceId, configContainerId, versionId, parameter.id, parameter
            )

            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(RuntimeException("Error: ${response.code()} - ${response.message()}")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating parameter", e)
            emit(Result.failure(e))
        }
    }
}