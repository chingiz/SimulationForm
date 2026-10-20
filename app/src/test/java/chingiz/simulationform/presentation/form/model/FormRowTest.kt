package chingiz.simulationform.presentation.form.model

import chingiz.simulationform.testutil.TestData
import org.junit.Assert.assertEquals
import org.junit.Test

class FormRowTest {

    @Test
    fun `tree is flattened in order with depth and section level`() {
        val rows = TestData.pages.toRows()

        assertEquals(listOf(1L, 2L, 3L, 5L, 4L, 13L, 14L), rows.map { it.key })
        assertEquals(listOf(0, 1, 2, 2, 3, 3, 1), rows.map { it.depth })

        val sectionLevels = rows.filterIsInstance<FormRow.SectionHeader>().map { it.sectionLevel }
        assertEquals(listOf(0, 1), sectionLevels)
    }
}
