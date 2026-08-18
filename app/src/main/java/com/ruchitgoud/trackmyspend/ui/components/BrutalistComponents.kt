package com.ruchitgoud.trackmyspend.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitgoud.trackmyspend.ui.theme.BrutalistBlack
import com.ruchitgoud.trackmyspend.ui.theme.BrutalistWhite

@Composable
fun BrutalistShadowBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shadowColor: Color = MaterialTheme.colorScheme.outline,
    borderWidth: Dp = 3.dp,
    cornerRadius: Dp = 24.dp,
    shadowOffset: Dp = 8.dp,
    isPressed: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed) shadowOffset else 0.dp,
        label = "sinkAnimation"
    )

    Box(
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = shadowColor,
                    topLeft = Offset(shadowOffset.toPx(), shadowOffset.toPx()),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
                )
            }
            .offset { IntOffset(animatedOffset.roundToPx(), animatedOffset.roundToPx()) }
            .border(borderWidth, shadowColor, RoundedCornerShape(cornerRadius))
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .padding(borderWidth),
        content = content
    )
}

@Composable
fun BrutalistButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 999.dp,
    fontWeight: FontWeight = FontWeight.Black,
    textColor: Color = BrutalistBlack
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    BrutalistShadowBox(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        ),
        backgroundColor = backgroundColor,
        shadowOffset = shadowOffset,
        cornerRadius = cornerRadius,
        isPressed = isPressed
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = fontWeight,
                fontSize = 18.sp,
                color = textColor
            )
        }
    }
}

@Composable
fun BrutalistCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    BrutalistShadowBox(
        modifier = modifier.then(clickableModifier),
        backgroundColor = backgroundColor,
        shadowOffset = shadowOffset,
        cornerRadius = cornerRadius,
        isPressed = isPressed,
        content = content
    )
}

@Composable
fun BrutalistIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shadowOffset: Dp = 2.dp,
    cornerRadius: Dp = 10.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    BrutalistShadowBox(
        modifier = modifier
            .size(40.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        backgroundColor = backgroundColor,
        shadowOffset = shadowOffset,
        cornerRadius = cornerRadius,
        borderWidth = 2.dp,
        isPressed = isPressed
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Composable
fun BrutalistTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    BrutalistShadowBox(
        modifier = modifier,
        backgroundColor = if (MaterialTheme.colorScheme.background == BrutalistWhite) BrutalistWhite else MaterialTheme.colorScheme.surface,
        shadowOffset = 0.dp,
        cornerRadius = 16.dp,
        borderWidth = 3.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = if (MaterialTheme.colorScheme.background == BrutalistWhite) BrutalistBlack.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (MaterialTheme.colorScheme.background == BrutalistWhite) BrutalistBlack else MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = keyboardOptions,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
