package berlin.tu.snet.coinconfigurator.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a parameter in the machine configuration
 */
@Serializable
data class Parameter(
    val id: String,
    val key: String,
    val type: String,
    val content: List<ParameterContent>,
    val parentId: String,
    val parameters: Map<String, Parameter> = emptyMap(),
    val parentType: String,
    val linkedParameters: List<String> = emptyList()
)

/**
 * Data class representing parameter content
 */
@Serializable
data class ParameterContent(
    val unit: String,
    val value: String,
    val language: String,
    val displayName: String
)

