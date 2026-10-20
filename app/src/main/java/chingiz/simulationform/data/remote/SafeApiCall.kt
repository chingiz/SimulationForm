package chingiz.simulationform.data.remote

import chingiz.simulationform.domain.model.DataError
import chingiz.simulationform.domain.model.DataResult
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

/** Executes [block] and converts thrown exceptions into a typed [DataError]. */
suspend fun <T> safeApiCall(block: suspend () -> T): DataResult<T> =
    try {
        DataResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        DataResult.Error(e.toDataError())
    }

fun Throwable.toDataError(): DataError = when (this) {
    is SocketTimeoutException -> DataError.Timeout
    is UnknownHostException -> DataError.NoInternet
    is IOException -> DataError.NoInternet
    is HttpException -> DataError.Server(code())
    is SerializationException, is IllegalArgumentException -> DataError.Serialization
    else -> DataError.Unknown
}

/** Transient failures that may succeed on a later attempt (poor connectivity, 5xx, 408, 429). */
fun DataError.isRetryable(): Boolean = when (this) {
    DataError.NoInternet, DataError.Timeout -> true
    is DataError.Server -> code >= 500 || code == 408 || code == 429
    DataError.Serialization, DataError.Unknown -> false
}

/**
 * Retries [block] with exponential backoff while it fails with a retryable error.
 * Provides a fallback mechanism for flaky / slow connections.
 */
suspend fun <T> retryWithBackoff(
    maxAttempts: Int = 3,
    initialDelayMillis: Long = 1_000,
    factor: Double = 2.0,
    block: suspend () -> DataResult<T>,
): DataResult<T> {
    var delayMillis = initialDelayMillis
    var result = block()
    repeat(maxAttempts - 1) {
        if (result !is DataResult.Error || !(result as DataResult.Error).error.isRetryable()) {
            return result
        }
        delay(delayMillis)
        delayMillis = (delayMillis * factor).toLong()
        result = block()
    }
    return result
}
