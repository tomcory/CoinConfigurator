package berlin.tu.snet.coinconfigurator.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network module that provides Retrofit and API service instances
 */
object NetworkModule {
    private const val TIMEOUT_SECONDS = 30L
    
    /**
     * Create OkHttpClient with logging
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Create Retrofit instance with dynamic baseUrl
     */
    private fun createRetrofit(baseUrl: String): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = true
            encodeDefaults = true
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    /**
     * Provide ConfigApiService with dynamic baseUrl
     */
    fun provideConfigApiService(baseUrl: String): ConfigApiService {
        return createRetrofit(baseUrl).create(ConfigApiService::class.java)
    }
}
