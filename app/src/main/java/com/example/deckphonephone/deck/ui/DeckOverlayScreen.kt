package com.example.deckphonephone.deck.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.example.deckphonephone.deck.application.ConnectedBluetoothDevice
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
        onActionCardClicked = viewModel::executeActionCard,
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
    onActionCardClicked: (ActionCard) -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedCategory = uiState.categories.firstOrNull { it.id == uiState.selectedCategoryId }
    val isCategoryOpen = selectedCategory != null
    val overlayShape = RoundedCornerShape(8.dp)
    val overlayBaseColor = if (isCategoryOpen) {
        DeckUiColors.actionScreenBackground
    } else {
        DeckUiColors.categoryScreenBackground
    }
    // 오버레이 패널의 alpha와 외부 테두리는 떠 있는 패널임을 보여주기 위한 의도된 차이입니다.
    val overlayColor = overlayBaseColor.copy(alpha = 0.96f)
    val overlayBorder = if (isCategoryOpen) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f))
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f))
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = overlayShape,
            color = overlayColor,
            tonalElevation = if (isCategoryOpen) 8.dp else 6.dp,
            shadowElevation = if (isCategoryOpen) 14.dp else 10.dp,
            border = overlayBorder,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 220.dp, max = 560.dp),
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                ) {
                    OverlayHeader(
                        title = selectedCategory?.name ?: "",
                        canGoBack = selectedCategory != null,
                        onBack = onBack,
                        onSettingsClicked = onSettingsClicked,
                        onClose = onClose,
                    )
                    if (selectedCategory == null) {
                        CategoryGrid(
                            categories = uiState.categories,
                            onCategorySelected = onCategorySelected,
                            onEmptyClicked = onSettingsClicked,
                        )
                    } else {
                        ActionCardGrid(
                            actionCards = uiState.actionCards,
                            connectedBluetoothDevices = uiState.connectedBluetoothDevices,
                            onActionCardClicked = onActionCardClicked,
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로",
                    tint = DeckUiColors.headerIcon,
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = DeckUiColors.headerTitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onSettingsClicked, modifier = Modifier.size(48.dp)) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "설정",
                tint = DeckUiColors.headerIcon,
            )
        }
        IconButton(onClick = onClose, modifier = Modifier.size(48.dp)) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "닫기",
                tint = DeckUiColors.headerIcon,
            )
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
        contentPadding = PaddingValues(top = 12.dp, bottom = 8.dp),
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
    actionCards: List<ActionCard>,
    connectedBluetoothDevices: List<ConnectedBluetoothDevice>,
    onActionCardClicked: (ActionCard) -> Unit,
    onEmptyClicked: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 88.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.heightIn(max = 474.dp),
    ) {
        if (actionCards.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DeckEmptyCard(
                    text = "+ 액션 카드 추가",
                    onClick = onEmptyClicked,
                )
            }
        }
        items(actionCards, key = { it.id }) { card ->
            OverlayActionCard(
                card = card,
                connectedBluetoothDevices = connectedBluetoothDevices,
                onClick = onActionCardClicked,
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
            labelColor = DeckUiColors.categoryLabel,
            labelMaxLines = 2,
            contentPadding = 10.dp,
        )
    }
}

@Composable
private fun OverlayActionCard(
    card: ActionCard,
    connectedBluetoothDevices: List<ConnectedBluetoothDevice>,
    onClick: (ActionCard) -> Unit,
) {
    val actionLabel = if (card.isEnabled) {
        card.operation.deckLabel()
    } else {
        "비활성 · ${card.operation.deckLabel()}"
    }
    val isConnectedBluetoothCard = card.hasConnectedBluetoothDevice(connectedBluetoothDevices)

    DeckCardSurface(
        onClick = { onClick(card) },
        modifier = Modifier.height(92.dp),
        enabled = card.isEnabled,
        containerColor = DeckUiColors.actionCardContainer,
        breathingGlow = isConnectedBluetoothCard,
    ) {
        DeckCardTextContent(
            title = card.title,
            label = actionLabel,
            badgeText = if (isConnectedBluetoothCard) "연결됨" else null,
            labelColor = if (card.isEnabled) {
                DeckUiColors.actionLabel
            } else {
                DeckUiColors.disabledActionLabel
            },
            labelMaxLines = 2,
            contentPadding = 10.dp,
        )
    }
}
