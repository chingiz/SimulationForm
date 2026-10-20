package chingiz.simulationform.testutil

import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.model.Response
import chingiz.simulationform.domain.model.ResponseSet
import chingiz.simulationform.domain.model.Section
import chingiz.simulationform.domain.model.TextQuestion

object TestData {
    val singleChoice = ChoiceQuestion(
        id = 13,
        content = "Single",
        responseSet = ResponseSet(
            id = 100,
            multipleSelection = false,
            responses = listOf(Response(1, "Yes", 1), Response(2, "No", null)),
        ),
    )

    val multiChoice = ChoiceQuestion(
        id = 14,
        content = "Multi",
        responseSet = ResponseSet(
            id = 101,
            multipleSelection = true,
            responses = listOf(Response(3, "A", 2), Response(4, "B", 0)),
        ),
    )

    val image = ImageQuestion(id = 4, title = "Image", src = "https://example.com/a.png")

    /** Page > Section > (Text, Section > (Image, Choice)), Choice */
    val pages = listOf(
        Page(
            id = 1,
            title = "Page 1",
            items = listOf(
                Section(
                    id = 2,
                    title = "Section 1",
                    items = listOf(
                        TextQuestion(3, "Text"),
                        Section(5, "Nested", listOf(image, singleChoice)),
                    ),
                ),
                multiChoice,
            ),
        ),
    )
}
