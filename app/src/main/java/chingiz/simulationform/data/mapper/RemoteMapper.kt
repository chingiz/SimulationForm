package chingiz.simulationform.data.mapper

import chingiz.simulationform.data.remote.dto.FormItemDto
import chingiz.simulationform.data.remote.dto.FormItemType
import chingiz.simulationform.data.remote.dto.ResponseSetDto
import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.model.FormItem
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.model.Response
import chingiz.simulationform.domain.model.ResponseSet
import chingiz.simulationform.domain.model.Section
import chingiz.simulationform.domain.model.TextQuestion

/** Maps the API payload to domain pages. Unknown or malformed nodes are skipped. */
fun List<FormItemDto>.toPages(): List<Page> =
    filter { it.type == FormItemType.PAGE }.map { dto ->
        Page(id = dto.id, title = dto.title.orEmpty(), items = dto.items.mapNotNull { it.toDomain() })
    }

fun FormItemDto.toDomain(): FormItem? = when (type) {
    FormItemType.PAGE -> Page(id, title.orEmpty(), items.mapNotNull { it.toDomain() })
    FormItemType.SECTION -> Section(id, title.orEmpty(), items.mapNotNull { it.toDomain() })
    FormItemType.TEXT -> TextQuestion(id, content.orEmpty())
    FormItemType.IMAGE -> src?.let { ImageQuestion(id, title = title.orEmpty(), src = it) }
    FormItemType.CHOICE -> responseSet?.let { ChoiceQuestion(id, content.orEmpty(), it.toDomain()) }
    else -> null
}

private fun ResponseSetDto.toDomain() = ResponseSet(
    id = id,
    multipleSelection = multipleSelection,
    responses = responses.map { Response(id = it.id, label = it.label, score = it.score) },
)
