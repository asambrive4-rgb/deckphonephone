package com.example.deckphonephone.deck.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.deckphonephone.deck.application.DeckColorTheme
import com.example.deckphonephone.deck.application.PairedBluetoothDevice
import com.example.deckphonephone.ui.theme.previewColor

@Composable
internal fun AppSettingsDialog(
    isDarkTheme: Boolean,
    colorTheme: DeckColorTheme,
    isRightHanded: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onColorThemeChanged: (DeckColorTheme) -> Unit,
    onRightHandedChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text("앱 설정") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AppColorThemeSelector(
                    selectedTheme = colorTheme,
                    onThemeChanged = onColorThemeChanged,
                )
                AppSettingsSwitchRow(
                    title = "화면 모드",
                    description = if (isDarkTheme) "다크 모드" else "라이트 모드",
                    checked = isDarkTheme,
                    onCheckedChange = onDarkThemeChanged,
                )
                AppSettingsSwitchRow(
                    title = "어느 손?",
                    description = if (isRightHanded) "오른손 잡이용" else "왼손잡이용",
                    checked = isRightHanded,
                    onCheckedChange = onRightHandedChanged,
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
private fun AppColorThemeSelector(
    selectedTheme: DeckColorTheme,
    onThemeChanged: (DeckColorTheme) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "앱 색상",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DeckColorTheme.entries.forEach { theme ->
                AppColorThemeOption(
                    colorTheme = theme,
                    selected = selectedTheme == theme,
                    onClick = { onThemeChanged(theme) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AppColorThemeOption(
    colorTheme: DeckColorTheme,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        ),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = colorTheme.previewColor(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Box(modifier = Modifier.size(12.dp))
            }
            Text(
                text = colorTheme.label(),
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

@Composable
private fun AppSettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
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
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

private fun DeckColorTheme.label(): String {
    return when (this) {
        DeckColorTheme.Sky -> "하늘"
        DeckColorTheme.Apricot -> "살구"
        DeckColorTheme.Mint -> "민트"
        DeckColorTheme.Lavender -> "라벤더"
    }
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
internal fun ActionCardEditDialog(
    editState: ActionCardEditState,
    bluetoothDevices: List<PairedBluetoothDevice>,
    isBluetoothDevicesLoading: Boolean,
    onTitleChanged: (String) -> Unit,
    onPayloadChanged: (String) -> Unit,
    onActionCardTypeChanged: (ActionCardType) -> Unit,
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
        title = { Text("액션 카드 수정") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = editState.title,
                    onValueChange = onTitleChanged,
                    label = { Text("슬롯 이름") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                ActionCardTypeChips(
                    selectedActionCardType = editState.selectedActionCardType,
                    onActionCardTypeChanged = onActionCardTypeChanged,
                )
                if (editState.selectedActionCardType == ActionCardType.Bluetooth) {
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
                        label = { Text(editState.selectedActionCardType.payloadLabel()) },
                        minLines = if (editState.selectedActionCardType == ActionCardType.Text) 3 else 1,
                        keyboardOptions = if (editState.selectedActionCardType == ActionCardType.Web) {
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
        is DeleteTarget.ActionCard -> target.actionCard.title
    }
    val targetLabel = when (target) {
        is DeleteTarget.Category -> "카테고리"
        is DeleteTarget.ActionCard -> "액션 카드"
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
internal fun ActionCardTypeChips(
    selectedActionCardType: ActionCardType,
    onActionCardTypeChanged: (ActionCardType) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionCardType.entries.forEach { type ->
            FilterChip(
                selected = selectedActionCardType == type,
                onClick = { onActionCardTypeChanged(type) },
                label = { Text(type.label()) },
            )
        }
    }
}

internal fun ActionCardType.label(): String {
    return when (this) {
        ActionCardType.Text -> "문구"
        ActionCardType.Web -> "웹"
        ActionCardType.Bluetooth -> "블루투스"
    }
}

private fun ActionCardType.payloadLabel(): String {
    return when (this) {
        ActionCardType.Text -> "복사할 문구"
        ActionCardType.Web -> "열 웹페이지 주소"
        ActionCardType.Bluetooth -> "블루투스 기기"
    }
}
