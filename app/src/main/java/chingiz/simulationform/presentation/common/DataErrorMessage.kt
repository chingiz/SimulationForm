package chingiz.simulationform.presentation.common

import androidx.annotation.StringRes
import chingiz.simulationform.R
import chingiz.simulationform.domain.model.DataError

@StringRes
fun DataError.toMessageRes(): Int = when (this) {
    DataError.NoInternet -> R.string.error_no_internet
    DataError.Timeout -> R.string.error_timeout
    is DataError.Server -> R.string.error_server
    DataError.Serialization -> R.string.error_serialization
    DataError.Unknown -> R.string.error_unknown
}
