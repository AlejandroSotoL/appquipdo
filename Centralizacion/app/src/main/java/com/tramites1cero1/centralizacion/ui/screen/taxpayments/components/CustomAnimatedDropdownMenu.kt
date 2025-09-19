package com.tramites1cero1.centralizacion.ui.screen.taxpayments.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp


@Composable
fun <T> CustomAnimatedDropdownMenu(
    isVisible: Boolean,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    itemToString: (T) -> String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible && items.isNotEmpty(),
        enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )

        ) {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(items) { item ->
                    DropdownMenuItem(
                        text = { Text(itemToString(item), style = MaterialTheme.typography.bodySmall) },
                        onClick = { onItemSelected(item) }
                    )
                    if (item != items.last()) {
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
            }
        }
    }
}