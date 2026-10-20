package chingiz.simulationform.domain.usecase

import chingiz.simulationform.domain.repository.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectivityUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor,
) {
    operator fun invoke(): Flow<Boolean> = networkMonitor.isOnline
}
