package chingiz.simulationform.data.mapper

import chingiz.simulationform.data.remote.dto.FormItemDto
import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Section
import chingiz.simulationform.domain.model.TextQuestion
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    private val payload = """
        [
          {"type":"page","id":1,"title":"Page 1","items":[
            {"type":"section","id":2,"title":"Section","items":[
              {"type":"text","id":3,"content":"Hello"},
              {"type":"image","id":4,"title":"Pic","src":"https://x/y.png"},
              {"type":"signature","id":99},
              {"type":"choice","id":13,"content":"Pick","response_set":{
                "id":7,"multiple_selection":true,"responses":[
                  {"id":1,"label":"A","score":1},
                  {"id":2,"label":"B","score":null},
                  {"id":3,"label":"C"}
                ]}}
            ]}
          ]},
          {"type":"text","id":50,"content":"orphan"}
        ]
    """.trimIndent()

    private val pages = json.decodeFromString<List<FormItemDto>>(payload).toPages()

    @Test
    fun `only pages are top level and unknown types are skipped`() {
        assertEquals(listOf(1L), pages.map { it.id })
        val section = pages.single().items.single() as Section
        assertEquals(listOf(3L, 4L, 13L), section.items.map { it.id })
        assertTrue(section.items[0] is TextQuestion)
        assertTrue(section.items[1] is ImageQuestion)
    }

    @Test
    fun `choice keeps multiple selection flag and tolerates missing scores`() {
        val choice = (pages.single().items.single() as Section).items.last() as ChoiceQuestion
        assertTrue(choice.responseSet.multipleSelection)
        assertEquals(1, choice.responseSet.responses[0].score)
        assertNull(choice.responseSet.responses[1].score)
        assertNull(choice.responseSet.responses[2].score)
    }
}
