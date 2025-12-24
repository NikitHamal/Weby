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
import com.officialcodingconvention.weby.presentation.screens.editor.components.CodeEditorPanel
import com.officialcodingconvention.weby.presentation.screens.editor.components.PreviewPanel

enum class EditorTab(val icon: ImageVector, val label: String) {
    CANVAS(Icons.Outlined.Crop169, "Canvas"),
    COMPONENTS(Icons.Outlined.Widgets, "Add"),
    LAYERS(Icons.Outlined.Layers, "Layers"),
    CODE(Icons.Outlined.Code, "Code"),
    PREVIEW(Icons.Outlined.Visibility, "Preview")
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
    var selectedTab by remember { mutableStateOf(EditorTab.CANVAS) }

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == EditorTab.CODE || selectedTab == EditorTab.PREVIEW) {
            viewModel.syncCodeToVisual()
        }
    }

    Scaffold(
        topBar = {
            EditorTopBar(
                projectName = uiState.project?.name ?: "",
                currentBreakpoint = uiState.editorState.currentBreakpoint,
                editorMode = uiState.editorState.editorMode,
                canUndo = uiState.editorState.history.undoStack.isNotEmpty(),
                canRedo = uiState.editorState.history.redoStack.isNotEmpty(),
                onNavigateBack = onNavigateBack,
                onBreakpointChange = viewModel::setBreakpoint,
                onEditorModeChange = viewModel::setEditorMode,
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
                onTabSelected = { selectedTab = it }
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
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProject(projectId) }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    when (selectedTab) {
                        EditorTab.CANVAS -> {
                            EditorCanvas(
                                elements = uiState.currentPageElements,
                                selectedElementId = uiState.editorState.selectedElementIds.firstOrNull(),
                                breakpoint = uiState.editorState.currentBreakpoint,
                                zoom = uiState.editorState.zoom,
                                panOffset = uiState.editorState.panOffset,
                                showGrid = uiState.editorState.showGrid,
                                onElementSelected = { id -> viewModel.selectElement(id) },
                                onElementMoved = { id, offset -> viewModel.moveElement(id, offset) },
                                onElementResized = { id, size -> viewModel.resizeElement(id, size) },
                                onPanChanged = viewModel::setPan,
                                onZoomChanged = viewModel::setZoom,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        EditorTab.COMPONENTS -> {
                            ComponentPanel(
                                onComponentSelected = { component ->
                                    viewModel.addElementFromComponent(component)
                                    selectedTab = EditorTab.CANVAS
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        EditorTab.LAYERS -> {
                            LayerPanel(
                                elements = uiState.currentPageElements,
                                selectedElementId = uiState.editorState.selectedElementIds.firstOrNull(),
                                onElementSelected = { id -> viewModel.selectElement(id) },
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
                        EditorTab.PREVIEW -> {
                            PreviewPanel(
                                htmlCode = uiState.generatedHtml,
                                cssCode = uiState.generatedCss,
                                jsCode = uiState.generatedJs,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
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
    onNavigateBack: () -> Unit,
    onBreakpointChange: (Breakpoint) -> Unit,
    onEditorModeChange: (EditorMode) -> Unit,
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
            Text(
                text = projectName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
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

            Box {
                AssistChip(
                    onClick = { showBreakpointMenu = true },
                    label = {
                        Text(
                            text = currentBreakpoint.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (currentBreakpoint) {
                                Breakpoint.MOBILE -> Icons.Outlined.PhoneAndroid
                                Breakpoint.TABLET -> Icons.Outlined.Tablet
                                else -> Icons.Outlined.DesktopWindows
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                DropdownMenu(
                    expanded = showBreakpointMenu,
                    onDismissRequest = { showBreakpointMenu = false }
                ) {
                    Breakpoint.entries.forEach { breakpoint ->
                        DropdownMenuItem(
                            text = { Text("${breakpoint.name} (${breakpoint.width}px)") },
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

            Box {
                IconButton(onClick = { showMoreMenu = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "More options"
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
    onTabSelected: (EditorTab) -> Unit
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
    }
}
