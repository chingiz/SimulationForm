package chingiz.simulationform.data.mapper

import chingiz.simulationform.testutil.TestData
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalMapperTest {

    @Test
    fun `entities round trip back to the same tree`() {
        assertEquals(TestData.pages, TestData.pages.toEntities().toPages())
    }

    @Test
    fun `tree is restored regardless of row order`() {
        val entities = TestData.pages.toEntities()
        val shuffled = entities.copy(
            items = entities.items.reversed(),
            responses = entities.responses.reversed(),
        )
        assertEquals(TestData.pages, shuffled.toPages())
    }

    @Test
    fun `parents reference their children and choices produce response rows`() {
        val entities = TestData.pages.toEntities()
        assertEquals(null, entities.items.first { it.id == 1L }.parentId)
        assertEquals(5L, entities.items.first { it.id == 13L }.parentId)
        assertEquals(2, entities.responseSets.size)
        assertEquals(4, entities.responses.size)
    }
}
