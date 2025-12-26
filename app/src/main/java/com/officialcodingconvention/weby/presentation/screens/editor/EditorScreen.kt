package com.officialcodingconvention.weby.presentation.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.officialcodingconvention.weby.core.theme.WebyPrimary
import com.officialcodingconvention.weby.domain.model.Breakpoint
import com.officialcodingconvention.weby.domain.model.EditorMode
import com.officialcodingconvention.weby.presentation.screens.editor.components.EditorCanvas
import com.officialcodingconvention.weby.presentation.screens.editor.components.ComponentPanel
import com.officialcodingconvention.weby.presentation.screens.editor.components.LayerPanel
import com.officialcodingconvention.weby.presentation.screens.editor.components.StylePanel
import com.officialcodingconvention.weby.presentation.screens.editor.components.CodeEditorPanel

// Simplified to 4 essential tabs
enum class EditorTab(val icon: ImageVector, val label: String) {
    DESIGN(Icons.Outlined.Edit, "Design"),
    ADD(Icons.Outlined.Add, "Add"),
    LAYERS(Icons.Outlined.Layers, "Layers"),
    CODE(Icons.Outlined.Code, "Code")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    projectId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = viewModel(
        factory = EditorViewModel.Factory,
        key = projectId
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(EditorTab.DESIGN) }
    var showStyleSheet by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    // When an element is selected in Design tab, auto-show style sheet
    val selectedElement = uiState.currentPageElements.find {
        it.id in uiState.editorState.selectedElementIds
    }

    Scaffold(
        topBar = {
            EditorTopBar(
                projectName = uiState.project?.name ?: "",
                currentBreakpoint = uiState.editorState.currentBreakpoint,
                editorMode = uiState.editorState.editorMode,
                canUndo = uiState.canUndo,
                canRedo = uiState.canRedo,
                isSaving = uiState.isSaving,
                onNavigateBack = onNavigateBack,
                onBreakpointChange = viewModel::setBreakpoint,
                onUndo = viewModel::undo,
                onRedo = viewModel::redo,
                onSave = viewModel::saveProject,
                onPreview = viewModel::togglePreview,
                onExport = viewModel::exportProject
            )
        },
        bottomBar = {
            EditorBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                hasSelection = selectedElement != null,
                onStylesClick = { showStyleSheet = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.error != null -> ErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadProject(projectId) }
                )
                else -> EditorContent(
                    selectedTab = selectedTab,
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }

    // Style bottom sheet
    if (showStyleSheet && selectedElement != null) {
        ModalBottomSheet(
            onDismissRequest = { showStyleSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            StylePanel(
                selectedElement = selectedElement,
                onStyleChange = { styles ->
                    viewModel.updateElementStyle(selectedElement.id, styles)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun BoxScope.LoadingState() {
    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
}

@Composable
private fun BoxScope.ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EditorContent(
    selectedTab: EditorTab,
    uiState: EditorUiState,
    viewModel: EditorViewModel
) {
    when (selectedTab) {
        EditorTab.DESIGN -> {
            EditorCanvas(
                elements = uiState.currentPageElements,
                selectedElementId = uiState.editorState.selectedElementIds.firstOrNull(),
                breakpoint = uiState.editorState.currentBreakpoint,
                zoom = uiState.editorState.zoom,
                panOffset = uiState.editorState.panOffset,
                showGrid = uiState.editorState.showGrid,
                onElementSelected = viewModel::selectElement,
                onElementMoved = viewModel::moveElement,
                onElementResized = viewModel::resizeElement,
                onPanChanged = viewModel::setPan,
                onZoomChanged = viewModel::setZoom,
                modifier = Modifier.fillMaxSize()
            )
        }
        EditorTab.ADD -> {
            ComponentPanel(
                onComponentSelected = viewModel::addElementFromComponent,
                modifier = Modifier.fillMaxSize()
            )
        }
        EditorTab.LAYERS -> {
            LayerPanel(
                elements = uiState.currentPageElements,
                selectedElementId = uiState.editorState.selectedElementIds.firstOrNull(),
                onElementSelected = viewModel::selectElement,
                onElementVisibilityToggle = viewModel::toggleElementVisibility,
                onElementLockToggle = viewModel::toggleElementLock,
                onElementDelete = viewModel::deleteElement,
                onElementDuplicate = viewModel::duplicateElement,
                onMoveUp = viewModel::moveElementUp,
                onMoveDown = viewModel::moveElementDown,
                modifier = Modifier.fillMaxSize()
            )
        }
        EditorTab.CODE -> {
            CodeEditorPanel(
                htmlCode = uiState.generatedHtml,
                cssCode = uiState.generatedCss,
                jsCode = uiState.generatedJs,
                onHtmlChange = viewModel::updateHtml,
                onCssChange = viewModel::updateCss,
                onJsChange = viewModel::updateJs,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTopBar(
    projectName: String,
    currentBreakpoint: Breakpoint,
    editorMode: EditorMode,
    canUndo: Boolean,
    canRedo: Boolean,
    isSaving: Boolean,
    onNavigateBack: () -> Unit,
    onBreakpointChange: (Breakpoint) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit,
    onPreview: () -> Unit,
    onExport: () -> Unit
) {
    var showBreakpointMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = projectName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            // Undo/Redo
            IconButton(onClick = onUndo, enabled = canUndo) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
            IconButton(onClick = onRedo, enabled = canRedo) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            // Breakpoint selector
            Box {
                IconButton(onClick = { showBreakpointMenu = true }) {
                    Icon(
                        imageVector = when (currentBreakpoint) {
                            Breakpoint.MOBILE -> Icons.Outlined.PhoneAndroid
                            Breakpoint.TABLET -> Icons.Outlined.Tablet
                            else -> Icons.Outlined.DesktopWindows
                        },
                        contentDescription = "Device"
                    )
                }

                DropdownMenu(
                    expanded = showBreakpointMenu,
                    onDismissRequest = { showBreakpointMenu = false }
                ) {
                    Breakpoint.entries.forEach { breakpoint ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${breakpoint.name.lowercase().replaceFirstChar { it.uppercase() }} (${breakpoint.width}px)"
                                )
                            },
                            onClick = {
                                onBreakpointChange(breakpoint)
                                showBreakpointMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (breakpoint) {
                                        Breakpoint.MOBILE -> Icons.Outlined.PhoneAndroid
                                        Breakpoint.TABLET -> Icons.Outlined.Tablet
                                        else -> Icons.Outlined.DesktopWindows
                                    },
                                    contentDescription = null
                                )
                            },
                            trailingIcon = if (breakpoint == currentBreakpoint) {
                                {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                        tint = WebyPrimary
                                    )
                                }
                            } else null
                        )
                    }
                }
            }

            // More menu
            Box {
                IconButton(onClick = { showMoreMenu = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "More"
                    )
                }

                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Save") },
                        onClick = {
                            onSave()
                            showMoreMenu = false
                        },
                        leadingIcon = { Icon(Icons.Outlined.Save, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Preview") },
                        onClick = {
                            onPreview()
                            showMoreMenu = false
                        },
                        leadingIcon = { Icon(Icons.Outlined.Visibility, null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Export") },
                        onClick = {
                            onExport()
                            showMoreMenu = false
                        },
                        leadingIcon = { Icon(Icons.Outlined.FileDownload, null) }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun EditorBottomBar(
    selectedTab: EditorTab,
    onTabSelected: (EditorTab) -> Unit,
    hasSelection: Boolean,
    onStylesClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        EditorTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WebyPrimary,
                    selectedTextColor = WebyPrimary,
                    indicatorColor = WebyPrimary.copy(alpha = 0.12f)
                )
            )
        }

        // Styles button - only enabled when element is selected
        NavigationBarItem(
            selected = false,
            onClick = onStylesClick,
            enabled = hasSelection,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = "Styles",
                    tint = if (hasSelection) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            },
            label = {
                Text(
                    text = "Styles",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hasSelection) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = WebyPrimary,
                selectedTextColor = WebyPrimary,
                indicatorColor = WebyPrimary.copy(alpha = 0.12f)
            )
        )
    }
}
