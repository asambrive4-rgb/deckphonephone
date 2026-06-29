package com.example.deckphonephone.deck.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.deckphonephone.deck.application.ConnectedBluetoothDevice
import com.example.deckphonephone.deck.domain.ActionCard
import com.example.deckphonephone.deck.domain.ActionCardOperation

@Composable
internal fun DeckCardSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color? = null,
    breathingGlow: Boolean = false,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        label = "deck-card-press-scale",
    )
    val resolvedContainerColor = containerColor ?: DeckUiColors.categoryCardContainer
    val glowProgress = if (breathingGlow && enabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "deck-card-breathing-glow")
        val animatedProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1200,
                    easing = FastOutSlowInEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "deck-card-breathing-glow-progress",
        )
        animatedProgress
    } else {
        0f
    }
    val isGlowVisible = breathingGlow && enabled
    val glowColor = DeckUiColors.cardGlow
    val cardBorder = if (isGlowVisible) {
        BorderStroke(
            width = 1.dp,
            color = glowColor.copy(alpha = 0.16f + 0.20f * glowProgress),
        )
    } else {
        BorderStroke(1.dp, DeckUiColors.cardBorder)
    }

    Box(
        modifier = modifier
            .heightIn(min = 72.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                if (isGlowVisible) {
                    drawBreathingGlow(
                        color = glowColor,
                        progress = glowProgress,
                        cornerRadiusPx = 8.dp.toPx(),
                    )
                }
            }
            .alpha(if (enabled) 1f else 0.5f),
    ) {
        Card(
            onClick = onClick,
            enabled = true,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = resolvedContainerColor,
                disabledContainerColor = resolvedContainerColor,
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 1.dp,
                disabledElevation = 0.dp,
            ),
            border = cardBorder,
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        if (isGlowVisible) {
                            drawInnerBreathingGlow(
                                color = glowColor,
                                progress = glowProgress,
                                cornerRadiusPx = 8.dp.toPx(),
                            )
                        }
                    },
            ) {
                content()
            }
        }
    }
}

private fun DrawScope.drawBreathingGlow(
    color: Color,
    progress: Float,
    cornerRadiusPx: Float,
) {
    val glowLayers = listOf(
        GlowLayer(spread = 14.dp, alpha = 0.030f),
        GlowLayer(spread = 11.dp, alpha = 0.045f),
        GlowLayer(spread = 8.dp, alpha = 0.060f),
        GlowLayer(spread = 5.dp, alpha = 0.075f),
        GlowLayer(spread = 2.dp, alpha = 0.090f),
    )
    val breathAlpha = 0.55f + 0.45f * progress
    val breathSpread = 2.dp.toPx() * progress

    glowLayers.forEach { layer ->
        val spread = layer.spread.toPx() + breathSpread
        drawRoundRect(
            color = color.copy(alpha = layer.alpha * breathAlpha),
            topLeft = Offset(-spread, -spread),
            size = Size(
                width = size.width + spread * 2f,
                height = size.height + spread * 2f,
            ),
            cornerRadius = CornerRadius(
                x = cornerRadiusPx + spread,
                y = cornerRadiusPx + spread,
            ),
        )
    }

    val softRimWidth = (1.5f + progress).dp.toPx()
    drawRoundRect(
        color = color.copy(alpha = 0.12f + 0.08f * progress),
        topLeft = Offset(-softRimWidth / 2f, -softRimWidth / 2f),
        size = Size(
            width = size.width + softRimWidth,
            height = size.height + softRimWidth,
        ),
        cornerRadius = CornerRadius(cornerRadiusPx + softRimWidth, cornerRadiusPx + softRimWidth),
        style = Stroke(width = softRimWidth),
    )
}

private fun DrawScope.drawInnerBreathingGlow(
    color: Color,
    progress: Float,
    cornerRadiusPx: Float,
) {
    val innerLayers = listOf(
        InnerGlowLayer(inset = 0.dp, width = 2.5.dp, alpha = 0.055f),
        InnerGlowLayer(inset = 3.dp, width = 2.dp, alpha = 0.035f),
        InnerGlowLayer(inset = 6.dp, width = 1.5.dp, alpha = 0.020f),
    )
    val breathAlpha = 0.55f + 0.45f * progress
    val breathWidth = 1.dp.toPx() * progress

    innerLayers.forEach { layer ->
        val inset = layer.inset.toPx()
        val strokeWidth = layer.width.toPx() + breathWidth
        val rectOffset = inset + strokeWidth / 2f
        drawRoundRect(
            color = color.copy(alpha = layer.alpha * breathAlpha),
            topLeft = Offset(rectOffset, rectOffset),
            size = Size(
                width = size.width - rectOffset * 2f,
                height = size.height - rectOffset * 2f,
            ),
            cornerRadius = CornerRadius(
                x = (cornerRadiusPx - inset).coerceAtLeast(0f),
                y = (cornerRadiusPx - inset).coerceAtLeast(0f),
            ),
            style = Stroke(width = strokeWidth),
        )
    }
}

private data class GlowLayer(
    val spread: Dp,
    val alpha: Float,
)

private data class InnerGlowLayer(
    val inset: Dp,
    val width: Dp,
    val alpha: Float,
)

@Composable
internal fun DeckCardTextContent(
    title: String,
    label: String,
    modifier: Modifier = Modifier,
    labelColor: androidx.compose.ui.graphics.Color? = null,
    badgeText: String? = null,
    titleMaxLines: Int = 2,
    labelMaxLines: Int = 1,
    contentPadding: Dp = 14.dp,
) {
    val resolvedLabelColor = labelColor ?: DeckUiColors.actionLabel
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = DeckUiColors.cardTitle,
            maxLines = titleMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
        DeckCardLabelRow(
            label = label,
            labelColor = resolvedLabelColor,
            badgeText = badgeText,
            labelMaxLines = labelMaxLines,
        )
    }
}

@Composable
private fun DeckCardLabelRow(
    label: String,
    labelColor: Color,
    badgeText: String?,
    labelMaxLines: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
            maxLines = labelMaxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (badgeText != null) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = DeckUiColors.connectedBadgeContainer,
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = DeckUiColors.connectedBadgeContent,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
                        .widthIn(min = 0.dp, max = 42.dp)
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                )
            }
        }
    }
}

@Composable
internal fun DeckEmptyCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = DeckUiColors.emptyCardContainer,
        border = BorderStroke(1.dp, DeckUiColors.cardBorder),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp),
    ) {
        Box(
            modifier = Modifier.padding(18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = DeckUiColors.emptyCardText,
            )
        }
    }
}

internal fun ActionCardOperation.deckLabel(): String {
    return when (this) {
        is ActionCardOperation.CopyText -> "문구"
        is ActionCardOperation.OpenUrl -> "웹사이트"
        is ActionCardOperation.BluetoothDevice -> "블루투스"
    }
}

internal fun ActionCard.hasConnectedBluetoothDevice(
    connectedBluetoothDevices: List<ConnectedBluetoothDevice>,
): Boolean {
    val bluetoothDevice = operation as? ActionCardOperation.BluetoothDevice ?: return false
    if (!isEnabled) return false

    return connectedBluetoothDevices.any { connectedDevice ->
        connectedDevice.address.equals(bluetoothDevice.deviceAddress, ignoreCase = true) ||
            connectedDevice.hasMatchingName(bluetoothDevice.deviceName)
    }
}

private fun ConnectedBluetoothDevice.hasMatchingName(savedDeviceName: String): Boolean {
    val connectedName = name.toBluetoothNameKey()
    val savedName = savedDeviceName.toBluetoothNameKey()
    if (connectedName.length < MinimumBluetoothNameMatchLength ||
        savedName.length < MinimumBluetoothNameMatchLength
    ) {
        return false
    }

    return connectedName == savedName ||
        connectedName.contains(savedName) ||
        savedName.contains(connectedName)
}

private fun String.toBluetoothNameKey(): String {
    return lowercase()
        .replace("galaxy", "")
        .replace("samsung", "")
        .replace("le-", "")
        .replace("(l)", "")
        .replace("(r)", "")
        .replace("left", "")
        .replace("right", "")
        .replace("왼쪽", "")
        .replace("오른쪽", "")
        .filter { it.isLetterOrDigit() }
}

private const val MinimumBluetoothNameMatchLength = 6
