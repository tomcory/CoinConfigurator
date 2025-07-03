package berlin.tu.snet.coinconfigurator.network

import berlin.tu.snet.coinconfigurator.model.Parameter
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service interface for configuration data
 */
interface ConfigApiService {

    /**
     * Get the configuration for a specific parameter in the configuration.
     *
     * @param spaceId ID of the space
     * @param configContainerId ID of the config container
     * @param versionId ID of the version
     * @param parameterId ID of the parameter to retrieve
     * @return Response containing the parameter data
     */
    @GET("spaces/{spaceId}/configurations/{configContainerId}/{versionId}/{parameterId}")
    suspend fun getParameter(
        @Path("spaceId") spaceId: String,
        @Path("configContainerId") configContainerId: String,
        @Path("versionId") versionId: String,
        @Path("parameterId") parameterId: String
    ): Response<Parameter>
    
    /**
     * Update a specific parameter in the configuration
     * 
     * @param spaceId ID of the space
     * @param configContainerId ID of the config container
     * @param versionId ID of the version
     * @param parameterId ID of the parameter to update
     * @param parameter Updated parameter data
     * @return Response indicating success or failure
     */
    @PUT("spaces/{spaceId}/configurations/{configContainerId}/{versionId}/{parameterId}")
    suspend fun updateParameter(
        @Path("spaceId") spaceId: String,
        @Path("configContainerId") configContainerId: String,
        @Path("versionId") versionId: String,
        @Path("parameterId") parameterId: String,
        @Body parameter: Parameter
    ): Response<Void>
}
