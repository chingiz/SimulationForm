package chingiz.simulationform.data.mapper

import chingiz.simulationform.data.local.entity.FormItemEntity
import chingiz.simulationform.data.local.entity.ResponseEntity
import chingiz.simulationform.data.local.entity.ResponseSetEntity
import chingiz.simulationform.data.remote.dto.FormItemType
import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.model.FormItem
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.model.Response
import chingiz.simulationform.domain.model.ResponseSet
import chingiz.simulationform.domain.model.Section
import chingiz.simulationform.domain.model.TextQuestion

data class FormEntities(
    val items: List<FormItemEntity>,
    val responseSets: List<ResponseSetEntity>,
    val responses: List<ResponseEntity>,
)

/** Flattens the domain tree into relational rows (parents always precede their children). */
fun List<Page>.toEntities(): FormEntities {
    val items = mutableListOf<FormItemEntity>()
    val sets = mutableListOf<ResponseSetEntity>()
    val responses = mutableListOf<ResponseEntity>()

    fun flatten(item: FormItem, parentId: Long?, position: Int) {
        when (item) {
            is Page -> {
                items += FormItemEntity(item.id, parentId, FormItemType.PAGE, position, title = item.title)
                item.items.forEachIndexed { index, child -> flatten(child, item.id, index) }
            }
            is Section -> {
                items += FormItemEntity(item.id, parentId, FormItemType.SECTION, position, title = item.title)
                item.items.forEachIndexed { index, child -> flatten(child, item.id, index) }
            }
            is TextQuestion ->
                items += FormItemEntity(item.id, parentId, FormItemType.TEXT, position, content = item.content)
            is ImageQuestion ->
                items += FormItemEntity(item.id, parentId, FormItemType.IMAGE, position, title = item.title, src = item.src)
            is ChoiceQuestion -> {
                items += FormItemEntity(item.id, parentId, FormItemType.CHOICE, position, content = item.content)
                val set = item.responseSet
                sets += ResponseSetEntity(set.id, item.id, set.multipleSelection)
                set.responses.forEachIndexed { index, r ->
                    responses += ResponseEntity(r.id, set.id, r.label, r.score, index)
                }
            }
        }
    }

    forEachIndexed { index, page -> flatten(page, null, index) }
    return FormEntities(items, sets, responses)
}

/** Rebuilds the domain tree from relational rows. */
fun FormEntities.toPages(): List<Page> {
    val childrenByParent = items.groupBy { it.parentId }.mapValues { (_, v) -> v.sortedBy { it.position } }
    val setByQuestion = responseSets.associateBy { it.questionId }
    val responsesBySet = responses.groupBy { it.responseSetId }.mapValues { (_, v) -> v.sortedBy { it.position } }

    fun build(entity: FormItemEntity): FormItem? {
        val children by lazy { childrenByParent[entity.id].orEmpty().mapNotNull(::build) }
        return when (entity.type) {
            FormItemType.PAGE -> Page(entity.id, entity.title.orEmpty(), children)
            FormItemType.SECTION -> Section(entity.id, entity.title.orEmpty(), children)
            FormItemType.TEXT -> TextQuestion(entity.id, entity.content.orEmpty())
            FormItemType.IMAGE -> entity.toImageQuestion()
            FormItemType.CHOICE -> setByQuestion[entity.id]?.let { set ->
                ChoiceQuestion(
                    id = entity.id,
                    content = entity.content.orEmpty(),
                    responseSet = ResponseSet(
                        id = set.id,
                        multipleSelection = set.multipleSelection,
                        responses = responsesBySet[set.id].orEmpty().map { Response(it.id, it.label, it.score) },
                    ),
                )
            }
            else -> null
        }
    }

    return childrenByParent[null].orEmpty().mapNotNull { build(it) as? Page }
}

fun FormItemEntity.toImageQuestion(): ImageQuestion? =
    src?.let { ImageQuestion(id = id, title = title.orEmpty(), src = it) }
