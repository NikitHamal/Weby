package com.officialcodingconvention.weby.presentation.screens.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.officialcodingconvention.weby.domain.model.ElementType
import com.officialcodingconvention.weby.domain.model.WebElement

@Composable
fun LayerPanel(
    elements: List<WebElement>,
    selectedElementId: String?,
    onElementSelected: (String?) -> Unit,
    onElementVisibilityToggle: (String) -> Unit,
    onElementLockToggle: (String) -> Unit,
    onElementDelete: (String) -> Unit,
    onElementDuplicate: (String) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedElements by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = modifier.fillMaxSize()) {
        LayerPanelHeader(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalDivider()

        if (elements.isEmpty()) {
            EmptyLayerState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            )
        } else {
            val filteredElements = if (searchQuery.isNotEmpty()) {
                filterElements(elements, searchQuery)
            } else {
                elements
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                filteredElements.forEachIndexed { index, element ->
                    item(key = element.id) {
                        LayerItem(
                            element = element,
                            depth = 0,
                            isSelected = element.id == selectedElementId,
                            isExpanded = expandedElements.contains(element.id),
                            isFirst = index == 0,
                            isLast = index == filteredElements.lastIndex,
                            onSelect = { onElementSelected(element.id) },
                            onToggleExpand = {
                                expandedElements = if (expandedElements.contains(element.id)) {
                                    expandedElements - element.id
                                } else {
                                    expandedElements + element.id
                                }
                            },
                            onVisibilityToggle = { onElementVisibilityToggle(element.id) },
                            onLockToggle = { onElementLockToggle(element.id) },
                            onDelete = { onElementDelete(element.id) },
                            onDuplicate = { onElementDuplicate(element.id) },
                            onMoveUp = { onMoveUp(element.id) },
                            onMoveDown = { onMoveDown(element.id) }
                        )

                        if (expandedElements.contains(element.id) && element.children.isNotEmpty()) {
                            ChildLayers(
                                children = element.children,
                                depth = 1,
                                selectedElementId = selectedElementId,
                                expandedElements = expandedElements,
                                onExpandedChange = { expandedElements = it },
                                onElementSelected = onElementSelected,
                                onElementVisibilityToggle = onElementVisibilityToggle,
                                onElementLockToggle = onElementLockToggle,
                                onElementDelete = onElementDelete,
                                onElementDuplicate = onElementDuplicate,
                                onMoveUp = onMoveUp,
                                onMoveDown = onMoveDown
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerPanelHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search layers...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun EmptyLayerState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Layers,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No elements yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Add components from the library to get started",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChildLayers(
    children: List<WebElement>,
    depth: Int,
    selectedElementId: String?,
    expandedElements: Set<String>,
    onExpandedChange: (Set<String>) -> Unit,
    onElementSelected: (String?) -> Unit,
    onElementVisibilityToggle: (String) -> Unit,
    onElementLockToggle: (String) -> Unit,
    onElementDelete: (String) -> Unit,
    onElementDuplicate: (String) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit
) {
    children.forEachIndexed { index, child ->
        LayerItem(
            element = child,
            depth = depth,
            isSelected = child.id == selectedElementId,
            isExpanded = expandedElements.contains(child.id),
            isFirst = index == 0,
            isLast = index == children.lastIndex,
            onSelect = { onElementSelected(child.id) },
            onToggleExpand = {
                onExpandedChange(
                    if (expandedElements.contains(child.id)) {
                        expandedElements - child.id
                    } else {
                        expandedElements + child.id
                    }
                )
            },
            onVisibilityToggle = { onElementVisibilityToggle(child.id) },
            onLockToggle = { onElementLockToggle(child.id) },
            onDelete = { onElementDelete(child.id) },
            onDuplicate = { onElementDuplicate(child.id) },
            onMoveUp = { onMoveUp(child.id) },
            onMoveDown = { onMoveDown(child.id) }
        )

        if (expandedElements.contains(child.id) && child.children.isNotEmpty()) {
            ChildLayers(
                children = child.children,
                depth = depth + 1,
                selectedElementId = selectedElementId,
                expandedElements = expandedElements,
                onExpandedChange = onExpandedChange,
                onElementSelected = onElementSelected,
                onElementVisibilityToggle = onElementVisibilityToggle,
                onElementLockToggle = onElementLockToggle,
                onElementDelete = onElementDelete,
                onElementDuplicate = onElementDuplicate,
                onMoveUp = onMoveUp,
                onMoveDown = onMoveDown
            )
        }
    }
}

@Composable
private fun LayerItem(
    element: WebElement,
    depth: Int,
    isSelected: Boolean,
    isExpanded: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onSelect: () -> Unit,
    onToggleExpand: () -> Unit,
    onVisibilityToggle: () -> Unit,
    onLockToggle: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    var showContextMenu by remember { mutableStateOf(false) }

    val expandRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "expand_rotation"
    )

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .background(backgroundColor)
            .padding(start = (16 + depth * 16).dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (element.children.isNotEmpty()) {
            IconButton(
                onClick = onToggleExpand,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(expandRotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(getElementTypeColor(element.type).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getElementTypeIcon(element.type),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = getElementTypeColor(element.type)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = element.customId ?: element.type.name.lowercase(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (element.classes.isNotEmpty()) {
                Text(
                    text = element.classes.joinToString(" ") { ".$it" },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            IconButton(
                onClick = onVisibilityToggle,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (element.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (element.isVisible) "Hide" else "Show",
                    modifier = Modifier.size(16.dp),
                    tint = if (element.isVisible)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.outline
                )
            }

            IconButton(
                onClick = onLockToggle,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (element.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = if (element.isLocked) "Unlock" else "Lock",
                    modifier = Modifier.size(16.dp),
                    tint = if (element.isLocked)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            }

            Box {
                IconButton(
                    onClick = { showContextMenu = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showContextMenu,
                    onDismissRequest = { showContextMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Duplicate") },
                        leadingIcon = {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                        },
                        onClick = {
                            showContextMenu = false
                            onDuplicate()
                        }
                    )

                    if (!isFirst) {
                        DropdownMenuItem(
                            text = { Text("Move Up") },
                            leadingIcon = {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null)
                            },
                            onClick = {
                                showContextMenu = false
                                onMoveUp()
                            }
                        )
                    }

                    if (!isLast) {
                        DropdownMenuItem(
                            text = { Text("Move Down") },
                            leadingIcon = {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null)
                            },
                            onClick = {
                                showContextMenu = false
                                onMoveDown()
                            }
                        )
                    }

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            showContextMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

private fun filterElements(elements: List<WebElement>, query: String): List<WebElement> {
    return elements.filter { element ->
        matchesQuery(element, query)
    }
}

private fun matchesQuery(element: WebElement, query: String): Boolean {
    val lowerQuery = query.lowercase()

    if (element.type.name.lowercase().contains(lowerQuery)) return true
    if (element.customId?.lowercase()?.contains(lowerQuery) == true) return true
    if (element.classes.any { it.lowercase().contains(lowerQuery) }) return true

    return element.children.any { matchesQuery(it, lowerQuery) }
}

private fun getElementTypeIcon(type: ElementType): ImageVector {
    return when (type) {
        ElementType.DIV -> Icons.Default.Crop169
        ElementType.SECTION -> Icons.Default.ViewDay
        ElementType.ARTICLE -> Icons.Default.Article
        ElementType.HEADER -> Icons.Default.VerticalAlignTop
        ElementType.FOOTER -> Icons.Default.VerticalAlignBottom
        ElementType.MAIN -> Icons.Default.WebAsset
        ElementType.ASIDE -> Icons.Default.ViewSidebar
        ElementType.NAV -> Icons.Default.Menu
        ElementType.H1, ElementType.H2, ElementType.H3, ElementType.H4,
        ElementType.H5, ElementType.H6 -> Icons.Default.Title
        ElementType.P -> Icons.Default.Notes
        ElementType.SPAN -> Icons.Default.TextFormat
        ElementType.A -> Icons.Default.Link
        ElementType.BUTTON -> Icons.Default.SmartButton
        ElementType.IMAGE -> Icons.Default.Image
        ElementType.VIDEO -> Icons.Default.Videocam
        ElementType.FORM -> Icons.Default.Description
        ElementType.INPUT -> Icons.Default.Input
        ElementType.TEXTAREA -> Icons.Default.Notes
        ElementType.SELECT -> Icons.Default.ArrowDropDownCircle
        ElementType.UL, ElementType.OL -> Icons.Default.FormatListBulleted
        ElementType.LI -> Icons.Default.FiberManualRecord
        ElementType.TABLE -> Icons.Default.TableChart
        else -> Icons.Default.Extension
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
