package com.example.deckphonephone.deck.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.deckphonephone.deck.domain.ActionCard
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

    if (uiState.isCategoriesLoading) return

    DeckOverlayScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCategorySelected = viewModel::selectCategory,
        onBack = viewModel::goBack,
        onClose = viewModel::close,
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
    onClose: () -> Unit,
    onCardClicked: (ActionCard) -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedCategory = uiState.categories.firstOrNull { it.id == uiState.selectedCategoryId }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
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
                        onClose = onClose,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (selectedCategory == null) {
                        CategoryGrid(
                            categories = uiState.categories,
                            onCategorySelected = onCategorySelected,
                            onEmptyClicked = onSettingsClicked,
                        )
                    } else {
                        ActionCardGrid(
                            cards = uiState.cards,
                            onCardClicked = onCardClicked,
                            onEmptyClicked = onSettingsClicked,
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
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
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
        IconButton(onClick = onSettingsClicked, modifier = Modifier.size(48.dp)) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "설정")
        }
        IconButton(onClick = onClose, modifier = Modifier.size(48.dp)) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "닫기")
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<DeckCategory>,
    onCategorySelected: (Long) -> Unit,
    onEmptyClicked: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 88.dp),
        contentPadding = PaddingValues(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.heightIn(max = 474.dp),
    ) {
        if (categories.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DeckEmptyCard(
                    text = "+ 카테고리 추가",
                    onClick = onEmptyClicked,
                )
            }
        }
        items(categories, key = { it.id }) { category ->
            OverlayCategoryCard(
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
    onEmptyClicked: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 88.dp),
        contentPadding = PaddingValues(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.heightIn(max = 474.dp),
    ) {
        if (cards.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DeckEmptyCard(
                    text = "+ 카드 추가",
                    onClick = onEmptyClicked,
                )
            }
        }
        items(cards, key = { it.id }) { card ->
            OverlayActionCard(
                card = card,
                onClick = onCardClicked,
            )
        }
    }
}

@Composable
private fun OverlayCategoryCard(
    category: DeckCategory,
    onClick: () -> Unit,
) {
    DeckCardSurface(
        onClick = onClick,
        modifier = Modifier.height(92.dp),
    ) {
        DeckCardTextContent(
            title = category.name,
            label = "카테고리",
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            labelMaxLines = 2,
            contentPadding = 10.dp,
        )
    }
}

@Composable
private fun OverlayActionCard(
    card: ActionCard,
    onClick: (ActionCard) -> Unit,
) {
    val actionLabel = if (card.isEnabled) {
        card.action.deckLabel()
    } else {
        "비활성 · ${card.action.deckLabel()}"
    }

    DeckCardSurface(
        onClick = { onClick(card) },
        modifier = Modifier.height(92.dp),
        enabled = card.isEnabled,
    ) {
        DeckCardTextContent(
            title = card.title,
            label = actionLabel,
            labelColor = if (card.isEnabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            labelMaxLines = 2,
            contentPadding = 10.dp,
        )
    }
}
