package chingiz.simulationform.domain.usecase

import chingiz.simulationform.domain.repository.FormRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectionsUseCase @Inject constructor(
    private val repository: FormRepository,
) {
    operator fun invoke(): Flow<Map<Long, Set<Long>>> = repository.observeSelections()
}
