package com.officialcodingconvention.weby.presentation.screens.editor

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.officialcodingconvention.weby.WebyApplication
import com.officialcodingconvention.weby.data.repository.ProjectRepositoryImpl
import com.officialcodingconvention.weby.domain.model.*
import com.officialcodingconvention.weby.domain.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class EditorUiState(
    val isLoading: Boolean = true,
    val project: Project? = null,
    val currentPage: Page? = null,
    val editorState: EditorState = EditorState(),
    val generatedHtml: String = "",
    val generatedCss: String = "",
    val generatedJs: String = "",
    val error: String? = null,
    val isSaving: Boolean = false,
    val lastSaved: Long? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
) {
    val currentPageElements: List<WebElement>
        get() = currentPage?.elements ?: emptyList()
}

// Lightweight snapshot for undo/redo - stores only element IDs and essential data
private data class PageSnapshot(
    val pageId: String,
    val elementsJson: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

class EditorViewModel(
    private val projectRepository: ProjectRepository,
    private val codeGenerator: CodeGenerator = CodeGenerator(),
    private val gson: Gson = Gson()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var autoSaveJob: Job? = null

    // Memory-bounded undo/redo stacks
    private val undoStack = ArrayDeque<PageSnapshot>(MAX_HISTORY_SIZE)
    private val redoStack = ArrayDeque<PageSnapshot>(MAX_HISTORY_SIZE)

    companion object {
        private const val MAX_HISTORY_SIZE = 30 // Limit to prevent OOM

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = WebyApplication.getInstance()
                val repository = ProjectRepositoryImpl(
                    projectDao = app.database.projectDao(),
                    fileSystemManager = app.fileSystemManager,
                    gson = Gson()
                )
                return EditorViewModel(repository) as T
            }
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val project = projectRepository.getProjectById(projectId)
                if (project != null) {
                    val currentPage = project.pages.find { it.isHomepage }
                        ?: project.pages.firstOrNull()

                    // Clear history on project load
                    undoStack.clear()
                    redoStack.clear()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            project = project,
                            currentPage = currentPage,
                            editorState = it.editorState.copy(
                                project = project,
                                currentPageId = currentPage?.id
                            ),
                            canUndo = false,
                            canRedo = false
                        )
                    }
                    regenerateCode()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Project not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectElement(elementId: String?, multiSelect: Boolean = false) {
        _uiState.update { state ->
            val newSelection = when {
                elementId == null -> emptySet()
                multiSelect -> {
                    if (elementId in state.editorState.selectedElementIds) {
                        state.editorState.selectedElementIds - elementId
                    } else {
                        state.editorState.selectedElementIds + elementId
                    }
                }
                else -> setOf(elementId)
            }
            state.copy(
                editorState = state.editorState.copy(selectedElementIds = newSelection)
            )
        }
    }

    fun clearSelection() {
        _uiState.update { state ->
            state.copy(editorState = state.editorState.copy(selectedElementIds = emptySet()))
        }
    }

    fun addElement(element: WebElement, parentId: String? = null, index: Int? = null) {
        saveSnapshot("Add ${element.type.name}")
        updateCurrentPage { page ->
            val newElement = element.copy(parentId = parentId)
            val newElements = if (parentId != null) {
                insertElementIntoParent(page.elements, newElement, parentId, index)
            } else {
                if (index != null) {
                    page.elements.toMutableList().apply { add(index, newElement) }
                } else {
                    page.elements + newElement
                }
            }
            page.copy(elements = newElements)
        }
    }

    fun addElementAtEnd(elementType: ElementType) {
        val componentDef = ComponentLibrary.getComponent(elementType) ?: return
        val newElement = WebElement(
            id = UUID.randomUUID().toString(),
            type = elementType,
            tag = componentDef.defaultTag,
            name = componentDef.name,
            content = componentDef.defaultContent,
            attributes = componentDef.defaultAttributes,
            styles = componentDef.defaultStyles
        )
        addElement(newElement)
    }

    fun deleteElement(elementId: String) {
        saveSnapshot("Delete element")
        updateCurrentPage { page ->
            page.copy(elements = removeElementById(page.elements, elementId))
        }
        _uiState.update { state ->
            state.copy(
                editorState = state.editorState.copy(
                    selectedElementIds = state.editorState.selectedElementIds - elementId
                )
            )
        }
    }

    fun moveElement(elementId: String, offset: Offset) {
        // Don't save snapshot for every move - only on drag end
        updateElementByIdSilent(elementId) { element ->
            element.copy(
                styles = element.styles.copy(
                    position = "absolute",
                    top = "${offset.y}px",
                    left = "${offset.x}px"
                )
            )
        }
    }

    fun finishMoveElement(elementId: String) {
        saveSnapshot("Move element")
        updateHistoryState()
    }

    fun resizeElement(elementId: String, size: Size) {
        updateElementByIdSilent(elementId) { element ->
            element.copy(
                styles = element.styles.copy(
                    width = "${size.width}px",
                    height = "${size.height}px"
                )
            )
        }
    }

    fun finishResizeElement(elementId: String) {
        saveSnapshot("Resize element")
        updateHistoryState()
    }

    fun addElementFromComponent(component: ComponentDefinition) {
        val newElement = WebElement(
            id = UUID.randomUUID().toString(),
            type = component.type,
            tag = component.defaultTag,
            name = component.name,
            content = component.defaultContent,
            attributes = component.defaultAttributes,
            styles = component.defaultStyles.copy(
                position = "absolute",
                left = "50px",
                top = "50px"
            )
        )
        addElement(newElement)
    }

    fun duplicateElement(elementId: String) {
        val currentPage = _uiState.value.currentPage ?: return
        val element = findElementById(currentPage.elements, elementId) ?: return

        saveSnapshot("Duplicate element")
        val duplicated = duplicateElementTree(element)
        updateCurrentPage { page ->
            page.copy(elements = page.elements + duplicated)
        }
    }

    private fun duplicateElementTree(element: WebElement): WebElement {
        return element.copy(
            id = UUID.randomUUID().toString(),
            customId = null,
            styles = element.styles.copy(
                left = element.styles.left?.let {
                    val value = it.removeSuffix("px").toFloatOrNull() ?: 0f
                    "${value + 20}px"
                },
                top = element.styles.top?.let {
                    val value = it.removeSuffix("px").toFloatOrNull() ?: 0f
                    "${value + 20}px"
                }
            ),
            children = element.children.map { duplicateElementTree(it) }
        )
    }

    private fun findElementById(elements: List<WebElement>, elementId: String): WebElement? {
        for (element in elements) {
            if (element.id == elementId) return element
            findElementById(element.children, elementId)?.let { return it }
        }
        return null
    }

    fun moveElementUp(elementId: String) {
        saveSnapshot("Move element up")
        updateCurrentPage { page ->
            val elements = page.elements.toMutableList()
            val index = elements.indexOfFirst { it.id == elementId }
            if (index > 0) {
                val element = elements.removeAt(index)
                elements.add(index - 1, element)
            }
            page.copy(elements = elements)
        }
    }

    fun moveElementDown(elementId: String) {
        saveSnapshot("Move element down")
        updateCurrentPage { page ->
            val elements = page.elements.toMutableList()
            val index = elements.indexOfFirst { it.id == elementId }
            if (index >= 0 && index < elements.size - 1) {
                val element = elements.removeAt(index)
                elements.add(index + 1, element)
            }
            page.copy(elements = elements)
        }
    }

    fun updateElementStyle(elementId: String, styles: ElementStyles) {
        saveSnapshot("Update styles")
        updateElementById(elementId) { element ->
            val currentBreakpoint = _uiState.value.editorState.currentBreakpoint
            if (currentBreakpoint == Breakpoint.DESKTOP) {
                element.copy(styles = styles)
            } else {
                element.copy(
                    responsiveStyles = element.responsiveStyles + (currentBreakpoint to styles)
                )
            }
        }
    }

    fun toggleElementVisibility(elementId: String) {
        updateElementById(elementId) { it.copy(isVisible = !it.isVisible) }
    }

    fun toggleElementLock(elementId: String) {
        updateElementById(elementId) { it.copy(isLocked = !it.isLocked) }
    }

    fun renameElement(elementId: String, newName: String) {
        updateElementById(elementId) { it.copy(name = newName) }
    }

    fun reorderElement(elementId: String, newIndex: Int) {
        saveSnapshot("Reorder elements")
        updateCurrentPage { page ->
            val elements = page.elements.toMutableList()
            val currentIndex = elements.indexOfFirst { it.id == elementId }
            if (currentIndex != -1 && currentIndex != newIndex) {
                val element = elements.removeAt(currentIndex)
                val adjustedIndex = if (newIndex > currentIndex) newIndex - 1 else newIndex
                elements.add(adjustedIndex.coerceIn(0, elements.size), element)
            }
            page.copy(elements = elements)
        }
    }

    fun setBreakpoint(breakpoint: Breakpoint) {
        _uiState.update { state ->
            state.copy(editorState = state.editorState.copy(currentBreakpoint = breakpoint))
        }
    }

    fun setEditorMode(mode: EditorMode) {
        _uiState.update { state ->
            state.copy(editorState = state.editorState.copy(editorMode = mode))
        }
    }

    fun setZoom(zoom: Float) {
        _uiState.update { state ->
            state.copy(editorState = state.editorState.copy(zoom = zoom.coerceIn(0.25f, 4f)))
        }
    }

    fun setPan(offset: Offset) {
        _uiState.update { state ->
            state.copy(editorState = state.editorState.copy(panOffset = offset))
        }
    }

    fun startDragComponent(elementType: ElementType) {
        // Prepare for drag operation
    }

    fun updateHtml(html: String) {
        _uiState.update { it.copy(generatedHtml = html) }
        scheduleAutoSave()
    }

    fun updateCss(css: String) {
        _uiState.update { it.copy(generatedCss = css) }
        scheduleAutoSave()
    }

    fun updateJs(js: String) {
        _uiState.update { it.copy(generatedJs = js) }
        scheduleAutoSave()
    }

    fun undo() {
        if (undoStack.isEmpty()) return

        val currentPage = _uiState.value.currentPage ?: return
        val currentSnapshot = createSnapshot("Current state")

        // Push current state to redo
        if (redoStack.size >= MAX_HISTORY_SIZE) {
            redoStack.removeLast()
        }
        redoStack.addFirst(currentSnapshot)

        // Pop and apply undo state
        val snapshot = undoStack.removeFirst()
        applySnapshot(snapshot)
        updateHistoryState()
    }

    fun redo() {
        if (redoStack.isEmpty()) return

        val currentPage = _uiState.value.currentPage ?: return
        val currentSnapshot = createSnapshot("Current state")

        // Push current state to undo
        if (undoStack.size >= MAX_HISTORY_SIZE) {
            undoStack.removeLast()
        }
        undoStack.addFirst(currentSnapshot)

        // Pop and apply redo state
        val snapshot = redoStack.removeFirst()
        applySnapshot(snapshot)
        updateHistoryState()
    }

    private fun saveSnapshot(description: String) {
        val currentPage = _uiState.value.currentPage ?: return
        val snapshot = createSnapshot(description)

        // Clear redo stack on new action
        redoStack.clear()

        // Add to undo stack with size limit
        if (undoStack.size >= MAX_HISTORY_SIZE) {
            undoStack.removeLast()
        }
        undoStack.addFirst(snapshot)
        updateHistoryState()
    }

    private fun createSnapshot(description: String): PageSnapshot {
        val currentPage = _uiState.value.currentPage ?: return PageSnapshot("", "", description)
        return PageSnapshot(
            pageId = currentPage.id,
            elementsJson = gson.toJson(currentPage.elements),
            description = description
        )
    }

    private fun applySnapshot(snapshot: PageSnapshot) {
        try {
            val elements = gson.fromJson(snapshot.elementsJson, Array<WebElement>::class.java).toList()
            _uiState.update { state ->
                val currentPage = state.currentPage ?: return@update state
                if (currentPage.id != snapshot.pageId) return@update state
                state.copy(currentPage = currentPage.copy(elements = elements))
            }
            regenerateCode()
        } catch (e: Exception) {
            // Invalid snapshot, ignore
        }
    }

    private fun updateHistoryState() {
        _uiState.update { it.copy(canUndo = undoStack.isNotEmpty(), canRedo = redoStack.isNotEmpty()) }
    }

    fun saveProject() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val currentState = _uiState.value
                val project = currentState.project ?: return@launch
                val currentPage = currentState.currentPage ?: return@launch

                val updatedProject = project.copy(
                    updatedAt = System.currentTimeMillis(),
                    pages = project.pages.map { page ->
                        if (page.id == currentPage.id) currentPage else page
                    }
                )

                projectRepository.updateProject(updatedProject)

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        lastSaved = System.currentTimeMillis(),
                        project = updatedProject
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun togglePreview() {
        _uiState.update { state ->
            state.copy(
                editorState = state.editorState.copy(
                    editorMode = if (state.editorState.editorMode == EditorMode.PREVIEW) {
                        EditorMode.VISUAL
                    } else {
                        EditorMode.PREVIEW
                    }
                )
            )
        }
    }

    fun exportProject() {
        viewModelScope.launch {
            // Export implementation will be added
        }
    }

    private fun updateCurrentPage(transform: (Page) -> Page) {
        _uiState.update { state ->
            val currentPage = state.currentPage ?: return@update state
            val updatedPage = transform(currentPage)
            state.copy(currentPage = updatedPage)
        }
        regenerateCode()
        scheduleAutoSave()
    }

    private fun updateElementById(elementId: String, transform: (WebElement) -> WebElement) {
        updateCurrentPage { page ->
            page.copy(elements = updateElementInList(page.elements, elementId, transform))
        }
    }

    private fun updateElementByIdSilent(elementId: String, transform: (WebElement) -> WebElement) {
        _uiState.update { state ->
            val currentPage = state.currentPage ?: return@update state
            val updatedPage = currentPage.copy(
                elements = updateElementInList(currentPage.elements, elementId, transform)
            )
            state.copy(currentPage = updatedPage)
        }
        regenerateCode()
    }

    private fun updateElementInList(
        elements: List<WebElement>,
        elementId: String,
        transform: (WebElement) -> WebElement
    ): List<WebElement> {
        return elements.map { element ->
            when {
                element.id == elementId -> transform(element)
                element.children.isNotEmpty() -> element.copy(
                    children = updateElementInList(element.children, elementId, transform)
                )
                else -> element
            }
        }
    }

    private fun insertElementIntoParent(
        elements: List<WebElement>,
        newElement: WebElement,
        parentId: String,
        index: Int?
    ): List<WebElement> {
        return elements.map { element ->
            when {
                element.id == parentId -> {
                    val newChildren = if (index != null) {
                        element.children.toMutableList().apply { add(index, newElement) }
                    } else {
                        element.children + newElement
                    }
                    element.copy(children = newChildren)
                }
                element.children.isNotEmpty() -> element.copy(
                    children = insertElementIntoParent(element.children, newElement, parentId, index)
                )
                else -> element
            }
        }
    }

    private fun removeElementById(elements: List<WebElement>, elementId: String): List<WebElement> {
        return elements.filter { it.id != elementId }.map { element ->
            if (element.children.isNotEmpty()) {
                element.copy(children = removeElementById(element.children, elementId))
            } else {
                element
            }
        }
    }

    private fun regenerateCode() {
        val currentState = _uiState.value
        val page = currentState.currentPage ?: return
        val project = currentState.project ?: return

        viewModelScope.launch {
            val html = codeGenerator.generateHtml(page, project)
            val css = codeGenerator.generateCss(page, project)
            val js = codeGenerator.generateJs(page)

            _uiState.update {
                it.copy(generatedHtml = html, generatedCss = css, generatedJs = js)
            }
        }
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(5000)
            saveProject()
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel()
        undoStack.clear()
        redoStack.clear()
    }
}
