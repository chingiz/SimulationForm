package chingiz.simulationform.data.repository

import app.cash.turbine.test
import chingiz.simulationform.data.local.dao.FormDao
import chingiz.simulationform.data.local.entity.SelectionEntity
import chingiz.simulationform.data.mapper.toEntities
import chingiz.simulationform.data.remote.api.FormApi
import chingiz.simulationform.data.remote.dto.FormItemDto
import chingiz.simulationform.data.remote.dto.FormItemType
import chingiz.simulationform.domain.model.DataError
import chingiz.simulationform.domain.model.DataResult
import chingiz.simulationform.testutil.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class FormRepositoryImplTest {

    private val api = mockk<FormApi>()
    private val dao = mockk<FormDao>(relaxed = true)

    private fun TestScope.repository() = FormRepositoryImpl(api, dao, StandardTestDispatcher(testScheduler))

    private val remoteForm = listOf(
        FormItemDto(
            id = 1,
            type = FormItemType.PAGE,
            title = "Page",
            items = listOf(FormItemDto(id = 3, type = FormItemType.TEXT, content = "Text")),
        ),
    )

    @Test
    fun `successful refresh replaces the cached form`() = runTest {
        coEvery { api.getForm() } returns remoteForm

        assertEquals(DataResult.Success(Unit), repository().refresh())
        coVerify(exactly = 1) { dao.replaceForm(any(), any(), any()) }
    }

    @Test
    fun `failed refresh keeps the cache and retries transient errors`() = runTest {
        coEvery { api.getForm() } throws IOException()

        assertEquals(DataResult.Error(DataError.NoInternet), repository().refresh())
        coVerify(exactly = 3) { api.getForm() }
        coVerify(exactly = 0) { dao.replaceForm(any(), any(), any()) }
    }

    @Test
    fun `client errors are not retried`() = runTest {
        coEvery { api.getForm() } throws HttpException(Response.error<Any>(404, "".toResponseBody()))

        assertEquals(DataResult.Error(DataError.Server(404)), repository().refresh())
        coVerify(exactly = 1) { api.getForm() }
    }

    @Test
    fun `pages are rebuilt from the database`() = runTest {
        val entities = TestData.pages.toEntities()
        every { dao.observeItems() } returns flowOf(entities.items)
        every { dao.observeResponseSets() } returns flowOf(entities.responseSets)
        every { dao.observeResponses() } returns flowOf(entities.responses)

        repository().observePages().test {
            assertEquals(TestData.pages, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `selections are grouped by question`() = runTest {
        every { dao.observeSelections() } returns flowOf(
            listOf(
                SelectionEntity(questionId = 14, responseId = 3),
                SelectionEntity(questionId = 14, responseId = 4),
                SelectionEntity(questionId = 13, responseId = 1),
            ),
        )

        repository().observeSelections().test {
            assertEquals(mapOf(14L to setOf(3L, 4L), 13L to setOf(1L)), awaitItem())
            awaitComplete()
        }
    }
}
