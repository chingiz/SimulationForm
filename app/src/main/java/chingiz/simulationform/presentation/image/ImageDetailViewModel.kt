package chingiz.simulationform.presentation.image

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import chingiz.simulationform.domain.usecase.GetImageQuestionUseCase
import chingiz.simulationform.presentation.mvi.MviViewModel
import chingiz.simulationform.presentation.navigation.ImageDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getImageQuestion: GetImageQuestionUseCase,
) : MviViewModel<ImageDetailState, ImageDetailIntent, ImageDetailEffect>(ImageDetailState()) {

    private val imageId: Long = checkNotNull(savedStateHandle[ImageDetailDestination.ARG_IMAGE_ID])

    init {
        viewModelScope.launch {
            val image = getImageQuestion(imageId)
            setState { copy(isLoading = false, image = image) }
        }
    }

    override fun onIntent(intent: ImageDetailIntent) {
        when (intent) {
            ImageDetailIntent.Back -> sendEffect(ImageDetailEffect.NavigateBack)
        }
    }
}
