package chingiz.simulationform.presentation.form.model

import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.model.FormItem
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.model.Section
import chingiz.simulationform.domain.model.TextQuestion

/**
 * Flat, render-ready representation of the form tree so it can be displayed lazily.
 * [depth] is the nesting level used for indentation and hierarchy guides.
 */
sealed interface FormRow {
    val key: Long
    val depth: Int

    data class PageHeader(val page: Page) : FormRow {
        override val key = page.id
        override val depth = 0
    }

    data class SectionHeader(
        val section: Section,
        override val depth: Int,
        val sectionLevel: Int,
    ) : FormRow {
        override val key = section.id
    }

    data class Text(val question: TextQuestion, override val depth: Int) : FormRow {
        override val key = question.id
    }

    data class Image(val question: ImageQuestion, override val depth: Int) : FormRow {
        override val key = question.id
    }

    data class Choice(val question: ChoiceQuestion, override val depth: Int) : FormRow {
        override val key = question.id
    }
}

fun List<Page>.toRows(): List<FormRow> = buildList {
    fun visit(item: FormItem, depth: Int, sectionLevel: Int) {
        when (item) {
            is Page -> {
                add(FormRow.PageHeader(item))
                item.items.forEach { visit(it, depth + 1, 0) }
            }
            is Section -> {
                add(FormRow.SectionHeader(item, depth, sectionLevel))
                item.items.forEach { visit(it, depth + 1, sectionLevel + 1) }
            }
            is TextQuestion -> add(FormRow.Text(item, depth))
            is ImageQuestion -> add(FormRow.Image(item, depth))
            is ChoiceQuestion -> add(FormRow.Choice(item, depth))
        }
    }
    this@toRows.forEach { visit(it, 0, 0) }
}
