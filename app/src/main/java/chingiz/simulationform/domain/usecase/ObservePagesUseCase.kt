package chingiz.simulationform.domain.usecase

import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.repository.FormRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePagesUseCase @Inject constructor(
    private val repository: FormRepository,
) {
    operator fun invoke(): Flow<List<Page>> = repository.observePages()
}
