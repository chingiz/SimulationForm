package chingiz.simulationform.presentation.form.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chingiz.simulationform.R
import chingiz.simulationform.domain.model.DataError
import chingiz.simulationform.presentation.common.toMessageRes
import chingiz.simulationform.ui.theme.WarningAmber

@Composable
fun FullScreenError(
    error: DataError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = if (error == DataError.NoInternet) Icons.Outlined.CloudOff else Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(error.toMessageRes()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}

/** Non-blocking banner shown above cached content when the latest data could not be fetched. */
@Composable
fun StaleDataBanner(
    isOnline: Boolean,
    error: DataError?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val message = if (!isOnline || error == null) {
        stringResource(R.string.offline_banner)
    } else {
        stringResource(R.string.stale_banner, stringResource(error.toMessageRes()))
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(WarningAmber.copy(alpha = 0.15f))
            .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(Icons.Outlined.CloudOff, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onRetry) { Text(stringResource(R.string.retry), color = WarningAmber) }
    }
}

@Composable
fun EmptyForm(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.empty_form),
        modifier = modifier.padding(32.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
