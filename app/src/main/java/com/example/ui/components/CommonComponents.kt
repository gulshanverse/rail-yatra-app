package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RailGold
import com.example.ui.theme.RailOrangeAccent

/**
 * Shimmering loading effect for cards and lists
 */
@Composable
fun ShimmerCard(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "translate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(10f, 10f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
    )
}

/**
 * Glassmorphic Card container with transparent surface, subtle outline, and background blur impression
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    } else {
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    }

    Column(
        modifier = cardModifier,
        content = content
    )
}

/**
 * Linear Gradient Background Brush
 */
@Composable
fun premiumBackgroundBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.background
        )
    )
}

/**
 * Custom interactive canvas railway tracker showing dynamic routes, stops, and current live location
 */
@Composable
fun InteractiveTrackMap(
    currentStationIndex: Int,
    stationsList: List<String>,
    delayMinutes: Int,
    modifier: Modifier = Modifier
) {
    val totalStations = stationsList.size
    if (totalStations == 0) return

    val infiniteTransition = rememberInfiniteTransition(label = "trainPulse")
    val pulseRatio by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Text(
        text = "Interactive Live Route Map",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw track line (Railway metal lines style)
            val trackY = height / 2f
            val startX = 40.dp.toPx()
            val endX = width - 40.dp.toPx()

            // Draw ties (railway sleeper blocks)
            val sleepersCount = 20
            val sleeperSpacing = (endX - startX) / sleepersCount
            for (i in 0..sleepersCount) {
                val sleeperX = startX + i * sleeperSpacing
                drawLine(
                    color = Color.Gray.copy(alpha = 0.4f),
                    start = Offset(sleeperX, trackY - 12f),
                    end = Offset(sleeperX, trackY + 12f),
                    strokeWidth = 6f
                )
            }

            // Draw parallel rail tracks
            drawLine(
                color = Color.DarkGray,
                start = Offset(startX, trackY - 8f),
                end = Offset(endX, trackY - 8f),
                strokeWidth = 4f
            )
            drawLine(
                color = Color.DarkGray,
                start = Offset(startX, trackY + 8f),
                end = Offset(endX, trackY + 8f),
                strokeWidth = 4f
            )

            // Draw station coordinates
            val stationSpacing = (endX - startX) / (totalStations - 1)
            for (i in 0 until totalStations) {
                val dotX = startX + i * stationSpacing
                val isVisited = i < currentStationIndex
                val isCurrent = i == currentStationIndex

                val dotColor = when {
                    isCurrent -> if (delayMinutes > 0) Color.Red else RailOrangeAccent
                    isVisited -> Color.Gray
                    else -> RailGold
                }

                // Station nodes
                drawCircle(
                    color = dotColor,
                    radius = if (isCurrent) 10.dp.toPx() else 6.dp.toPx(),
                    center = Offset(dotX, trackY)
                )

                if (isCurrent) {
                    // Pulsing animation for the active train location
                    drawCircle(
                        color = dotColor.copy(alpha = 0.4f),
                        radius = 16.dp.toPx() * pulseRatio,
                        center = Offset(dotX, trackY),
                        style = Stroke(width = 3f)
                    )
                }
            }
        }

        // Draw station text labels under the tracks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stationsList.forEachIndexed { index, name ->
                val label = name.substringBefore(" ").take(4)
                val isCurrent = index == currentStationIndex
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = if (isCurrent) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
