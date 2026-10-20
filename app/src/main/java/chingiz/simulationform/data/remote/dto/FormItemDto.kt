package chingiz.simulationform.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Flat representation of any node in the JSON tree. The `type` discriminator decides which
 * optional fields are meaningful; mapping to the strongly typed domain model happens in
 * [chingiz.simulationform.data.mapper]. Keeping the DTO flat makes parsing tolerant to
 * unknown item types instead of failing the whole response.
 */
@Serializable
data class FormItemDto(
    @SerialName("id") val id: Long,
    @SerialName("type") val type: String,
    @SerialName("title") val title: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("src") val src: String? = null,
    @SerialName("items") val items: List<FormItemDto> = emptyList(),
    @SerialName("response_set") val responseSet: ResponseSetDto? = null,
)

@Serializable
data class ResponseSetDto(
    @SerialName("id") val id: Long,
    @SerialName("multiple_selection") val multipleSelection: Boolean = false,
    @SerialName("responses") val responses: List<ResponseDto> = emptyList(),
)

@Serializable
data class ResponseDto(
    @SerialName("id") val id: Long,
    @SerialName("label") val label: String,
    @SerialName("score") val score: Int? = null,
)

object FormItemType {
    const val PAGE = "page"
    const val SECTION = "section"
    const val TEXT = "text"
    const val IMAGE = "image"
    const val CHOICE = "choice"
}
