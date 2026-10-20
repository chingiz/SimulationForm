package chingiz.simulationform.domain.usecase

import chingiz.simulationform.domain.repository.FormRepository
import chingiz.simulationform.testutil.TestData
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ToggleResponseUseCaseTest {

    @Test
    fun `single select replaces the current response`() {
        assertEquals(setOf(2L), ToggleResponseUseCase.toggle(false, 2, setOf(1)))
    }

    @Test
    fun `multi select adds to the current responses`() {
        assertEquals(setOf(1L, 2L), ToggleResponseUseCase.toggle(true, 2, setOf(1)))
    }

    @Test
    fun `tapping a selected response deselects it in both modes`() {
        assertEquals(emptySet<Long>(), ToggleResponseUseCase.toggle(false, 1, setOf(1)))
        assertEquals(setOf(2L), ToggleResponseUseCase.toggle(true, 1, setOf(1, 2)))
    }

    @Test
    fun `invoke persists the new selection`() = runTest {
        val repository = mockk<FormRepository>(relaxed = true)
        val result = ToggleResponseUseCase(repository)(TestData.multiChoice, 4, setOf(3))

        assertEquals(setOf(3L, 4L), result)
        coVerify { repository.saveSelection(TestData.multiChoice.id, setOf(3L, 4L)) }
    }
}
