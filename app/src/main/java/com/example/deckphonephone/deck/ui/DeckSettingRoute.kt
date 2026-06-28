package com.example.deckphonephone.deck.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun DeckSettingRoute(
    viewModel: DeckSettingViewModel,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadPairedBluetoothDevices()
        } else {
            viewModel.bluetoothPermissionDenied()
        }
    }

    fun requestBluetoothPermissionOrLoadDevices() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            viewModel.loadPairedBluetoothDevices()
            return
        }

        val permission = Manifest.permission.BLUETOOTH_CONNECT
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadPairedBluetoothDevices()
        } else {
            bluetoothPermissionLauncher.launch(permission)
        }
    }

    LaunchedEffect(
        uiState.isCreatingCard,
        uiState.selectedCardType,
        uiState.editingCard?.cardId,
        uiState.editingCard?.selectedCardType,
    ) {
        val createNeedsBluetooth = uiState.isCreatingCard && uiState.selectedCardType == CardType.Bluetooth
        val editNeedsBluetooth = uiState.editingCard?.selectedCardType == CardType.Bluetooth
        if (createNeedsBluetooth || editNeedsBluetooth) {
            requestBluetoothPermissionOrLoadDevices()
        }
    }

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

    if (uiState.isCategoriesLoading) return

    DeckSettingScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCategoryNameChanged = viewModel::onCategoryNameChanged,
        onCreateCategoryRequested = viewModel::requestCreateCategory,
        onDismissCreateCategory = viewModel::dismissCreateCategory,
        onCreateCategory = viewModel::createCategory,
        onCategorySelected = viewModel::selectCategory,
        onEditCategory = viewModel::requestEditCategory,
        onDeleteCategory = viewModel::requestDeleteCategory,
        onBack = viewModel::leaveCategory,
        onCardTitleChanged = viewModel::onCardTitleChanged,
        onCardPayloadChanged = viewModel::onCardPayloadChanged,
        onCardTypeChanged = viewModel::onCardTypeChanged,
        onBluetoothDeviceSelected = viewModel::onBluetoothDeviceSelected,
        onCreateCardRequested = viewModel::requestCreateCard,
        onDismissCreateCard = viewModel::dismissCreateCard,
        onOpenAppSettings = viewModel::requestAppSettings,
        onDismissAppSettings = viewModel::dismissAppSettings,
        onDarkThemeChanged = viewModel::setDarkTheme,
        onOverlayRightHandedChanged = viewModel::setOverlayRightHanded,
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
        onEditingBluetoothDeviceSelected = viewModel::onEditingBluetoothDeviceSelected,
        onEditingCardEnabledChanged = viewModel::onEditingCardEnabledChanged,
        onSaveCardEdit = viewModel::saveCardEdit,
        onDismissCardEdit = viewModel::dismissCardEdit,
        onConfirmDelete = viewModel::confirmDelete,
        onDismissDelete = viewModel::dismissDeleteConfirmation,
        modifier = modifier,
    )
}
