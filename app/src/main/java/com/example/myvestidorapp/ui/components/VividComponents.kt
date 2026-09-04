package com.example.myvestidorapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myvestidorapp.ui.theme.ElectricPink
import com.example.myvestidorapp.ui.theme.ElectricPinkDark

val CoutureBackground = Brush.linearGradient(
    colors = listOf(Color(0xFFFFF7FB), Color(0xFFFAF8FF), Color(0xFFF3F8FF)),
    start = Offset.Zero,
    end = Offset.Infinite,
)

@Composable
fun PrimaryCoutureButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingText: String? = null,
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(if (enabled) 8.dp else 0.dp, shape, ambientColor = ElectricPink.copy(alpha = 0.25f))
            .clip(shape)
            .background(
                if (enabled) Brush.linearGradient(listOf(ElectricPink, ElectricPinkDark))
                else Brush.linearGradient(listOf(Color(0xFFE3E7FA), Color(0xFFDDE3FA))),
            )
            .clickable(enabled = enabled && !loading, onClick = onClick)
            .alpha(if (enabled) 1f else 0.78f),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(10.dp))
            } else if (leadingText != null) {
                Text(leadingText, color = Color.White, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else Color(0xFF9A8790),
            )
        }
    }
}

fun Modifier.dashedRoundedBorder(
    color: Color,
    radius: Dp,
    strokeWidth: Dp = 1.dp,
    dash: Dp = 6.dp,
): Modifier = this.then(
    Modifier.background(Color.Transparent).drawDashedBorder(color, radius, strokeWidth, dash),
)

private fun Modifier.drawDashedBorder(
    color: Color,
    radius: Dp,
    strokeWidth: Dp,
    dash: Dp,
): Modifier = drawWithCache {
    val strokePx = strokeWidth.toPx()
    val inset = strokePx / 2f
    val effect = PathEffect.dashPathEffect(floatArrayOf(dash.toPx(), dash.toPx()))
    onDrawBehind {
        drawRoundRect(
            color = color,
            topLeft = Offset(inset, inset),
            size = Size(size.width - strokePx, size.height - strokePx),
            cornerRadius = CornerRadius(radius.toPx()),
            style = Stroke(width = strokePx, pathEffect = effect),
        )
    }
}

@Composable
fun CameraOutline(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val stroke = 3.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * .12f, size.height * .28f),
            size = Size(size.width * .76f, size.height * .58f),
            cornerRadius = CornerRadius(5.dp.toPx()),
            style = Stroke(stroke),
        )
        drawCircle(
            color = color,
            radius = size.minDimension * .16f,
            center = Offset(size.width / 2f, size.height * .57f),
            style = Stroke(stroke),
        )
        drawLine(
            color = color,
            start = Offset(size.width * .35f, size.height * .28f),
            end = Offset(size.width * .42f, size.height * .16f),
            strokeWidth = stroke,
        )
        drawLine(
            color = color,
            start = Offset(size.width * .42f, size.height * .16f),
            end = Offset(size.width * .62f, size.height * .16f),
            strokeWidth = stroke,
        )
        drawLine(
            color = color,
            start = Offset(size.width * .62f, size.height * .16f),
            end = Offset(size.width * .69f, size.height * .28f),
            strokeWidth = stroke,
        )
    }
}
