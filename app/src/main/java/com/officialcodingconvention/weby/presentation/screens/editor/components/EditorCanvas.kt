package com.officialcodingconvention.weby.presentation.screens.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialcodingconvention.weby.domain.model.*

@Composable
fun EditorCanvas(
    elements: List<WebElement>,
    selectedElementId: String?,
    breakpoint: Breakpoint,
    zoom: Float,
    panOffset: Offset,
    showGrid: Boolean,
    onElementSelected: (String?) -> Unit,
    onElementMoved: (String, Offset) -> Unit,
    onElementResized: (String, Size) -> Unit,
    onPanChanged: (Offset) -> Unit,
    onZoomChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
    var currentDragOffset by remember { mutableStateOf(Offset.Zero) }
    var activeHandle by remember { mutableStateOf<ResizeHandle?>(null) }
    var initialElementBounds by remember { mutableStateOf<Rect?>(null) }

    val viewportWidth = when (breakpoint) {
        Breakpoint.LARGE_DESKTOP -> 1920f
        Breakpoint.DESKTOP -> 1440f
        Breakpoint.TABLET -> 768f
        Breakpoint.MOBILE -> 375f
    }
    val viewportHeight = 900f

    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        if (!isDragging) {
                            val newZoom = (zoom * gestureZoom).coerceIn(0.25f, 3f)
                            onZoomChanged(newZoom)
                            onPanChanged(panOffset + pan)
                        }
                    }
                }
                .pointerInput(elements, selectedElementId, zoom, panOffset) {
                    detectTapGestures { tapOffset ->
                        val canvasPoint = screenToCanvas(tapOffset, zoom, panOffset, canvasSize)
                        val tappedElement = findElementAtPoint(elements, canvasPoint)
                        onElementSelected(tappedElement?.id)
                    }
                }
                .pointerInput(elements, selectedElementId, zoom, panOffset) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val canvasPoint = screenToCanvas(offset, zoom, panOffset, canvasSize)

                            selectedElementId?.let { selectedId ->
                                val selectedElement = findElementById(elements, selectedId)
                                selectedElement?.let { element ->
                                    val bounds = getElementBounds(element)
                                    val handle = detectResizeHandle(canvasPoint, bounds)
                                    if (handle != null) {
                                        activeHandle = handle
                                        initialElementBounds = bounds
                                        isDragging = true
                                        dragStartOffset = canvasPoint
                                        return@detectDragGestures
                                    }

                                    if (bounds.contains(canvasPoint)) {
                                        isDragging = true
                                        dragStartOffset = canvasPoint
                                        initialElementBounds = bounds
                                        return@detectDragGestures
                                    }
                                }
                            }

                            val tappedElement = findElementAtPoint(elements, canvasPoint)
                            if (tappedElement != null) {
                                onElementSelected(tappedElement.id)
                                isDragging = true
                                dragStartOffset = canvasPoint
                                initialElementBounds = getElementBounds(tappedElement)
                            }
                        },
                        onDrag = { change, _ ->
                            if (isDragging && selectedElementId != null) {
                                change.consume()
                                val canvasPoint = screenToCanvas(change.position, zoom, panOffset, canvasSize)
                                currentDragOffset = canvasPoint - dragStartOffset

                                initialElementBounds?.let { bounds ->
                                    if (activeHandle != null) {
                                        val newSize = calculateResizedSize(
                                            bounds,
                                            activeHandle!!,
                                            currentDragOffset
                                        )
                                        onElementResized(selectedElementId, newSize)
                                    } else {
                                        val newPosition = Offset(
                                            bounds.left + currentDragOffset.x,
                                            bounds.top + currentDragOffset.y
                                        )
                                        onElementMoved(selectedElementId, newPosition)
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            isDragging = false
                            activeHandle = null
                            initialElementBounds = null
                            currentDragOffset = Offset.Zero
                        },
                        onDragCancel = {
                            isDragging = false
                            activeHandle = null
                            initialElementBounds = null
                            currentDragOffset = Offset.Zero
                        }
                    )
                }
        ) {
            canvasSize = size

            drawRect(color = Color(0xFFF5F5F5))

            if (showGrid) {
                drawGrid(zoom, panOffset)
            }

            translate(
                left = panOffset.x + (size.width - viewportWidth * zoom) / 2,
                top = panOffset.y + 40f
            ) {
                drawRect(
                    color = Color.White,
                    topLeft = Offset.Zero,
                    size = Size(viewportWidth * zoom, viewportHeight * zoom),
                    style = Stroke(width = 1f)
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset.Zero,
                    size = Size(viewportWidth * zoom, viewportHeight * zoom)
                )

                clipRect(
                    left = 0f,
                    top = 0f,
                    right = viewportWidth * zoom,
                    bottom = viewportHeight * zoom
                ) {
                    elements.forEach { element ->
                        drawElement(
                            element = element,
                            zoom = zoom,
                            isSelected = element.id == selectedElementId,
                            primaryColor = primaryColor,
                            outlineColor = outlineColor,
                            textMeasurer = textMeasurer
                        )
                    }

                    selectedElementId?.let { selectedId ->
                        findElementById(elements, selectedId)?.let { element ->
                            drawSelectionOverlay(element, zoom, primaryColor)
                        }
                    }
                }
            }

            drawBreakpointIndicator(breakpoint, viewportWidth, zoom, panOffset, size)
        }

        CanvasControls(
            zoom = zoom,
            onZoomIn = { onZoomChanged((zoom * 1.25f).coerceAtMost(3f)) },
            onZoomOut = { onZoomChanged((zoom / 1.25f).coerceAtLeast(0.25f)) },
            onResetView = {
                onZoomChanged(1f)
                onPanChanged(Offset.Zero)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        ZoomIndicator(
            zoom = zoom,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

@Composable
private fun CanvasControls(
    zoom: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetView: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onZoomOut) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Zoom out"
                )
            }

            IconButton(onClick = onResetView) {
                Icon(
                    imageVector = Icons.Default.CenterFocusWeak,
                    contentDescription = "Reset view"
                )
            }

            IconButton(onClick = onZoomIn) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom in"
                )
            }
        }
    }
}

@Composable
private fun ZoomIndicator(
    zoom: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = "${(zoom * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

private fun DrawScope.drawGrid(zoom: Float, panOffset: Offset) {
    val gridSize = 20f * zoom
    val gridColor = Color(0xFFE0E0E0)
    val majorGridColor = Color(0xFFBDBDBD)

    val startX = (panOffset.x % gridSize) - gridSize
    val startY = (panOffset.y % gridSize) - gridSize

    var x = startX
    var gridIndex = 0
    while (x < size.width + gridSize) {
        val isMajor = gridIndex % 5 == 0
        drawLine(
            color = if (isMajor) majorGridColor else gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = if (isMajor) 1f else 0.5f
        )
        x += gridSize
        gridIndex++
    }

    var y = startY
    gridIndex = 0
    while (y < size.height + gridSize) {
        val isMajor = gridIndex % 5 == 0
        drawLine(
            color = if (isMajor) majorGridColor else gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = if (isMajor) 1f else 0.5f
        )
        y += gridSize
        gridIndex++
    }
}

private fun DrawScope.drawElement(
    element: WebElement,
    zoom: Float,
    isSelected: Boolean,
    primaryColor: Color,
    outlineColor: Color,
    textMeasurer: TextMeasurer
) {
    val bounds = getElementBounds(element)
    val scaledBounds = Rect(
        left = bounds.left * zoom,
        top = bounds.top * zoom,
        right = bounds.right * zoom,
        bottom = bounds.bottom * zoom
    )

    val backgroundColor = element.styles.backgroundColor?.let { parseColor(it) }
        ?: getDefaultBackgroundColor(element.type)

    drawRect(
        color = backgroundColor,
        topLeft = Offset(scaledBounds.left, scaledBounds.top),
        size = Size(scaledBounds.width, scaledBounds.height)
    )

    element.styles.border?.let { border ->
        val borderColor = parseBorderColor(border)
        val borderWidth = parseBorderWidth(border)
        drawRect(
            color = borderColor,
            topLeft = Offset(scaledBounds.left, scaledBounds.top),
            size = Size(scaledBounds.width, scaledBounds.height),
            style = Stroke(width = borderWidth * zoom)
        )
    }

    if (element.content.isNotEmpty() && isTextElement(element.type)) {
        val textColor = element.styles.color?.let { parseColor(it) } ?: Color.Black
        val fontSize = element.styles.fontSize?.let { parseFontSize(it) } ?: 14f

        val textStyle = TextStyle(
            color = textColor,
            fontSize = (fontSize * zoom).sp
        )

        val textLayoutResult = textMeasurer.measure(
            text = element.content,
            style = textStyle,
            constraints = androidx.compose.ui.unit.Constraints(
                maxWidth = scaledBounds.width.toInt().coerceAtLeast(1)
            )
        )

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                scaledBounds.left + 4 * zoom,
                scaledBounds.top + 4 * zoom
            )
        )
    }

    if (!isSelected) {
        drawRect(
            color = outlineColor.copy(alpha = 0.2f),
            topLeft = Offset(scaledBounds.left, scaledBounds.top),
            size = Size(scaledBounds.width, scaledBounds.height),
            style = Stroke(width = 1f)
        )
    }

    drawElementTypeIndicator(element, scaledBounds, zoom, textMeasurer)

    element.children.forEach { child ->
        drawElement(child, zoom, isSelected = false, primaryColor, outlineColor, textMeasurer)
    }
}

private fun DrawScope.drawElementTypeIndicator(
    element: WebElement,
    bounds: Rect,
    zoom: Float,
    textMeasurer: TextMeasurer
) {
    val label = element.type.name.lowercase().take(3)
    val labelStyle = TextStyle(
        color = Color.White,
        fontSize = (8f * zoom).sp
    )

    val labelResult = textMeasurer.measure(label, labelStyle)
    val labelPadding = 2f * zoom
    val labelWidth = labelResult.size.width + labelPadding * 2
    val labelHeight = labelResult.size.height + labelPadding * 2

    drawRect(
        color = getElementTypeColor(element.type),
        topLeft = Offset(bounds.left, bounds.top - labelHeight),
        size = Size(labelWidth, labelHeight)
    )

    drawText(
        textLayoutResult = labelResult,
        topLeft = Offset(bounds.left + labelPadding, bounds.top - labelHeight + labelPadding)
    )
}

private fun DrawScope.drawSelectionOverlay(
    element: WebElement,
    zoom: Float,
    primaryColor: Color
) {
    val bounds = getElementBounds(element)
    val scaledBounds = Rect(
        left = bounds.left * zoom,
        top = bounds.top * zoom,
        right = bounds.right * zoom,
        bottom = bounds.bottom * zoom
    )

    drawRect(
        color = primaryColor,
        topLeft = Offset(scaledBounds.left, scaledBounds.top),
        size = Size(scaledBounds.width, scaledBounds.height),
        style = Stroke(width = 2f)
    )

    val handleSize = 8f
    val handles = listOf(
        Offset(scaledBounds.left, scaledBounds.top),
        Offset(scaledBounds.centerX, scaledBounds.top),
        Offset(scaledBounds.right, scaledBounds.top),
        Offset(scaledBounds.right, scaledBounds.centerY),
        Offset(scaledBounds.right, scaledBounds.bottom),
        Offset(scaledBounds.centerX, scaledBounds.bottom),
        Offset(scaledBounds.left, scaledBounds.bottom),
        Offset(scaledBounds.left, scaledBounds.centerY)
    )

    handles.forEach { handle ->
        drawRect(
            color = Color.White,
            topLeft = Offset(handle.x - handleSize / 2, handle.y - handleSize / 2),
            size = Size(handleSize, handleSize)
        )
        drawRect(
            color = primaryColor,
            topLeft = Offset(handle.x - handleSize / 2, handle.y - handleSize / 2),
            size = Size(handleSize, handleSize),
            style = Stroke(width = 1.5f)
        )
    }
}

private fun DrawScope.drawBreakpointIndicator(
    breakpoint: Breakpoint,
    viewportWidth: Float,
    zoom: Float,
    panOffset: Offset,
    canvasSize: Size
) {
    val label = when (breakpoint) {
        Breakpoint.LARGE_DESKTOP -> "Large Desktop (1920px)"
        Breakpoint.DESKTOP -> "Desktop (1440px)"
        Breakpoint.TABLET -> "Tablet (768px)"
        Breakpoint.MOBILE -> "Mobile (375px)"
    }

    val x = panOffset.x + (canvasSize.width - viewportWidth * zoom) / 2

    drawRect(
        color = Color(0xFF424242),
        topLeft = Offset(x, 8f),
        size = Size(viewportWidth * zoom, 24f)
    )
}

private fun getElementBounds(element: WebElement): Rect {
    val x = element.styles.left?.let { parseSize(it) } ?: 0f
    val y = element.styles.top?.let { parseSize(it) } ?: 0f
    val width = element.styles.width?.let { parseSize(it) } ?: 100f
    val height = element.styles.height?.let { parseSize(it) } ?: 50f

    return Rect(x, y, x + width, y + height)
}

private fun parseSize(value: String): Float {
    return value.replace(Regex("[^0-9.]"), "").toFloatOrNull() ?: 0f
}

private fun parseColor(value: String): Color {
    return try {
        when {
            value.startsWith("#") -> {
                val colorLong = value.removePrefix("#").toLong(16)
                if (value.length == 7) {
                    Color(colorLong or 0xFF000000)
                } else {
                    Color(colorLong)
                }
            }
            value.startsWith("rgb") -> {
                val values = value.replace(Regex("[^0-9,]"), "")
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                if (values.size >= 3) {
                    Color(values[0], values[1], values[2])
                } else Color.Transparent
            }
            else -> Color.Transparent
        }
    } catch (e: Exception) {
        Color.Transparent
    }
}

private fun parseBorderColor(border: String): Color {
    val parts = border.split(" ")
    val colorPart = parts.lastOrNull { it.startsWith("#") || it.startsWith("rgb") }
    return colorPart?.let { parseColor(it) } ?: Color.Gray
}

private fun parseBorderWidth(border: String): Float {
    val parts = border.split(" ")
    val widthPart = parts.firstOrNull { it.endsWith("px") }
    return widthPart?.let { parseSize(it) } ?: 1f
}

private fun parseFontSize(value: String): Float {
    return parseSize(value).takeIf { it > 0 } ?: 14f
}

private fun getDefaultBackgroundColor(type: ElementType): Color {
    return when (type) {
        ElementType.DIV, ElementType.SECTION, ElementType.ARTICLE,
        ElementType.HEADER, ElementType.FOOTER, ElementType.MAIN,
        ElementType.NAV, ElementType.ASIDE -> Color(0xFFF5F5F5)
        ElementType.BUTTON -> Color(0xFF6366F1)
        ElementType.INPUT, ElementType.TEXTAREA -> Color.White
        ElementType.IMAGE -> Color(0xFFE0E0E0)
        else -> Color.Transparent
    }
}

private fun getElementTypeColor(type: ElementType): Color {
    return when (type) {
        ElementType.DIV -> Color(0xFF2196F3)
        ElementType.SECTION -> Color(0xFF4CAF50)
        ElementType.HEADER, ElementType.FOOTER -> Color(0xFF9C27B0)
        ElementType.NAV -> Color(0xFFFF9800)
        ElementType.BUTTON -> Color(0xFFE91E63)
        ElementType.INPUT, ElementType.TEXTAREA, ElementType.FORM -> Color(0xFF00BCD4)
        ElementType.IMAGE, ElementType.VIDEO -> Color(0xFFFF5722)
        ElementType.H1, ElementType.H2, ElementType.H3, ElementType.H4,
        ElementType.H5, ElementType.H6, ElementType.P, ElementType.SPAN -> Color(0xFF607D8B)
        ElementType.A -> Color(0xFF3F51B5)
        ElementType.UL, ElementType.OL, ElementType.LI -> Color(0xFF795548)
        else -> Color(0xFF9E9E9E)
    }
}

private fun isTextElement(type: ElementType): Boolean {
    return type in listOf(
        ElementType.H1, ElementType.H2, ElementType.H3, ElementType.H4,
        ElementType.H5, ElementType.H6, ElementType.P, ElementType.SPAN,
        ElementType.A, ElementType.LABEL, ElementType.BUTTON
    )
}

private fun screenToCanvas(
    screenPoint: Offset,
    zoom: Float,
    panOffset: Offset,
    canvasSize: Size
): Offset {
    val viewportWidth = 1440f
    val offsetX = panOffset.x + (canvasSize.width - viewportWidth * zoom) / 2
    val offsetY = panOffset.y + 40f

    return Offset(
        (screenPoint.x - offsetX) / zoom,
        (screenPoint.y - offsetY) / zoom
    )
}

private fun findElementAtPoint(elements: List<WebElement>, point: Offset): WebElement? {
    for (element in elements.reversed()) {
        val childResult = findElementAtPoint(element.children, point)
        if (childResult != null) return childResult

        val bounds = getElementBounds(element)
        if (bounds.contains(point)) return element
    }
    return null
}

private fun findElementById(elements: List<WebElement>, id: String): WebElement? {
    for (element in elements) {
        if (element.id == id) return element
        val childResult = findElementById(element.children, id)
        if (childResult != null) return childResult
    }
    return null
}

private enum class ResizeHandle {
    TOP_LEFT, TOP_CENTER, TOP_RIGHT,
    MIDDLE_LEFT, MIDDLE_RIGHT,
    BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
}

private fun detectResizeHandle(point: Offset, bounds: Rect): ResizeHandle? {
    val handleSize = 12f
    val handles = mapOf(
        ResizeHandle.TOP_LEFT to Offset(bounds.left, bounds.top),
        ResizeHandle.TOP_CENTER to Offset(bounds.centerX, bounds.top),
        ResizeHandle.TOP_RIGHT to Offset(bounds.right, bounds.top),
        ResizeHandle.MIDDLE_RIGHT to Offset(bounds.right, bounds.centerY),
        ResizeHandle.BOTTOM_RIGHT to Offset(bounds.right, bounds.bottom),
        ResizeHandle.BOTTOM_CENTER to Offset(bounds.centerX, bounds.bottom),
        ResizeHandle.BOTTOM_LEFT to Offset(bounds.left, bounds.bottom),
        ResizeHandle.MIDDLE_LEFT to Offset(bounds.left, bounds.centerY)
    )

    for ((handle, handlePoint) in handles) {
        val handleBounds = Rect(
            handlePoint.x - handleSize / 2,
            handlePoint.y - handleSize / 2,
            handlePoint.x + handleSize / 2,
            handlePoint.y + handleSize / 2
        )
        if (handleBounds.contains(point)) return handle
    }
    return null
}

private fun calculateResizedSize(
    bounds: Rect,
    handle: ResizeHandle,
    dragOffset: Offset
): Size {
    val minSize = 20f

    return when (handle) {
        ResizeHandle.TOP_LEFT -> Size(
            (bounds.width - dragOffset.x).coerceAtLeast(minSize),
            (bounds.height - dragOffset.y).coerceAtLeast(minSize)
        )
        ResizeHandle.TOP_CENTER -> Size(
            bounds.width,
            (bounds.height - dragOffset.y).coerceAtLeast(minSize)
        )
        ResizeHandle.TOP_RIGHT -> Size(
            (bounds.width + dragOffset.x).coerceAtLeast(minSize),
            (bounds.height - dragOffset.y).coerceAtLeast(minSize)
        )
        ResizeHandle.MIDDLE_RIGHT -> Size(
            (bounds.width + dragOffset.x).coerceAtLeast(minSize),
            bounds.height
        )
        ResizeHandle.BOTTOM_RIGHT -> Size(
            (bounds.width + dragOffset.x).coerceAtLeast(minSize),
            (bounds.height + dragOffset.y).coerceAtLeast(minSize)
        )
        ResizeHandle.BOTTOM_CENTER -> Size(
            bounds.width,
            (bounds.height + dragOffset.y).coerceAtLeast(minSize)
        )
        ResizeHandle.BOTTOM_LEFT -> Size(
            (bounds.width - dragOffset.x).coerceAtLeast(minSize),
            (bounds.height + dragOffset.y).coerceAtLeast(minSize)
        )
        ResizeHandle.MIDDLE_LEFT -> Size(
            (bounds.width - dragOffset.x).coerceAtLeast(minSize),
            bounds.height
        )
    }
}
