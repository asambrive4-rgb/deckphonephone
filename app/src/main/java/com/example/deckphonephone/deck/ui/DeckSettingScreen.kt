package com.example.deckphonephone.deck.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.deckphonephone.deck.application.ConnectedBluetoothDevice
import com.example.deckphonephone.deck.application.OverlayHandPreference
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.DeckCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckSettingScreen(
    uiState: DeckSettingUiState,
    snackbarHostState: SnackbarHostState,
    onCategoryNameChanged: (String) -> Unit,
    onCreateCategoryRequested: () -> Unit,
    onDismissCreateCategory: () -> Unit,
    onCreateCategory: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onEditCategory: (DeckCategory) -> Unit,
    onDeleteCategory: (DeckCategory) -> Unit,
    onBack: () -> Unit,
    onCardTitleChanged: (String) -> Unit,
    onCardPayloadChanged: (String) -> Unit,
    onCardTypeChanged: (CardType) -> Unit,
    onBluetoothDeviceSelected: (PairedBluetoothDevice) -> Unit,
    onCreateCardRequested: () -> Unit,
    onDismissCreateCard: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onDismissAppSettings: () -> Unit,
    onDarkThemeChanged: (Boolean) -> Unit,
    onOverlayRightHandedChanged: (Boolean) -> Unit,
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
    onEditingBluetoothDeviceSelected: (PairedBluetoothDevice) -> Unit,
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
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        text = selectedCategory?.name ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    if (selectedCategory != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로",
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenAppSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "앱 설정",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = if (selectedCategory == null) onCreateCategoryRequested else onCreateCardRequested,
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                text = { Text(if (selectedCategory == null) "카테고리 추가" else "카드 추가") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
    ) { innerPadding ->
        if (selectedCategory == null) {
            HomeScreen(
                categories = uiState.categories,
                onCreateCategoryRequested = onCreateCategoryRequested,
                onCategorySelected = onCategorySelected,
                onEditCategory = onEditCategory,
                onDeleteCategory = onDeleteCategory,
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            CategoryDetailScreen(
                cards = uiState.cards,
                connectedBluetoothDevices = uiState.connectedBluetoothDevices,
                onCreateCardRequested = onCreateCardRequested,
                onCardClicked = onCardClicked,
                onEditCard = onEditCard,
                onToggleCardEnabled = onToggleCardEnabled,
                onDeleteCard = onDeleteCard,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (uiState.isAppSettingsOpen) {
        AppSettingsDialog(
            isDarkTheme = uiState.isDarkTheme,
            isRightHanded = uiState.overlayHandPreference == OverlayHandPreference.Right,
            onDarkThemeChanged = onDarkThemeChanged,
            onRightHandedChanged = onOverlayRightHandedChanged,
            onDismiss = onDismissAppSettings,
        )
    }

    if (uiState.isCreatingCategory) {
        CreateCategorySheet(
            name = uiState.categoryNameInput,
            onNameChanged = onCategoryNameChanged,
            onSave = onCreateCategory,
            onDismiss = onDismissCreateCategory,
        )
    }

    if (uiState.isCreatingCard) {
        CreateCardSheet(
            title = uiState.cardTitleInput,
            payload = uiState.cardPayloadInput,
            selectedCardType = uiState.selectedCardType,
            bluetoothDevices = uiState.pairedBluetoothDevices,
            selectedBluetoothDevice = uiState.selectedBluetoothDevice,
            isBluetoothDevicesLoading = uiState.isBluetoothDevicesLoading,
            onTitleChanged = onCardTitleChanged,
            onPayloadChanged = onCardPayloadChanged,
            onCardTypeChanged = onCardTypeChanged,
            onBluetoothDeviceSelected = onBluetoothDeviceSelected,
            onSave = onCreateCard,
            onDismiss = onDismissCreateCard,
        )
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
            bluetoothDevices = uiState.pairedBluetoothDevices,
            isBluetoothDevicesLoading = uiState.isBluetoothDevicesLoading,
            onTitleChanged = onEditingCardTitleChanged,
            onPayloadChanged = onEditingCardPayloadChanged,
            onCardTypeChanged = onEditingCardTypeChanged,
            onBluetoothDeviceSelected = onEditingBluetoothDeviceSelected,
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
    onCreateCategoryRequested: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onEditCategory: (DeckCategory) -> Unit,
    onDeleteCategory: (DeckCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        if (categories.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DeckEmptyCard(
                    text = "+ 카테고리 추가",
                    onClick = onCreateCategoryRequested,
                )
            }
        }
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

@Composable
private fun CategoryDetailScreen(
    cards: List<ActionCard>,
    connectedBluetoothDevices: List<ConnectedBluetoothDevice>,
    onCreateCardRequested: () -> Unit,
    onCardClicked: (ActionCard) -> Unit,
    onEditCard: (ActionCard) -> Unit,
    onToggleCardEnabled: (ActionCard) -> Unit,
    onDeleteCard: (ActionCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        if (cards.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DeckEmptyCard(
                    text = "+ 카드 추가",
                    onClick = onCreateCardRequested,
                )
            }
        }
        items(cards, key = { it.id }) { card ->
            ActionCardView(
                card = card,
                connectedBluetoothDevices = connectedBluetoothDevices,
                onClick = onCardClicked,
                onEdit = { onEditCard(card) },
                onToggleEnabled = { onToggleCardEnabled(card) },
                onDelete = { onDeleteCard(card) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCategorySheet(
    name: String,
    onNameChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "카테고리 추가",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                value = name,
                onValueChange = onNameChanged,
                label = { Text("카테고리 이름") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text("카테고리 추가")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCardSheet(
    title: String,
    payload: String,
    selectedCardType: CardType,
    bluetoothDevices: List<PairedBluetoothDevice>,
    selectedBluetoothDevice: PairedBluetoothDevice?,
    isBluetoothDevicesLoading: Boolean,
    onTitleChanged: (String) -> Unit,
    onPayloadChanged: (String) -> Unit,
    onCardTypeChanged: (CardType) -> Unit,
    onBluetoothDeviceSelected: (PairedBluetoothDevice) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "카드 추가",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                label = { Text("슬롯 이름") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            CardTypeChips(
                selectedCardType = selectedCardType,
                onCardTypeChanged = onCardTypeChanged,
            )
            if (selectedCardType == CardType.Bluetooth) {
                BluetoothDeviceSelector(
                    devices = bluetoothDevices,
                    selectedDevice = selectedBluetoothDevice,
                    isLoading = isBluetoothDevicesLoading,
                    onDeviceSelected = onBluetoothDeviceSelected,
                )
            } else {
                OutlinedTextField(
                    value = payload,
                    onValueChange = onPayloadChanged,
                    label = { Text(selectedCardType.payloadLabel()) },
                    minLines = if (selectedCardType == CardType.Text) 3 else 1,
                    keyboardOptions = if (selectedCardType == CardType.Web) {
                        KeyboardOptions(keyboardType = KeyboardType.Uri)
                    } else {
                        KeyboardOptions.Default
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text("카드 추가")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun CardType.payloadLabel(): String {
    return when (this) {
        CardType.Text -> "복사할 문구"
        CardType.Web -> "열 웹페이지 주소"
        CardType.Bluetooth -> "블루투스 기기"
    }
}
