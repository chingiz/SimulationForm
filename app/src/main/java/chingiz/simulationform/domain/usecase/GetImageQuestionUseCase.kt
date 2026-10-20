package chingiz.simulationform.domain.usecase

import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.repository.FormRepository
import javax.inject.Inject

class GetImageQuestionUseCase @Inject constructor(
    private val repository: FormRepository,
) {
    suspend operator fun invoke(id: Long): ImageQuestion? = repository.getImageQuestion(id)
}
