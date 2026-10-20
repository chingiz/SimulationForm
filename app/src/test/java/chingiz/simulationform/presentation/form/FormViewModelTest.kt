package chingiz.simulationform.presentation.form

import app.cash.turbine.test
import chingiz.simulationform.domain.model.DataError
import chingiz.simulationform.domain.model.DataResult
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.repository.FormRepository
import chingiz.simulationform.domain.repository.NetworkMonitor
import chingiz.simulationform.domain.usecase.ObserveConnectivityUseCase
import chingiz.simulationform.domain.usecase.ObservePagesUseCase
import chingiz.simulationform.domain.usecase.ObserveSelectionsUseCase
import chingiz.simulationform.domain.usecase.RefreshFormUseCase
import chingiz.simulationform.domain.usecase.ToggleResponseUseCase
import chingiz.simulationform.testutil.MainDispatcherRule
import chingiz.simulationform.testutil.TestData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeRepository : FormRepository {
        val pages = MutableStateFlow<List<Page>>(emptyList())
        val selections = MutableStateFlow<Map<Long, Set<Long>>>(emptyMap())
        val refreshResults = ArrayDeque<DataResult<Unit>>()
        var refreshCount = 0

        override fun observePages() = pages
        override suspend fun refresh(): DataResult<Unit> {
            refreshCount++
            return refreshResults.removeFirstOrNull() ?: DataResult.Success(Unit)
        }
        override suspend fun getImageQuestion(id: Long): ImageQuestion? = null
        override fun observeSelections() = selections
        override suspend fun saveSelection(questionId: Long, responseIds: Set<Long>) {
            selections.value = selections.value + (questionId to responseIds)
        }
    }

    private class FakeNetworkMonitor : NetworkMonitor {
        override val isOnline = MutableStateFlow(true)
    }

    private val repository = FakeRepository()
    private val networkMonitor = FakeNetworkMonitor()

    private fun viewModel() = FormViewModel(
        observePages = ObservePagesUseCase(repository),
        observeSelections = ObserveSelectionsUseCase(repository),
        observeConnectivity = ObserveConnectivityUseCase(networkMonitor),
        refreshForm = RefreshFormUseCase(repository),
        toggleResponse = ToggleResponseUseCase(repository),
    )

    @Test
    fun `initial failure without cache shows full screen error`() = runTest {
        repository.refreshResults += DataResult.Error(DataError.NoInternet)
        val vm = viewModel()
        assertTrue(vm.state.value.isInitialLoading)

        advanceUntilIdle()

        with(vm.state.value) {
            assertTrue(showFullScreenError)
            assertEquals(DataError.NoInternet, error)
            assertFalse(isRefreshing)
        }
    }

    @Test
    fun `failure with cached data shows stale content instead of full screen error`() = runTest {
        repository.pages.value = TestData.pages
        repository.refreshResults += DataResult.Error(DataError.Timeout)
        val vm = viewModel()

        advanceUntilIdle()

        with(vm.state.value) {
            assertEquals(TestData.pages, pages)
            assertFalse(showFullScreenError)
            assertTrue(isShowingStaleData)
        }
    }

    @Test
    fun `retry intent clears the error on success`() = runTest {
        repository.refreshResults += DataResult.Error(DataError.NoInternet)
        val vm = viewModel()
        advanceUntilIdle()

        vm.onIntent(FormIntent.Refresh)
        advanceUntilIdle()

        assertNull(vm.state.value.error)
        assertEquals(2, repository.refreshCount)
    }

    @Test
    fun `reconnecting automatically retries a failed refresh`() = runTest {
        networkMonitor.isOnline.value = false
        repository.refreshResults += DataResult.Error(DataError.NoInternet)
        val vm = viewModel()
        advanceUntilIdle()
        assertFalse(vm.state.value.isOnline)

        networkMonitor.isOnline.value = true
        advanceUntilIdle()

        assertTrue(vm.state.value.isOnline)
        assertNull(vm.state.value.error)
        assertEquals(2, repository.refreshCount)
    }

    @Test
    fun `toggling responses respects multiple selection`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        vm.onIntent(FormIntent.ToggleResponse(TestData.singleChoice, 1))
        vm.onIntent(FormIntent.ToggleResponse(TestData.singleChoice, 2))
        vm.onIntent(FormIntent.ToggleResponse(TestData.multiChoice, 3))
        vm.onIntent(FormIntent.ToggleResponse(TestData.multiChoice, 4))
        advanceUntilIdle()

        assertEquals(setOf(2L), vm.state.value.selections[TestData.singleChoice.id])
        assertEquals(setOf(3L, 4L), vm.state.value.selections[TestData.multiChoice.id])
    }

    @Test
    fun `open image emits navigation effect`() = runTest {
        val vm = viewModel()

        vm.effects.test {
            vm.onIntent(FormIntent.OpenImage(TestData.image.id))
            assertEquals(FormEffect.NavigateToImage(TestData.image.id), awaitItem())
        }
    }
}
