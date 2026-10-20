package chingiz.simulationform.presentation.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chingiz.simulationform.R
import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.model.Response
import chingiz.simulationform.domain.model.ResponseSet
import chingiz.simulationform.domain.model.Section
import chingiz.simulationform.domain.model.TextQuestion
import chingiz.simulationform.presentation.form.components.ChoiceQuestionRow
import chingiz.simulationform.presentation.form.components.HierarchyRow
import chingiz.simulationform.presentation.form.components.ImageQuestionRow
import chingiz.simulationform.presentation.form.components.PageHeaderRow
import chingiz.simulationform.presentation.form.components.SectionHeaderRow
import chingiz.simulationform.presentation.form.components.TextQuestionRow
import chingiz.simulationform.presentation.form.model.FormRow
import chingiz.simulationform.presentation.form.model.toRows
import chingiz.simulationform.ui.theme.SimulationFormTheme

@Composable
fun FormRoute(
    onOpenImage: (Long) -> Unit,
    viewModel: FormViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnOpenImage by rememberUpdatedState(onOpenImage)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FormEffect.NavigateToImage -> currentOnOpenImage(effect.imageId)
            }
        }
    }

    FormScreen(state = state, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    state: FormState,
    onIntent: (FormIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.form_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.isInitialLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                FormContent(state, onIntent)
            }
        }
    }
}

@Composable
private fun FormContent(state: FormState, onIntent: (FormIntent) -> Unit) {
    val rows = remember(state.pages) { state.pages.toRows() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        items(rows, key = { it.key }, contentType = { it::class }) { row ->
            HierarchyRow(depth = row.depth) {
                when (row) {
                    is FormRow.PageHeader -> PageHeaderRow(row.page)
                    is FormRow.SectionHeader -> SectionHeaderRow(row.section, row.sectionLevel)
                    is FormRow.Text -> TextQuestionRow(row.question)
                    is FormRow.Image -> ImageQuestionRow(
                        question = row.question,
                        onClick = { onIntent(FormIntent.OpenImage(row.question.id)) },
                    )
                    is FormRow.Choice -> ChoiceQuestionRow(
                        question = row.question,
                        selected = state.selections[row.question.id].orEmpty(),
                        onResponseClick = { onIntent(FormIntent.ToggleResponse(row.question, it)) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FormScreenPreview() {
    val pages = listOf(
        Page(
            id = 1,
            title = "Main Page",
            items = listOf(
                Section(
                    id = 2,
                    title = "Chapter 1",
                    items = listOf(
                        TextQuestion(3, "This is the first chapter."),
                        Section(4, "Subsection 1.1", listOf(ImageQuestion(5, "Chapter 1 Image", ""))),
                        ChoiceQuestion(
                            id = 6,
                            content = "Which areas were inspected?",
                            responseSet = ResponseSet(
                                id = 7,
                                multipleSelection = true,
                                responses = listOf(Response(8, "Entrance", 1), Response(9, "Storage", null)),
                            ),
                        ),
                    ),
                ),
            ),
        ),
    )
    SimulationFormTheme {
        FormScreen(state = FormState(pages = pages, selections = mapOf(6L to setOf(8L))), onIntent = {})
    }
}
