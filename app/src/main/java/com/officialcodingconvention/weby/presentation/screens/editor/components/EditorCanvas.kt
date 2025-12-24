package com.officialcodingconvention.weby.presentation.screens.editor.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.unit.dp
import com.officialcodingconvention.weby.domain.model.*

private enum class CanvasResizeHandle {
    TOP_LEFT, TOP_CENTER, TOP_RIGHT,
    MIDDLE_LEFT, MIDDLE_RIGHT,
    BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
}

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
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
    var activeHandle by remember { mutableStateOf<CanvasResizeHandle?>(null) }
    var initialElementBounds by remember { mutableStateOf<Rect?>(null) }

    val viewportWidth by remember(breakpoint) {
        derivedStateOf {
            when (breakpoint) {
                Breakpoint.LARGE_DESKTOP -> 1920f
                Breakpoint.DESKTOP -> 1440f
                Breakpoint.TABLET -> 768f
                Breakpoint.MOBILE -> 375f
            }
        }
    }
    val viewportHeight = 900f

    val elementBoundsCache by remember(elements) {
        derivedStateOf { buildBoundsCache(elements) }
    }

    val viewportRect by remember(canvasSize, zoom, panOffset, viewportWidth, viewportHeight) {
        derivedStateOf {
            if (canvasSize == Size.Zero) Rect.Zero
            else {
                val offsetX = panOffset.x + (canvasSize.width - viewportWidth * zoom) / 2
                val offsetY = panOffset.y + 40f
                Rect(
                    left = -offsetX / zoom,
                    top = -offsetY / zoom,
                    right = (-offsetX + canvasSize.width) / zoom,
                    bottom = (-offsetY + canvasSize.height) / zoom
                )
            }
        }
    }

    val visibleElements by remember(elements, viewportRect, elementBoundsCache) {
        derivedStateOf {
            if (viewportRect == Rect.Zero) elements
            else filterVisibleElements(elements, viewportRect, elementBoundsCache)
        }
    }

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
                .pointerInput(elementBoundsCache, zoom, panOffset, viewportWidth) {
                    detectTapGestures { tapOffset ->
                        val canvasPoint = screenToCanvas(
                            tapOffset, zoom, panOffset, canvasSize, viewportWidth
                        )
                        val tappedElement = findElementAtPoint(
                            elements, canvasPoint, elementBoundsCache
                        )
                        onElementSelected(tappedElement?.id)
                    }
                }
                .pointerInput(elementBoundsCache, selectedElementId, zoom, panOffset, viewportWidth) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val canvasPoint = screenToCanvas(
                                offset, zoom, panOffset, canvasSize, viewportWidth
                            )
                            selectedElementId?.let { selectedId ->
                                val bounds = elementBoundsCache[selectedId]
                                if (bounds != null) {
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
                            val tappedElement = findElementAtPoint(
                                elements, canvasPoint, elementBoundsCache
                            )
                            if (tappedElement != null) {
                                onElementSelected(tappedElement.id)
                                isDragging = true
                                dragStartOffset = canvasPoint
                                initialElementBounds = elementBoundsCache[tappedElement.id]
                            }
                        },
                        onDrag = { change, _ ->
                            if (isDragging && selectedElementId != null) {
                                change.consume()
                                val canvasPoint = screenToCanvas(
                                    change.position, zoom, panOffset, canvasSize, viewportWidth
                                )
                                val dragOffset = canvasPoint - dragStartOffset
                                initialElementBounds?.let { bounds ->
                                    if (activeHandle != null) {
                                        val newSize = calculateResizedSize(
                                            bounds, activeHandle!!, dragOffset
                                        )
                                        onElementResized(selectedElementId, newSize)
                                    } else {
                                        val newPosition = Offset(
                                            bounds.left + dragOffset.x,
                                            bounds.top + dragOffset.y
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
                        },
                        onDragCancel = {
                            isDragging = false
                            activeHandle = null
                            initialElementBounds = null
                        }
                    )
                }
        ) {
            canvasSize = size
            drawRect(color = Color(0xFFF5F5F5))
            if (showGrid) drawCanvasGrid(zoom, panOffset)
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
                    visibleElements.forEach { element ->
                        drawCanvasElement(
                            element = element,
                            zoom = zoom,
                            isSelected = element.id == selectedElementId,
                            primaryColor = primaryColor,
                            outlineColor = outlineColor,
                            boundsCache = elementBoundsCache
                        )
                    }
                    selectedElementId?.let { selectedId ->
                        elementBoundsCache[selectedId]?.let { bounds ->
                            drawSelectionOverlay(bounds, zoom, primaryColor)
                        }
                    }
                }
            }
            drawBreakpointLabel(viewportWidth, zoom, panOffset, size)
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
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out")
            }
            IconButton(onClick = onResetView) {
                Icon(Icons.Default.CenterFocusWeak, contentDescription = "Reset view")
            }
            IconButton(onClick = onZoomIn) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in")
            }
        }
    }
}

@Composable
private fun ZoomIndicator(zoom: Float, modifier: Modifier = Modifier) {
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

private fun buildBoundsCache(elements: List<WebElement>): Map<String, Rect> {
    val cache = mutableMapOf<String, Rect>()
    fun addBounds(elementList: List<WebElement>) {
        for (element in elementList) {
            cache[element.id] = computeElementBounds(element)
            if (element.children.isNotEmpty()) addBounds(element.children)
        }
    }
    addBounds(elements)
    return cache
}

private fun computeElementBounds(element: WebElement): Rect {
    val x = element.styles.left?.let { parseSize(it) } ?: 0f
    val y = element.styles.top?.let { parseSize(it) } ?: 0f
    val width = element.styles.width?.let { parseSize(it) } ?: 100f
    val height = element.styles.height?.let { parseSize(it) } ?: 50f
    return Rect(x, y, x + width, y + height)
}

private fun filterVisibleElements(
    elements: List<WebElement>,
    viewportRect: Rect,
    boundsCache: Map<String, Rect>
): List<WebElement> {
    return elements.filter { element ->
        val bounds = boundsCache[element.id] ?: return@filter true
        bounds.overlaps(viewportRect)
    }
}

private fun parseSize(value: String): Float {
    return value.replace(Regex("[^0-9.-]"), "").toFloatOrNull() ?: 0f
}

private fun parseColor(value: String): Color {
    return try {
        when {
            value.startsWith("#") -> {
                val hex = value.removePrefix("#")
                when (hex.length) {
                    3 -> {
                        val r = hex[0].toString().repeat(2).toInt(16)
                        val g = hex[1].toString().repeat(2).toInt(16)
                        val b = hex[2].toString().repeat(2).toInt(16)
                        Color(r, g, b)
                    }
                    6 -> Color(hex.toLong(16) or 0xFF000000)
                    8 -> Color(hex.toLong(16))
                    else -> Color.Transparent
                }
            }
            value.startsWith("rgb") -> {
                val values = value.replace(Regex("[^0-9,]"), "")
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                if (values.size >= 3) Color(values[0], values[1], values[2])
                else Color.Transparent
            }
            else -> Color.Transparent
        }
    } catch (e: Exception) {
        Color.Transparent
    }
}

private fun getElementBackgroundColor(type: ElementType): Color {
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

private fun DrawScope.drawCanvasGrid(zoom: Float, panOffset: Offset) {
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

private fun DrawScope.drawCanvasElement(
    element: WebElement,
    zoom: Float,
    isSelected: Boolean,
    primaryColor: Color,
    outlineColor: Color,
    boundsCache: Map<String, Rect>
) {
    val bounds = boundsCache[element.id] ?: return
    val scaledBounds = Rect(
        left = bounds.left * zoom,
        top = bounds.top * zoom,
        right = bounds.right * zoom,
        bottom = bounds.bottom * zoom
    )
    val backgroundColor = element.styles.backgroundColor?.let { parseColor(it) }
        ?: getElementBackgroundColor(element.type)
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
    if (!isSelected) {
        drawRect(
            color = outlineColor.copy(alpha = 0.2f),
            topLeft = Offset(scaledBounds.left, scaledBounds.top),
            size = Size(scaledBounds.width, scaledBounds.height),
            style = Stroke(width = 1f)
        )
    }
    drawElementTypeIndicator(element.type, scaledBounds, zoom)
    element.children.forEach { child ->
        drawCanvasElement(child, zoom, false, primaryColor, outlineColor, boundsCache)
    }
}

private fun DrawScope.drawElementTypeIndicator(type: ElementType, bounds: Rect, zoom: Float) {
    val typeColor = getElementTypeColor(type)
    val labelHeight = 14f * zoom
    val labelWidth = 24f * zoom
    drawRect(
        color = typeColor,
        topLeft = Offset(bounds.left, bounds.top - labelHeight),
        size = Size(labelWidth, labelHeight)
    )
}

private fun DrawScope.drawSelectionOverlay(bounds: Rect, zoom: Float, primaryColor: Color) {
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
        Offset(scaledBounds.center.x, scaledBounds.top),
        Offset(scaledBounds.right, scaledBounds.top),
        Offset(scaledBounds.right, scaledBounds.center.y),
        Offset(scaledBounds.right, scaledBounds.bottom),
        Offset(scaledBounds.center.x, scaledBounds.bottom),
        Offset(scaledBounds.left, scaledBounds.bottom),
        Offset(scaledBounds.left, scaledBounds.center.y)
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

private fun DrawScope.drawBreakpointLabel(
    viewportWidth: Float,
    zoom: Float,
    panOffset: Offset,
    canvasSize: Size
) {
    val x = panOffset.x + (canvasSize.width - viewportWidth * zoom) / 2
    drawRect(
        color = Color(0xFF424242),
        topLeft = Offset(x, 8f),
        size = Size(viewportWidth * zoom, 24f)
    )
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

private fun screenToCanvas(
    screenPoint: Offset,
    zoom: Float,
    panOffset: Offset,
    canvasSize: Size,
    viewportWidth: Float
): Offset {
    val offsetX = panOffset.x + (canvasSize.width - viewportWidth * zoom) / 2
    val offsetY = panOffset.y + 40f
    return Offset(
        (screenPoint.x - offsetX) / zoom,
        (screenPoint.y - offsetY) / zoom
    )
}

private fun findElementAtPoint(
    elements: List<WebElement>,
    point: Offset,
    boundsCache: Map<String, Rect>
): WebElement? {
    for (element in elements.reversed()) {
        val childResult = findElementAtPoint(element.children, point, boundsCache)
        if (childResult != null) return childResult
        val bounds = boundsCache[element.id]
        if (bounds != null && bounds.contains(point)) return element
    }
    return null
}

private fun detectResizeHandle(point: Offset, bounds: Rect): CanvasResizeHandle? {
    val handleSize = 12f
    val handles = mapOf(
        CanvasResizeHandle.TOP_LEFT to Offset(bounds.left, bounds.top),
        CanvasResizeHandle.TOP_CENTER to Offset(bounds.center.x, bounds.top),
        CanvasResizeHandle.TOP_RIGHT to Offset(bounds.right, bounds.top),
        CanvasResizeHandle.MIDDLE_RIGHT to Offset(bounds.right, bounds.center.y),
        CanvasResizeHandle.BOTTOM_RIGHT to Offset(bounds.right, bounds.bottom),
        CanvasResizeHandle.BOTTOM_CENTER to Offset(bounds.center.x, bounds.bottom),
        CanvasResizeHandle.BOTTOM_LEFT to Offset(bounds.left, bounds.bottom),
        CanvasResizeHandle.MIDDLE_LEFT to Offset(bounds.left, bounds.center.y)
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
    handle: CanvasResizeHandle,
    dragOffset: Offset
): Size {
    val minSize = 20f
    return when (handle) {
        CanvasResizeHandle.TOP_LEFT -> Size(
            (bounds.width - dragOffset.x).coerceAtLeast(minSize),
            (bounds.height - dragOffset.y).coerceAtLeast(minSize)
        )
        CanvasResizeHandle.TOP_CENTER -> Size(
            bounds.width,
            (bounds.height - dragOffset.y).coerceAtLeast(minSize)
        )
        CanvasResizeHandle.TOP_RIGHT -> Size(
            (bounds.width + dragOffset.x).coerceAtLeast(minSize),
            (bounds.height - dragOffset.y).coerceAtLeast(minSize)
        )
        CanvasResizeHandle.MIDDLE_RIGHT -> Size(
            (bounds.width + dragOffset.x).coerceAtLeast(minSize),
            bounds.height
        )
        CanvasResizeHandle.BOTTOM_RIGHT -> Size(
            (bounds.width + dragOffset.x).coerceAtLeast(minSize),
            (bounds.height + dragOffset.y).coerceAtLeast(minSize)
        )
        CanvasResizeHandle.BOTTOM_CENTER -> Size(
            bounds.width,
            (bounds.height + dragOffset.y).coerceAtLeast(minSize)
        )
        CanvasResizeHandle.BOTTOM_LEFT -> Size(
            (bounds.width - dragOffset.x).coerceAtLeast(minSize),
            (bounds.height + dragOffset.y).coerceAtLeast(minSize)
        )
        CanvasResizeHandle.MIDDLE_LEFT -> Size(
            (bounds.width - dragOffset.x).coerceAtLeast(minSize),
            bounds.height
        )
    }
}
