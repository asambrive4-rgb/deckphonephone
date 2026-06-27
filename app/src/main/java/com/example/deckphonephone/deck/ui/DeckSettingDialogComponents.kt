package com.example.deckphonephone.deck.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.deckphonephone.deck.application.PairedBluetoothDevice

@Composable
internal fun AppSettingsDialog(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text("앱 설정") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "화면 모드",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = if (isDarkTheme) "다크 모드" else "라이트 모드",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onDarkThemeChanged,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        },
    )
}

@Composable
internal fun CategoryEditDialog(
    editState: CategoryEditState,
    onNameChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text("카테고리 수정") },
        text = {
            OutlinedTextField(
                value = editState.name,
                onValueChange = onNameChanged,
                label = { Text("카테고리 이름") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
    )
}

@Composable
internal fun CardEditDialog(
    editState: CardEditState,
    bluetoothDevices: List<PairedBluetoothDevice>,
    isBluetoothDevicesLoading: Boolean,
    onTitleChanged: (String) -> Unit,
    onPayloadChanged: (String) -> Unit,
    onCardTypeChanged: (CardType) -> Unit,
    onBluetoothDeviceSelected: (PairedBluetoothDevice) -> Unit,
    onEnabledChanged: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text("카드 수정") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = editState.title,
                    onValueChange = onTitleChanged,
                    label = { Text("슬롯 이름") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                CardTypeChips(
                    selectedCardType = editState.selectedCardType,
                    onCardTypeChanged = onCardTypeChanged,
                )
                if (editState.selectedCardType == CardType.Bluetooth) {
                    BluetoothDeviceSelector(
                        devices = bluetoothDevices,
                        selectedDevice = editState.selectedBluetoothDevice,
                        isLoading = isBluetoothDevicesLoading,
                        onDeviceSelected = onBluetoothDeviceSelected,
                    )
                } else {
                    OutlinedTextField(
                        value = editState.payload,
                        onValueChange = onPayloadChanged,
                        label = { Text(editState.selectedCardType.payloadLabel()) },
                        minLines = if (editState.selectedCardType == CardType.Text) 3 else 1,
                        keyboardOptions = if (editState.selectedCardType == CardType.Web) {
                            KeyboardOptions(keyboardType = KeyboardType.Uri)
                        } else {
                            KeyboardOptions.Default
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "활성",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Switch(
                        checked = editState.isEnabled,
                        onCheckedChange = onEnabledChanged,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
    )
}

@Composable
internal fun DeleteConfirmationDialog(
    target: DeleteTarget,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val targetName = when (target) {
        is DeleteTarget.Category -> target.category.name
        is DeleteTarget.Card -> target.card.title
    }
    val targetLabel = when (target) {
        is DeleteTarget.Category -> "카테고리"
        is DeleteTarget.Card -> "카드"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text("$targetLabel 삭제") },
        text = {
            Text(
                text = "${targetName}을 삭제할까요?",
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text("삭제")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
    )
}

@Composable
internal fun CardTypeChips(
    selectedCardType: CardType,
    onCardTypeChanged: (CardType) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CardType.entries.forEach { type ->
            FilterChip(
                selected = selectedCardType == type,
                onClick = { onCardTypeChanged(type) },
                label = { Text(type.label()) },
            )
        }
    }
}

internal fun CardType.label(): String {
    return when (this) {
        CardType.Text -> "문구"
        CardType.Web -> "웹"
        CardType.Bluetooth -> "블루투스"
    }
}

private fun CardType.payloadLabel(): String {
    return when (this) {
        CardType.Text -> "복사할 문구"
        CardType.Web -> "열 웹페이지 주소"
        CardType.Bluetooth -> "블루투스 기기"
    }
}