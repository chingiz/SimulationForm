package chingiz.simulationform.presentation.form

import androidx.lifecycle.viewModelScope
import chingiz.simulationform.domain.model.DataResult
import chingiz.simulationform.domain.usecase.ObserveConnectivityUseCase
import chingiz.simulationform.domain.usecase.ObservePagesUseCase
import chingiz.simulationform.domain.usecase.ObserveSelectionsUseCase
import chingiz.simulationform.domain.usecase.RefreshFormUseCase
import chingiz.simulationform.domain.usecase.ToggleResponseUseCase
import chingiz.simulationform.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    observePages: ObservePagesUseCase,
    observeSelections: ObserveSelectionsUseCase,
    observeConnectivity: ObserveConnectivityUseCase,
    private val refreshForm: RefreshFormUseCase,
    private val toggleResponse: ToggleResponseUseCase,
) : MviViewModel<FormState, FormIntent, FormEffect>(FormState()) {

    private var refreshJob: Job? = null

    init {
        observePages()
            .onEach { pages -> setState { copy(pages = pages) } }
            .launchIn(viewModelScope)
        observeSelections()
            .onEach { selections -> setState { copy(selections = selections) } }
            .launchIn(viewModelScope)
        observeConnectivity()
            .onEach(::onConnectivityChanged)
            .launchIn(viewModelScope)
        refresh()
    }

    /** Automatically retries a failed refresh as soon as connectivity is restored. */
    private fun onConnectivityChanged(isOnline: Boolean) {
        val wasOffline = !currentState.isOnline
        setState { copy(isOnline = isOnline) }
        if (isOnline && wasOffline && currentState.error != null) refresh()
    }

    override fun onIntent(intent: FormIntent) {
        when (intent) {
            FormIntent.Refresh -> refresh()
            is FormIntent.ToggleResponse -> onToggleResponse(intent)
            is FormIntent.OpenImage -> sendEffect(FormEffect.NavigateToImage(intent.imageId))
        }
    }

    private fun refresh() {
        if (refreshJob?.isActive == true) return
        // Set synchronously so the very first frame already shows the loading state.
        setState { copy(isRefreshing = true) }
        refreshJob = viewModelScope.launch {
            when (val result = refreshForm()) {
                is DataResult.Success -> setState { copy(isRefreshing = false, error = null) }
                is DataResult.Error -> setState { copy(isRefreshing = false, error = result.error) }
            }
        }
    }

    private fun onToggleResponse(intent: FormIntent.ToggleResponse) {
        val questionId = intent.question.id
        val current = currentState.selections[questionId].orEmpty()
        val updated = ToggleResponseUseCase.toggle(
            multipleSelection = intent.question.responseSet.multipleSelection,
            responseId = intent.responseId,
            current = current,
        )
        // Optimistic update for instant feedback; the persisted value flows back from Room.
        setState { copy(selections = selections + (questionId to updated)) }
        viewModelScope.launch { toggleResponse(intent.question, intent.responseId, current) }
    }
}
