package com.example.deckphonephone.deck.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory

@Composable
fun DeckSettingScreen(
    viewModel: DeckSettingViewModel,
    onExit: () -> Unit = {},
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

    BackHandler {
        if (uiState.selectedCategoryId == null) {
            onExit()
        } else {
            viewModel.leaveCategory()
        }
    }

    DeckSettingScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCategoryNameChanged = viewModel::onCategoryNameChanged,
        onCreateCategory = viewModel::createCategory,
        onCategorySelected = viewModel::selectCategory,
        onEditCategory = viewModel::requestEditCategory,
        onDeleteCategory = viewModel::requestDeleteCategory,
        onBack = viewModel::leaveCategory,
        onCardTitleChanged = viewModel::onCardTitleChanged,
        onCardPayloadChanged = viewModel::onCardPayloadChanged,
        onCardTypeChanged = viewModel::onCardTypeChanged,
        onCreateCard = viewModel::createCard,
        onCardClicked = viewModel::executeCard,
        onEditCard = viewModel::requestEditCard,
        onToggleCardEnabled = viewModel::toggleCardEnabled,
        onDeleteCard = viewModel::requestDeleteCard,
        onEditingCategoryNameChanged = viewModel::onEditingCategoryNameChanged,
        onSaveCategoryEdit = viewModel::saveCategoryEdit,
        onDismissCategoryEdit = viewModel::dismissCategoryEdit,
        onEditingCardTitleChanged = viewModel::onEditingCardTitleChanged,
        onEditingCardPayloadChanged = viewModel::onEditingCardPayloadChanged,
        onEditingCardTypeChanged = viewModel::onEditingCardTypeChanged,
        onEditingCardEnabledChanged = viewModel::onEditingCardEnabledChanged,
        onSaveCardEdit = viewModel::saveCardEdit,
        onDismissCardEdit = viewModel::dismissCardEdit,
        onConfirmDelete = viewModel::confirmDelete,
        onDismissDelete = viewModel::dismissDeleteConfirmation,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckSettingScreenContent(
    uiState: DeckSettingUiState,
    snackbarHostState: SnackbarHostState,
    onCategoryNameChanged: (String) -> Unit,
    onCreateCategory: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onEditCategory: (DeckCategory) -> Unit,
    onDeleteCategory: (DeckCategory) -> Unit,
    onBack: () -> Unit,
    onCardTitleChanged: (String) -> Unit,
    onCardPayloadChanged: (String) -> Unit,
    onCardTypeChanged: (CardType) -> Unit,
    onCreateCard: () -> Unit,
    onCardClicked: (ActionCard) -> Unit,
    onEditCard: (ActionCard) -> Unit,
    onToggleCardEnabled: (ActionCard) -> Unit,
    onDeleteCard: (ActionCard) -> Unit,
    onEditingCategoryNameChanged: (String) -> Unit,
    onSaveCategoryEdit: () -> Unit,
    onDismissCategoryEdit: () -> Unit,
    onEditingCardTitleChanged: (String) -> Unit,
    onEditingCardPayloadChanged: (String) -> Unit,
    onEditingCardTypeChanged: (CardType) -> Unit,
    onEditingCardEnabledChanged: (Boolean) -> Unit,
    onSaveCardEdit: () -> Unit,
    onDismissCardEdit: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedCategory = uiState.categories.firstOrNull { it.id == uiState.selectedCategoryId }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedCategory?.name ?: "DeckDeckDeck",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    if (selectedCategory != null) {
                        TextButton(onClick = onBack) {
                            Text("뒤로")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        if (selectedCategory == null) {
            HomeScreen(
                categories = uiState.categories,
                categoryNameInput = uiState.categoryNameInput,
                onCategoryNameChanged = onCategoryNameChanged,
                onCreateCategory = onCreateCategory,
                onCategorySelected = onCategorySelected,
                onEditCategory = onEditCategory,
                onDeleteCategory = onDeleteCategory,
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            CategoryDetailScreen(
                category = selectedCategory,
                cards = uiState.cards,
                cardTitleInput = uiState.cardTitleInput,
                cardPayloadInput = uiState.cardPayloadInput,
                selectedCardType = uiState.selectedCardType,
                onCardTitleChanged = onCardTitleChanged,
                onCardPayloadChanged = onCardPayloadChanged,
                onCardTypeChanged = onCardTypeChanged,
                onCreateCard = onCreateCard,
                onCardClicked = onCardClicked,
                onEditCard = onEditCard,
                onToggleCardEnabled = onToggleCardEnabled,
                onDeleteCard = onDeleteCard,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    uiState.editingCategory?.let { editState ->
        CategoryEditDialog(
            editState = editState,
            onNameChanged = onEditingCategoryNameChanged,
            onSave = onSaveCategoryEdit,
            onDismiss = onDismissCategoryEdit,
        )
    }

    uiState.editingCard?.let { editState ->
        CardEditDialog(
            editState = editState,
            onTitleChanged = onEditingCardTitleChanged,
            onPayloadChanged = onEditingCardPayloadChanged,
            onCardTypeChanged = onEditingCardTypeChanged,
            onEnabledChanged = onEditingCardEnabledChanged,
            onSave = onSaveCardEdit,
            onDismiss = onDismissCardEdit,
        )
    }

    uiState.deleteTarget?.let { target ->
        DeleteConfirmationDialog(
            target = target,
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete,
        )
    }
}

@Composable
private fun HomeScreen(
    categories: List<DeckCategory>,
    categoryNameInput: String,
    onCategoryNameChanged: (String) -> Unit,
    onCreateCategory: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onEditCategory: (DeckCategory) -> Unit,
    onDeleteCategory: (DeckCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = categoryNameInput,
                onValueChange = onCategoryNameChanged,
                label = { Text("카테고리 이름") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = onCreateCategory,
                modifier = Modifier.height(56.dp),
            ) {
                Text("추가")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 132.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategorySelected(category.id) },
                    onEdit = { onEditCategory(category) },
                    onDelete = { onDeleteCategory(category) },
                )
            }
        }
    }
}

@Composable
private fun CategoryDetailScreen(
    category: DeckCategory,
    cards: List<ActionCard>,
    cardTitleInput: String,
    cardPayloadInput: String,
    selectedCardType: CardType,
    onCardTitleChanged: (String) -> Unit,
    onCardPayloadChanged: (String) -> Unit,
    onCardTypeChanged: (CardType) -> Unit,
    onCreateCard: () -> Unit,
    onCardClicked: (ActionCard) -> Unit,
    onEditCard: (ActionCard) -> Unit,
    onToggleCardEnabled: (ActionCard) -> Unit,
    onDeleteCard: (ActionCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(12.dp))
        CardForm(
            title = cardTitleInput,
            payload = cardPayloadInput,
            selectedCardType = selectedCardType,
            onTitleChanged = onCardTitleChanged,
            onPayloadChanged = onCardPayloadChanged,
            onCardTypeChanged = onCardTypeChanged,
            onCreateCard = onCreateCard,
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 132.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(cards, key = { it.id }) { card ->
                ActionCardView(
                    card = card,
                    onClick = onCardClicked,
                    onEdit = { onEditCard(card) },
                    onToggleEnabled = { onToggleCardEnabled(card) },
                    onDelete = { onDeleteCard(card) },
                )
            }
        }
    }
}

@Composable
private fun CardForm(
    title: String,
    payload: String,
    selectedCardType: CardType,
    onTitleChanged: (String) -> Unit,
    onPayloadChanged: (String) -> Unit,
    onCardTypeChanged: (CardType) -> Unit,
    onCreateCard: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChanged,
            label = { Text("슬롯 이름") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedCardType == CardType.Text,
                onClick = { onCardTypeChanged(CardType.Text) },
                label = { Text("문구") },
            )
            FilterChip(
                selected = selectedCardType == CardType.Web,
                onClick = { onCardTypeChanged(CardType.Web) },
                label = { Text("웹") },
            )
        }
        OutlinedTextField(
            value = payload,
            onValueChange = onPayloadChanged,
            label = {
                Text(
                    if (selectedCardType == CardType.Text) {
                        "복사할 문구"
                    } else {
                        "열 웹페이지 주소"
                    },
                )
            },
            minLines = if (selectedCardType == CardType.Text) 3 else 1,
            keyboardOptions = if (selectedCardType == CardType.Web) {
                KeyboardOptions(keyboardType = KeyboardType.Uri)
            } else {
                KeyboardOptions.Default
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = onCreateCard,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("카드 추가")
        }
    }
}