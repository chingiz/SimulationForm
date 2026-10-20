package chingiz.simulationform.domain.usecase

import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.repository.FormRepository
import javax.inject.Inject

/**
 * Applies a tap on a response while respecting the question's `multiple_selection` flag:
 * - multi-select: the tapped response is toggled independently of the others;
 * - single-select: the tapped response replaces the current one, tapping it again clears it.
 */
class ToggleResponseUseCase @Inject constructor(
    private val repository: FormRepository,
) {
    suspend operator fun invoke(
        question: ChoiceQuestion,
        responseId: Long,
        currentSelection: Set<Long>,
    ): Set<Long> {
        val newSelection = toggle(question.responseSet.multipleSelection, responseId, currentSelection)
        repository.saveSelection(question.id, newSelection)
        return newSelection
    }

    companion object {
        fun toggle(multipleSelection: Boolean, responseId: Long, current: Set<Long>): Set<Long> =
            when {
                responseId in current -> current - responseId
                multipleSelection -> current + responseId
                else -> setOf(responseId)
            }
    }
}
