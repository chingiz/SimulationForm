package chingiz.simulationform.domain.model

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error(val error: DataError) : DataResult<Nothing>
}
