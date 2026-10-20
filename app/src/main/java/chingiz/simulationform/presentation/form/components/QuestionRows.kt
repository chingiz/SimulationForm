package chingiz.simulationform.presentation.form.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chingiz.simulationform.R
import chingiz.simulationform.domain.model.ChoiceQuestion
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Response
import chingiz.simulationform.domain.model.TextQuestion
import chingiz.simulationform.ui.theme.HierarchyTypography
import coil3.compose.SubcomposeAsyncImage

private val ThumbnailSize = 120.dp

@Composable
private fun QuestionText(text: String, id: Long, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = text,
            style = HierarchyTypography.questionStyle(),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f, fill = false),
        )
        IdBadge(id)
    }
}

@Composable
fun TextQuestionRow(question: TextQuestion, modifier: Modifier = Modifier) {
    QuestionText(question.content, question.id, modifier.padding(vertical = 6.dp))
}

@Composable
fun ImageQuestionRow(
    question: ImageQuestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 6.dp)) {
        val shape = RoundedCornerShape(8.dp)
        SubcomposeAsyncImage(
            model = question.src,
            contentDescription = stringResource(R.string.image_content_description, question.title),
            contentScale = ContentScale.Fit,
            loading = {
                CenteredBox { CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp) }
            },
            error = {
                CenteredBox {
                    Icon(Icons.Outlined.BrokenImage, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            modifier = Modifier
                .size(ThumbnailSize)
                .clip(shape)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                .clickable(role = Role.Image, onClick = onClick),
        )
        Row(
            modifier = Modifier.padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = question.title,
                fontFamily = FontFamily.Monospace,
                fontSize = HierarchyTypography.question,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            IdBadge(question.id)
        }
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChoiceQuestionRow(
    question: ChoiceQuestion,
    selected: Set<Long>,
    onResponseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 6.dp)) {
        QuestionText(question.content, question.id)
        FlowRow(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            question.responseSet.responses.forEach { response ->
                ResponseChip(
                    response = response,
                    isSelected = response.id in selected,
                    onClick = { onResponseClick(response.id) },
                )
            }
        }
        SelectionSummary(selectedCount = selected.size, multiple = question.responseSet.multipleSelection)
    }
}

@Composable
private fun ResponseChip(response: Response, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        shape = RoundedCornerShape(50),
        label = {
            Text(response.label, fontSize = 13.sp)
            response.score?.let { score ->
                Box(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(score.toString(), fontSize = 10.sp)
                }
            }
        },
        leadingIcon = if (isSelected) {
            { Icon(Icons.Rounded.Check, null, Modifier.size(FilterChipDefaults.IconSize)) }
        } else {
            null
        },
    )
}

@Composable
private fun SelectionSummary(selectedCount: Int, multiple: Boolean) {
    val count = if (selectedCount == 0) {
        stringResource(R.string.selection_none)
    } else {
        stringResource(R.string.selection_count, selectedCount)
    }
    val mode = stringResource(if (multiple) R.string.selection_multi else R.string.selection_single)
    Text(
        text = stringResource(R.string.selection_summary, count, mode),
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
