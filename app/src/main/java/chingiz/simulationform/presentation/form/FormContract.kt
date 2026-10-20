package chingiz.simulationform.presentation.form

import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.model.DataError
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.presentation.mvi.UiEffect
import chingiz.simulationform.presentation.mvi.UiIntent
import chingiz.simulationform.presentation.mvi.UiState

data class FormState(
    val pages: List<Page> = emptyList(),
    val selections: Map<Long, Set<Long>> = emptyMap(),
    val isRefreshing: Boolean = false,
    val error: DataError? = null,
) : UiState {
    val isInitialLoading: Boolean get() = pages.isEmpty() && isRefreshing
    val showFullScreenError: Boolean get() = pages.isEmpty() && !isRefreshing && error != null

    /** Cached content is shown but the latest refresh failed. */
    val isShowingStaleData: Boolean get() = pages.isNotEmpty() && error != null
}

sealed interface FormIntent : UiIntent {
    data object Refresh : FormIntent
    data class ToggleResponse(val question: ChoiceQuestion, val responseId: Long) : FormIntent
    data class OpenImage(val imageId: Long) : FormIntent
}

sealed interface FormEffect : UiEffect {
    data class NavigateToImage(val imageId: Long) : FormEffect
}
