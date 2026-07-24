package com.ruchitgoud.trackmyspend.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitgoud.trackmyspend.ui.components.BrutalistButton
import com.ruchitgoud.trackmyspend.ui.components.BrutalistShadowBox
import com.ruchitgoud.trackmyspend.ui.theme.BrutalistBlack
import com.ruchitgoud.trackmyspend.ui.theme.BrutalistGray
import com.ruchitgoud.trackmyspend.ui.theme.BrutalistWhite
import com.ruchitgoud.trackmyspend.ui.theme.Mint
import androidx.compose.foundation.interaction.collectIsPressedAsState

import androidx.compose.ui.tooling.preview.Preview
import com.ruchitgoud.trackmyspend.ui.theme.TrackMySpendTheme

@Composable
fun LandingScreen(onGetStarted: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalistWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .scale(scale.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TRACK\nMY\nSPEND",
                fontSize = 64.sp,
                lineHeight = 60.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                letterSpacing = (-2).sp,
                color = BrutalistBlack
            )

            Spacer(modifier = Modifier.height(40.dp))

            BrutalistButton(
                text = "Get Started",
                onClick = onGetStarted,
                backgroundColor = Mint,
                shadowOffset = 6.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "A Lightweight Expense Tracker App to Monitor Spending.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color(0xFF333333),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        val uriHandler = LocalUriHandler.current
        val footerInteractionSource = remember { MutableInteractionSource() }
        val isFooterPressed by footerInteractionSource.collectIsPressedAsState()

        BrutalistShadowBox(
            modifier = Modifier
                .clickable(
                    interactionSource = footerInteractionSource,
                    indication = null,
                    onClick = { uriHandler.openUri("https://github.com/ruchitgoud") }
                ),
            backgroundColor = BrutalistWhite,
            shadowOffset = 2.dp,
            borderWidth = 2.dp,
            cornerRadius = 999.dp,
            isPressed = isFooterPressed,
            shadowColor = Color(0xFFCCCCCC)
        ) {
            Text(
                text = "Designed by Ruchit Goud",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BrutalistGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    TrackMySpendTheme {
        LandingScreen(onGetStarted = {})
    }
}
