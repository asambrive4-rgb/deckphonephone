package com.example.deckphonephone.deck.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.CardAction
import com.example.deckphonephone.deck.domain.DeckCategory

@Composable
fun DeckOverlayScreen(
    viewModel: DeckOverlayViewModel,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.messageShown()
        }
    }

    DeckOverlayScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCategorySelected = viewModel::selectCategory,
        onBack = viewModel::goBack,
        onCardClicked = viewModel::executeCard,
        onSettingsClicked = onSettingsClicked,
        modifier = modifier,
    )
}

@Composable
private fun DeckOverlayScreenContent(
    uiState: DeckOverlayUiState,
    snackbarHostState: SnackbarHostState,
    onCategorySelected: (Long) -> Unit,
    onBack: () -> Unit,
    onCardClicked: (ActionCard) -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedCategory = uiState.categories.firstOrNull { it.id == uiState.selectedCategoryId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
                .heightIn(min = 220.dp, max = 560.dp),
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                ) {
                    OverlayHeader(
                        title = selectedCategory?.name ?: "DeckDeckDeck",
                        canGoBack = selectedCategory != null,
                        onBack = onBack,
                        onSettingsClicked = onSettingsClicked,
                        onClose = onBack,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (selectedCategory == null) {
                        CategoryGrid(
                            categories = uiState.categories,
                            onCategorySelected = onCategorySelected,
                        )
                    } else {
                        ActionCardGrid(
                            cards = uiState.cards,
                            onCardClicked = onCardClicked,
                        )
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                )
            }
        }
    }
}

@Composable
private fun OverlayHeader(
    title: String,
    canGoBack: Boolean,
    onBack: () -> Unit,
    onSettingsClicked: () -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (canGoBack) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onSettingsClicked, modifier = Modifier.size(40.dp)) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "설정")
        }
        IconButton(onClick = onClose, modifier = Modifier.size(40.dp)) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "닫기")
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<DeckCategory>,
    onCategorySelected: (Long) -> Unit,
) {
    if (categories.isEmpty()) {
        EmptyMessage(text = "카테고리가 없습니다")
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
        contentPadding = PaddingValues(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.heightIn(max = 474.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategorySelected(category.id) },
            )
        }
    }
}

@Composable
private fun ActionCardGrid(
    cards: List<ActionCard>,
    onCardClicked: (ActionCard) -> Unit,
) {
    if (cards.isEmpty()) {
        EmptyMessage(text = "카드가 없습니다")
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
        contentPadding = PaddingValues(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.heightIn(max = 474.dp),
    ) {
        items(cards, key = { it.id }) { card ->
            ActionCardView(
                card = card,
                onClick = onCardClicked,
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: DeckCategory,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.height(92.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "카테고리",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ActionCardView(
    card: ActionCard,
    onClick: (ActionCard) -> Unit,
) {
    ElevatedCard(
        onClick = { onClick(card) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .height(102.dp)
            .alpha(if (card.isEnabled) 1f else 0.55f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = card.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = card.action.label(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun EmptyMessage(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun CardAction.label(): String {
    return when (this) {
        is CardAction.CopyText -> "문구"
        is CardAction.OpenUrl -> "웹사이트"
    }
}