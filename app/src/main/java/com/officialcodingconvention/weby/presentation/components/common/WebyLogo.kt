package com.officialcodingconvention.weby.presentation.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.officialcodingconvention.weby.core.theme.WebyPrimary
import com.officialcodingconvention.weby.core.theme.WebySecondary
import com.officialcodingconvention.weby.core.theme.WebyTertiary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WebyLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    animated: Boolean = false,
    primaryColor: Color = WebyPrimary,
    secondaryColor: Color = WebySecondary,
    tertiaryColor: Color = WebyTertiary
) {
    val rotation by if (animated) {
        rememberInfiniteTransition(label = "logo_rotation").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing)
            ),
            label = "rotation"
        )
    } else {
        androidx.compose.runtime.remember { mutableFloatStateOf(0f) }
    }

    val pulse by if (animated) {
        rememberInfiniteTransition(label = "logo_pulse").animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        androidx.compose.runtime.remember { mutableFloatStateOf(1f) }
    }

    Canvas(
        modifier = modifier.size(size)
    ) {
        val center = Offset(this.size.width / 2, this.size.height / 2)
        val baseRadius = this.size.minDimension / 2 * 0.85f

        // Draw background circle with gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.15f),
                    primaryColor.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = center,
                radius = baseRadius * 1.2f
            ),
            radius = baseRadius * 1.2f,
            center = center
        )

        // Draw orbital rings
        rotate(rotation, center) {
            drawOrbitalRing(
                center = center,
                radius = baseRadius * 0.9f,
                color = secondaryColor.copy(alpha = 0.3f),
                strokeWidth = 1.5f
            )
        }

        rotate(-rotation * 0.7f, center) {
            drawOrbitalRing(
                center = center,
                radius = baseRadius * 0.75f,
                color = tertiaryColor.copy(alpha = 0.25f),
                strokeWidth = 1.5f
            )
        }

        // Draw connection nodes
        val nodeCount = 6
        val nodeRadius = baseRadius * 0.08f
        for (i in 0 until nodeCount) {
            val angle = (rotation + i * 360f / nodeCount) * (Math.PI / 180f)
            val nodeX = center.x + cos(angle).toFloat() * baseRadius * 0.85f
            val nodeY = center.y + sin(angle).toFloat() * baseRadius * 0.85f

            drawCircle(
                color = when (i % 3) {
                    0 -> primaryColor
                    1 -> secondaryColor
                    else -> tertiaryColor
                }.copy(alpha = 0.8f),
                radius = nodeRadius * pulse,
                center = Offset(nodeX, nodeY)
            )
        }

        // Draw stylized "W" with angle brackets
        drawWebySymbol(
            center = center,
            size = baseRadius * 1.4f,
            primaryColor = primaryColor,
            scale = pulse
        )
    }
}

private fun DrawScope.drawOrbitalRing(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float
) {
    drawCircle(
        color = color,
        radius = radius,
        center = center,
        style = Stroke(
            width = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        )
    )
}

private fun DrawScope.drawWebySymbol(
    center: Offset,
    size: Float,
    primaryColor: Color,
    scale: Float
) {
    val strokeWidth = size * 0.08f * scale
    val halfSize = size / 2

    // Left angle bracket <
    val leftBracketPath = Path().apply {
        val leftX = center.x - halfSize * 0.5f
        moveTo(leftX, center.y - halfSize * 0.25f)
        lineTo(leftX - halfSize * 0.2f, center.y)
        lineTo(leftX, center.y + halfSize * 0.25f)
    }

    drawPath(
        path = leftBracketPath,
        color = primaryColor.copy(alpha = 0.6f),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Right angle bracket >
    val rightBracketPath = Path().apply {
        val rightX = center.x + halfSize * 0.5f
        moveTo(rightX, center.y - halfSize * 0.25f)
        lineTo(rightX + halfSize * 0.2f, center.y)
        lineTo(rightX, center.y + halfSize * 0.25f)
    }

    drawPath(
        path = rightBracketPath,
        color = primaryColor.copy(alpha = 0.6f),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Stylized "W" in the center
    val wPath = Path().apply {
        val wWidth = halfSize * 0.55f
        val wHeight = halfSize * 0.4f
        val wTop = center.y - wHeight * 0.5f
        val wBottom = center.y + wHeight * 0.5f
        val wLeft = center.x - wWidth * 0.5f
        val wRight = center.x + wWidth * 0.5f

        moveTo(wLeft, wTop)
        lineTo(wLeft + wWidth * 0.25f, wBottom)
        lineTo(center.x, wTop + wHeight * 0.35f)
        lineTo(wRight - wWidth * 0.25f, wBottom)
        lineTo(wRight, wTop)
    }

    // W with gradient stroke
    drawPath(
        path = wPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                primaryColor,
                primaryColor.copy(alpha = 0.8f)
            )
        ),
        style = Stroke(
            width = strokeWidth * 1.3f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private val EaseInOutCubic = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1f)

private fun mutableFloatStateOf(value: Float) = androidx.compose.runtime.mutableFloatStateOf(value)
