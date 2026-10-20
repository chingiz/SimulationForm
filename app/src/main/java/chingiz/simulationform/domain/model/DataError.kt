package chingiz.simulationform.domain.model

sealed interface DataError {
    data object NoInternet : DataError
    data object Timeout : DataError
    data class Server(val code: Int) : DataError
    data object Serialization : DataError
    data object Unknown : DataError
}
