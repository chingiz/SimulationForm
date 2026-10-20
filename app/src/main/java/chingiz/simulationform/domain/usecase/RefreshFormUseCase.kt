package chingiz.simulationform.domain.usecase

import chingiz.simulationform.domain.model.DataResult
import chingiz.simulationform.domain.repository.FormRepository
import javax.inject.Inject

class RefreshFormUseCase @Inject constructor(
    private val repository: FormRepository,
) {
    suspend operator fun invoke(): DataResult<Unit> = repository.refresh()
}
