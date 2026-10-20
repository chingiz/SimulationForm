package chingiz.simulationform.presentation.form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.model.Section
import chingiz.simulationform.ui.theme.HierarchyTypography

@Composable
fun PageHeaderRow(page: Page, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(top = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = page.title,
            style = HierarchyTypography.pageStyle(),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .weight(1f, fill = false)
                .semantics { heading() },
        )
        IdBadge(page.id)
    }
}

@Composable
fun SectionHeaderRow(section: Section, level: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = section.title,
            style = HierarchyTypography.sectionStyle(level),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .weight(1f, fill = false)
                .semantics { heading() },
        )
        IdBadge(section.id)
    }
}
