package chingiz.simulationform.presentation.image

import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.presentation.mvi.UiEffect
import chingiz.simulationform.presentation.mvi.UiIntent
import chingiz.simulationform.presentation.mvi.UiState

data class ImageDetailState(
    val isLoading: Boolean = true,
    val image: ImageQuestion? = null,
) : UiState

sealed interface ImageDetailIntent : UiIntent {
    data object Back : ImageDetailIntent
}

sealed interface ImageDetailEffect : UiEffect {
    data object NavigateBack : ImageDetailEffect
}
