package chingiz.simulationform.data.remote

import chingiz.simulationform.domain.model.DataError
import chingiz.simulationform.domain.model.DataResult
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SafeApiCallTest {

    private fun http(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    @Test
    fun `exceptions are mapped to typed errors`() {
        assertEquals(DataError.Timeout, SocketTimeoutException().toDataError())
        assertEquals(DataError.NoInternet, UnknownHostException().toDataError())
        assertEquals(DataError.NoInternet, IOException().toDataError())
        assertEquals(DataError.Server(503), http(503).toDataError())
        assertEquals(DataError.Serialization, SerializationException("bad").toDataError())
        assertEquals(DataError.Unknown, IllegalStateException().toDataError())
    }

    @Test
    fun `only transient errors are retryable`() {
        assertTrue(DataError.NoInternet.isRetryable())
        assertTrue(DataError.Timeout.isRetryable())
        assertTrue(DataError.Server(500).isRetryable())
        assertTrue(DataError.Server(429).isRetryable())
        assertFalse(DataError.Server(404).isRetryable())
        assertFalse(DataError.Serialization.isRetryable())
    }

    @Test
    fun `safeApiCall wraps success and failure`() = runTest {
        assertEquals(DataResult.Success(1), safeApiCall { 1 })
        assertEquals(DataResult.Error(DataError.NoInternet), safeApiCall { throw IOException() })
    }

    @Test
    fun `retry backs off and succeeds after transient failures`() = runTest {
        var calls = 0
        val result = retryWithBackoff {
            calls++
            if (calls < 3) DataResult.Error(DataError.Timeout) else DataResult.Success("ok")
        }

        assertEquals(DataResult.Success("ok"), result)
        assertEquals(3, calls)
        assertEquals(3_000L, currentTime)
    }

    @Test
    fun `retry gives up after max attempts`() = runTest {
        var calls = 0
        val result = retryWithBackoff { calls++; DataResult.Error(DataError.NoInternet) }

        assertEquals(DataResult.Error(DataError.NoInternet), result)
        assertEquals(3, calls)
    }

    @Test
    fun `non retryable errors fail immediately`() = runTest {
        var calls = 0
        retryWithBackoff { calls++; DataResult.Error(DataError.Server(404)) }
        assertEquals(1, calls)
    }
}
