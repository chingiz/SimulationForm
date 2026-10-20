package chingiz.simulationform.domain.model

/**
 * Hierarchical form tree. Pages are the top-level nodes, sections may nest
 * arbitrarily, and questions are leaves.
 */
sealed interface FormItem {
    val id: Long
}

sealed interface ContainerItem : FormItem {
    val title: String
    val items: List<FormItem>
}

data class Page(
    override val id: Long,
    override val title: String,
    override val items: List<FormItem>,
) : ContainerItem

data class Section(
    override val id: Long,
    override val title: String,
    override val items: List<FormItem>,
) : ContainerItem

sealed interface Question : FormItem

data class TextQuestion(
    override val id: Long,
    val content: String,
) : Question

data class ImageQuestion(
    override val id: Long,
    val title: String,
    val src: String,
) : Question

data class ChoiceQuestion(
    override val id: Long,
    val content: String,
    val responseSet: ResponseSet,
) : Question

data class ResponseSet(
    val id: Long,
    val multipleSelection: Boolean,
    val responses: List<Response>,
)

data class Response(
    val id: Long,
    val label: String,
    val score: Int?,
)
