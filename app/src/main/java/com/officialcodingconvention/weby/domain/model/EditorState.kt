package com.officialcodingconvention.weby.domain.model

import androidx.compose.ui.geometry.Offset

data class EditorState(
    val project: Project? = null,
    val currentPageId: String? = null,
    val selectedElementIds: Set<String> = emptySet(),
    val hoveredElementId: String? = null,
    val currentBreakpoint: Breakpoint = Breakpoint.DESKTOP,
    val editorMode: EditorMode = EditorMode.VISUAL,
    val zoom: Float = 1f,
    val panOffset: Offset = Offset(0f, 0f),
    val showGrid: Boolean = true,
    val snapToGrid: Boolean = true,
    val gridSize: Int = 8,
    val showRulers: Boolean = true,
    val showGuides: Boolean = true,
    val guides: List<Guide> = emptyList(),
    val clipboard: List<WebElement> = emptyList(),
    val history: EditorHistory = EditorHistory(),
    val isDragging: Boolean = false,
    val draggedElementId: String? = null,
    val dropTargetId: String? = null,
    val resizeHandle: ResizeHandle? = null,
    val isResizing: Boolean = false,
    val selectionRect: SelectionRect? = null,
    val codeTab: CodeTab = CodeTab.HTML,
    val splitViewEnabled: Boolean = false,
    val panelVisibility: PanelVisibility = PanelVisibility()
)

enum class EditorMode {
    VISUAL, CODE, SPLIT, PREVIEW
}

enum class CodeTab {
    HTML, CSS, JAVASCRIPT
}

data class Guide(
    val id: String,
    val orientation: GuideOrientation,
    val position: Float,
    val isLocked: Boolean = false
)

enum class GuideOrientation { HORIZONTAL, VERTICAL }

data class EditorHistory(
    val undoStack: List<HistoryEntry> = emptyList(),
    val redoStack: List<HistoryEntry> = emptyList(),
    val maxSize: Int = 100
)

data class HistoryEntry(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String,
    val type: HistoryActionType,
    val pageId: String,
    val previousState: String,
    val newState: String
)

enum class HistoryActionType {
    ADD_ELEMENT, DELETE_ELEMENT, MOVE_ELEMENT, RESIZE_ELEMENT,
    UPDATE_STYLE, UPDATE_CONTENT, UPDATE_ATTRIBUTES,
    GROUP_ELEMENTS, UNGROUP_ELEMENTS,
    REORDER_ELEMENTS, DUPLICATE_ELEMENTS,
    ADD_PAGE, DELETE_PAGE, UPDATE_PAGE,
    UPDATE_PROJECT_SETTINGS, UPDATE_GLOBAL_STYLES,
    BATCH_OPERATION
}

enum class ResizeHandle {
    TOP_LEFT, TOP, TOP_RIGHT,
    LEFT, RIGHT,
    BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT
}

data class SelectionRect(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)

data class PanelVisibility(
    val leftPanel: Boolean = true,
    val rightPanel: Boolean = true,
    val bottomPanel: Boolean = false
)

data class CanvasViewport(
    val width: Float,
    val height: Float,
    val scrollX: Float = 0f,
    val scrollY: Float = 0f
)

data class ElementBounds(
    val elementId: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)
