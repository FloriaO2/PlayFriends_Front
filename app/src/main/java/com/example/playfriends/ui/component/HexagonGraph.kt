package com.example.playfriends.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.playfriends.data.model.PlayPreferences
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HexagonGraph(
    playPreferences: PlayPreferences,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = size.minDimension / 2f * 0.8f // 그래프 영역을 약간 줄여 레이블 공간 확보
        val labelOffset = size.minDimension / 2f * 0.2f // 레이블과 그래프 사이 간격

        // 6가지 취향 항목과 레이블
        val preferences = listOf(
            playPreferences.crowd_level to "붐빔",
            playPreferences.activeness_level to "활동성",
            playPreferences.trend_level to "유행",
            playPreferences.planning_level to "계획성",
            playPreferences.location_preference to "실내",
            playPreferences.vibe_level to "도파민"
        )

        // 1. 배경 육각형 그리드 그리기
        drawHexagonGrid(centerX, centerY, radius)

        // 2. 축 그리기
        drawAxes(centerX, centerY, radius)

        // 3. 데이터에 따른 사용자 취향 다각형 그리기
        drawDataPolygon(preferences, centerX, centerY, radius)

        // 4. 각 꼭짓점에 레이블 그리기
        drawLabels(preferences, centerX, centerY, radius, labelOffset, textMeasurer)
    }
}

private fun DrawScope.drawHexagonGrid(centerX: Float, centerY: Float, radius: Float) {
    val gridColor = Color.LightGray.copy(alpha = 0.7f)
    val strokeWidth = 2f
    // 3개의 다른 크기로 육각형 배경을 그립니다.
    for (i in 1..3) {
        val scaledRadius = radius * (i / 3f)
        val hexPath = Path()
        for (j in 0..5) {
            val angle = Math.toRadians(60.0 * j - 30.0) // -30도 회전하여 시작
            val x = centerX + scaledRadius * cos(angle).toFloat()
            val y = centerY + scaledRadius * sin(angle).toFloat()
            if (j == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
        }
        hexPath.close()
        drawPath(path = hexPath, color = gridColor, style = Stroke(width = strokeWidth))
    }
}

private fun DrawScope.drawAxes(centerX: Float, centerY: Float, radius: Float) {
    val axisColor = Color.LightGray.copy(alpha = 0.9f)
    val strokeWidth = 2f
    for (i in 0..5) {
        val angle = Math.toRadians(60.0 * i - 30.0)
        val x = centerX + radius * cos(angle).toFloat()
        val y = centerY + radius * sin(angle).toFloat()
        drawLine(
            color = axisColor,
            start = Offset(centerX, centerY),
            end = Offset(x, y),
            strokeWidth = strokeWidth
        )
    }
}

private fun DrawScope.drawDataPolygon(
    preferences: List<Pair<Float, String>>,
    centerX: Float,
    centerY: Float,
    radius: Float
) {
    val dataPath = Path()
    preferences.forEachIndexed { i, (value, _) ->
        // 값을 0~1 범위로 정규화 (-1 -> 0, 0 -> 0.5, 1 -> 1)
        val normalizedValue = (value + 1) / 2f
        val angle = Math.toRadians(60.0 * i - 30.0)
        val x = centerX + radius * normalizedValue * cos(angle).toFloat()
        val y = centerY + radius * normalizedValue * sin(angle).toFloat()
        if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
    }
    dataPath.close()

    // 데이터 다각형 채우기
    drawPath(
        path = dataPath,
        color = Color(0xFF7EA86A).copy(alpha = 0.5f),
        style = Fill
    )
    // 데이터 다각형 외곽선
    drawPath(
        path = dataPath,
        color = Color(0xFF228B22),
        style = Stroke(width = 5f)
    )
}

private fun DrawScope.drawLabels(
    preferences: List<Pair<Float, String>>,
    centerX: Float,
    centerY: Float,
    radius: Float,
    offset: Float,
    textMeasurer: TextMeasurer
) {
    preferences.forEachIndexed { i, (_, label) ->
        val angle = Math.toRadians(60.0 * i - 30.0)
        val labelRadius = radius + offset
        val x = centerX + labelRadius * cos(angle).toFloat()
        val y = centerY + labelRadius * sin(angle).toFloat()

        val textLayoutResult = textMeasurer.measure(
            text = AnnotatedString(label),
            style = TextStyle(fontSize = 14.sp, color = Color.Black)
        )

        // 텍스트 위치를 중앙으로 조정
        val textX = x - textLayoutResult.size.width / 2
        val textY = y - textLayoutResult.size.height / 2

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(textX.toFloat(), textY.toFloat())
        )
    }
}
