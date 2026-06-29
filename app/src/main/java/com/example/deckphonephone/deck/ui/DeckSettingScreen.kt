package com.example.deckphonephone.deck.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
    onActionCardTitleChanged: (String) -> Unit,
    onActionCardPayloadChanged: (String) -> Unit,
    onActionCardTypeChanged: (ActionCardType) -> Unit,
    onBluetoothDeviceSelected: (PairedBluetoothDevice) -> Unit,
    onCreateActionCardRequested: () -> Unit,
    onDismissCreateActionCard: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onDismissAppSettings: () -> Unit,
    onDarkThemeChanged: (Boolean) -> Unit,
    onOverlayRightHandedChanged: (Boolean) -> Unit,
    onCreateActionCard: () -> Unit,
    onActionCardClicked: (ActionCard) -> Unit,
    onEditActionCard: (ActionCard) -> Unit,
    onToggleActionCardEnabled: (ActionCard) -> Unit,
    onDeleteActionCard: (ActionCard) -> Unit,
    onEditingCategoryNameChanged: (String) -> Unit,
    onSaveCategoryEdit: () -> Unit,
    onDismissCategoryEdit: () -> Unit,
    onEditingActionCardTitleChanged: (String) -> Unit,
    onEditingActionCardPayloadChanged: (String) -> Unit,
    onEditingActionCardTypeChanged: (ActionCardType) -> Unit,
    onEditingBluetoothDeviceSelected: (PairedBluetoothDevice) -> Unit,
    onEditingActionCardEnabledChanged: (Boolean) -> Unit,
    onSaveActionCardEdit: () -> Unit,
    onDismissActionCardEdit: () -> Unit,
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
                onClick = if (selectedCategory == null) onCreateCategoryRequested else onCreateActionCardRequested,
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                text = { Text(if (selectedCategory == null) "카테고리 추가" else "액션 카드 추가") },
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
                actionCards = uiState.actionCards,
                connectedBluetoothDevices = uiState.connectedBluetoothDevices,
                onCreateActionCardRequested = onCreateActionCardRequested,
                onActionCardClicked = onActionCardClicked,
                onEditActionCard = onEditActionCard,
                onToggleActionCardEnabled = onToggleActionCardEnabled,
                onDeleteActionCard = onDeleteActionCard,
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

    if (uiState.isCreatingActionCard) {
        CreateActionCardSheet(
            title = uiState.actionCardTitleInput,
            payload = uiState.actionCardPayloadInput,
            selectedActionCardType = uiState.selectedActionCardType,
            bluetoothDevices = uiState.pairedBluetoothDevices,
            selectedBluetoothDevice = uiState.selectedBluetoothDevice,
            isBluetoothDevicesLoading = uiState.isBluetoothDevicesLoading,
            onTitleChanged = onActionCardTitleChanged,
            onPayloadChanged = onActionCardPayloadChanged,
            onActionCardTypeChanged = onActionCardTypeChanged,
            onBluetoothDeviceSelected = onBluetoothDeviceSelected,
            onSave = onCreateActionCard,
            onDismiss = onDismissCreateActionCard,
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

    uiState.editingActionCard?.let { editState ->
        ActionCardEditDialog(
            editState = editState,
            bluetoothDevices = uiState.pairedBluetoothDevices,
            isBluetoothDevicesLoading = uiState.isBluetoothDevicesLoading,
            onTitleChanged = onEditingActionCardTitleChanged,
            onPayloadChanged = onEditingActionCardPayloadChanged,
            onActionCardTypeChanged = onEditingActionCardTypeChanged,
            onBluetoothDeviceSelected = onEditingBluetoothDeviceSelected,
            onEnabledChanged = onEditingActionCardEnabledChanged,
            onSave = onSaveActionCardEdit,
            onDismiss = onDismissActionCardEdit,
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
    actionCards: List<ActionCard>,
    connectedBluetoothDevices: List<ConnectedBluetoothDevice>,
    onCreateActionCardRequested: () -> Unit,
    onActionCardClicked: (ActionCard) -> Unit,
    onEditActionCard: (ActionCard) -> Unit,
    onToggleActionCardEnabled: (ActionCard) -> Unit,
    onDeleteActionCard: (ActionCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        if (actionCards.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DeckEmptyCard(
                    text = "+ 액션 카드 추가",
                    onClick = onCreateActionCardRequested,
                )
            }
        }
        items(actionCards, key = { it.id }) { card ->
            ActionCardView(
                card = card,
                connectedBluetoothDevices = connectedBluetoothDevices,
                onClick = onActionCardClicked,
                onEdit = { onEditActionCard(card) },
                onToggleEnabled = { onToggleActionCardEnabled(card) },
                onDelete = { onDeleteActionCard(card) },
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
        modifier = Modifier.imePadding(),
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
private fun CreateActionCardSheet(
    title: String,
    payload: String,
    selectedActionCardType: ActionCardType,
    bluetoothDevices: List<PairedBluetoothDevice>,
    selectedBluetoothDevice: PairedBluetoothDevice?,
    isBluetoothDevicesLoading: Boolean,
    onTitleChanged: (String) -> Unit,
    onPayloadChanged: (String) -> Unit,
    onActionCardTypeChanged: (ActionCardType) -> Unit,
    onBluetoothDeviceSelected: (PairedBluetoothDevice) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        modifier = Modifier.imePadding(),
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "액션 카드 추가",
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
            ActionCardTypeChips(
                selectedActionCardType = selectedActionCardType,
                onActionCardTypeChanged = onActionCardTypeChanged,
            )
            if (selectedActionCardType == ActionCardType.Bluetooth) {
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
                    label = { Text(selectedActionCardType.payloadLabel()) },
                    minLines = if (selectedActionCardType == ActionCardType.Text) 3 else 1,
                    keyboardOptions = if (selectedActionCardType == ActionCardType.Web) {
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
                Text("액션 카드 추가")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun ActionCardType.payloadLabel(): String {
    return when (this) {
        ActionCardType.Text -> "복사할 문구"
        ActionCardType.Web -> "열 웹페이지 주소"
        ActionCardType.Bluetooth -> "블루투스 기기"
    }
}
