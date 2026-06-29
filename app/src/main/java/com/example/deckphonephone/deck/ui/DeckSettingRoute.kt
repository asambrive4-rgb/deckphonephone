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
        uiState.isCreatingActionCard,
        uiState.selectedActionCardType,
        uiState.editingActionCard?.actionCardId,
        uiState.editingActionCard?.selectedActionCardType,
    ) {
        val createNeedsBluetooth = uiState.isCreatingActionCard && uiState.selectedActionCardType == ActionCardType.Bluetooth
        val editNeedsBluetooth = uiState.editingActionCard?.selectedActionCardType == ActionCardType.Bluetooth
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
        onActionCardTitleChanged = viewModel::onActionCardTitleChanged,
        onActionCardPayloadChanged = viewModel::onActionCardPayloadChanged,
        onActionCardTypeChanged = viewModel::onActionCardTypeChanged,
        onBluetoothDeviceSelected = viewModel::onBluetoothDeviceSelected,
        onCreateActionCardRequested = viewModel::requestCreateActionCard,
        onDismissCreateActionCard = viewModel::dismissCreateActionCard,
        onOpenAppSettings = viewModel::requestAppSettings,
        onDismissAppSettings = viewModel::dismissAppSettings,
        onDarkThemeChanged = viewModel::setDarkTheme,
        onColorThemeChanged = viewModel::setColorTheme,
        onOverlayRightHandedChanged = viewModel::setOverlayRightHanded,
        onCreateActionCard = viewModel::createActionCard,
        onActionCardClicked = viewModel::executeActionCard,
        onEditActionCard = viewModel::requestEditActionCard,
        onToggleActionCardEnabled = viewModel::toggleActionCardEnabled,
        onDeleteActionCard = viewModel::requestDeleteActionCard,
        onEditingCategoryNameChanged = viewModel::onEditingCategoryNameChanged,
        onSaveCategoryEdit = viewModel::saveCategoryEdit,
        onDismissCategoryEdit = viewModel::dismissCategoryEdit,
        onEditingActionCardTitleChanged = viewModel::onEditingActionCardTitleChanged,
        onEditingActionCardPayloadChanged = viewModel::onEditingActionCardPayloadChanged,
        onEditingActionCardTypeChanged = viewModel::onEditingActionCardTypeChanged,
        onEditingBluetoothDeviceSelected = viewModel::onEditingBluetoothDeviceSelected,
        onEditingActionCardEnabledChanged = viewModel::onEditingActionCardEnabledChanged,
        onSaveActionCardEdit = viewModel::saveActionCardEdit,
        onDismissActionCardEdit = viewModel::dismissActionCardEdit,
        onConfirmDelete = viewModel::confirmDelete,
        onDismissDelete = viewModel::dismissDeleteConfirmation,
        modifier = modifier,
    )
}
