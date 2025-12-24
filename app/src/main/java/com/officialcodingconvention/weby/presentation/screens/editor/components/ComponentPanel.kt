package com.officialcodingconvention.weby.presentation.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.officialcodingconvention.weby.domain.model.ComponentDefinition
import com.officialcodingconvention.weby.domain.model.ComponentLibrary
import com.officialcodingconvention.weby.domain.model.ElementType

enum class ComponentCategory(val displayName: String, val icon: ImageVector) {
    LAYOUT("Layout", Icons.Default.GridView),
    BASIC("Basic", Icons.Default.Widgets),
    TYPOGRAPHY("Typography", Icons.Default.TextFields),
    FORMS("Forms", Icons.Default.EditNote),
    NAVIGATION("Navigation", Icons.Default.Menu),
    MEDIA("Media", Icons.Default.Image),
    ADVANCED("Advanced", Icons.Default.Extension)
}

@Composable
fun ComponentPanel(
    onComponentSelected: (ComponentDefinition) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(ComponentCategory.LAYOUT) }
    var searchQuery by remember { mutableStateOf("") }
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            viewMode = viewMode,
            onViewModeChange = { viewMode = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (searchQuery.isEmpty()) {
            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        val components = if (searchQuery.isNotEmpty()) {
            ComponentLibrary.allComponents.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        } else {
            getComponentsForCategory(selectedCategory)
        }

        when (viewMode) {
            ViewMode.GRID -> ComponentGrid(
                components = components,
                onComponentSelected = onComponentSelected,
                modifier = Modifier.fillMaxSize()
            )
            ViewMode.LIST -> ComponentList(
                components = components,
                onComponentSelected = onComponentSelected,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    viewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search components...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row {
                IconButton(
                    onClick = { onViewModeChange(ViewMode.GRID) }
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Grid view",
                        tint = if (viewMode == ViewMode.GRID)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { onViewModeChange(ViewMode.LIST) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewList,
                        contentDescription = "List view",
                        tint = if (viewMode == ViewMode.LIST)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: ComponentCategory,
    onCategorySelected: (ComponentCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = ComponentCategory.entries.indexOf(selectedCategory),
        modifier = modifier,
        edgePadding = 16.dp,
        divider = {}
    ) {
        ComponentCategory.entries.forEach { category ->
            Tab(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(category.displayName)
                    }
                }
            )
        }
    }
}

@Composable
private fun ComponentGrid(
    components: List<ComponentDefinition>,
    onComponentSelected: (ComponentDefinition) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(components, key = { it.type.name }) { component ->
            ComponentGridItem(
                component = component,
                onClick = { onComponentSelected(component) }
            )
        }
    }
}

@Composable
private fun ComponentGridItem(
    component: ComponentDefinition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getComponentIcon(component.type),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = component.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ComponentList(
    components: List<ComponentDefinition>,
    onComponentSelected: (ComponentDefinition) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(components, key = { it.type.name }) { component ->
            ComponentListItem(
                component = component,
                onClick = { onComponentSelected(component) }
            )
        }
    }
}

@Composable
private fun ComponentListItem(
    component: ComponentDefinition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getComponentIcon(component.type),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = component.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = component.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "<${component.tag}>",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private enum class ViewMode {
    GRID, LIST
}

private fun getComponentsForCategory(category: ComponentCategory): List<ComponentDefinition> {
    val typesByCategory = mapOf(
        ComponentCategory.LAYOUT to listOf(
            ElementType.DIV, ElementType.SECTION, ElementType.ARTICLE,
            ElementType.HEADER, ElementType.FOOTER, ElementType.MAIN,
            ElementType.ASIDE, ElementType.NAV
        ),
        ComponentCategory.BASIC to listOf(
            ElementType.BUTTON, ElementType.LINK_BUTTON, ElementType.ICON_BUTTON,
            ElementType.CARD, ElementType.BADGE, ElementType.CHIP,
            ElementType.DIVIDER, ElementType.SPACER
        ),
        ComponentCategory.TYPOGRAPHY to listOf(
            ElementType.H1, ElementType.H2, ElementType.H3, ElementType.H4,
            ElementType.H5, ElementType.H6, ElementType.P, ElementType.SPAN,
            ElementType.A, ElementType.BLOCKQUOTE, ElementType.CODE,
            ElementType.PRE, ElementType.UL, ElementType.OL, ElementType.LI
        ),
        ComponentCategory.FORMS to listOf(
            ElementType.FORM, ElementType.INPUT, ElementType.TEXTAREA,
            ElementType.SELECT, ElementType.CHECKBOX, ElementType.RADIO,
            ElementType.LABEL, ElementType.FIELDSET
        ),
        ComponentCategory.NAVIGATION to listOf(
            ElementType.NAV, ElementType.A, ElementType.BREADCRUMB,
            ElementType.TABS, ElementType.DROPDOWN
        ),
        ComponentCategory.MEDIA to listOf(
            ElementType.IMAGE, ElementType.VIDEO, ElementType.AUDIO,
            ElementType.IFRAME, ElementType.CANVAS, ElementType.SVG,
            ElementType.FIGURE
        ),
        ComponentCategory.ADVANCED to listOf(
            ElementType.TABLE, ElementType.MODAL, ElementType.ACCORDION,
            ElementType.CAROUSEL, ElementType.TOOLTIP, ElementType.PROGRESS,
            ElementType.SLIDER, ElementType.MAP
        )
    )

    val types = typesByCategory[category] ?: emptyList()
    return types.mapNotNull { type ->
        ComponentLibrary.getComponent(type)
    }
}

private fun getComponentIcon(type: ElementType): ImageVector {
    return when (type) {
        ElementType.DIV -> Icons.Default.Crop169
        ElementType.SECTION -> Icons.Default.ViewDay
        ElementType.ARTICLE -> Icons.Default.Article
        ElementType.HEADER -> Icons.Default.VerticalAlignTop
        ElementType.FOOTER -> Icons.Default.VerticalAlignBottom
        ElementType.MAIN -> Icons.Default.WebAsset
        ElementType.ASIDE -> Icons.Default.ViewSidebar
        ElementType.NAV -> Icons.Default.Menu

        ElementType.H1 -> Icons.Default.Title
        ElementType.H2 -> Icons.Default.TextFields
        ElementType.H3 -> Icons.Default.TextFields
        ElementType.H4 -> Icons.Default.TextFields
        ElementType.H5 -> Icons.Default.TextFields
        ElementType.H6 -> Icons.Default.TextFields
        ElementType.P -> Icons.Default.Notes
        ElementType.SPAN -> Icons.Default.TextFormat
        ElementType.A -> Icons.Default.Link
        ElementType.BLOCKQUOTE -> Icons.Default.FormatQuote
        ElementType.CODE -> Icons.Default.Code
        ElementType.PRE -> Icons.Default.Terminal
        ElementType.UL -> Icons.Default.FormatListBulleted
        ElementType.OL -> Icons.Default.FormatListNumbered
        ElementType.LI -> Icons.Default.FiberManualRecord

        ElementType.BUTTON -> Icons.Default.SmartButton
        ElementType.LINK_BUTTON -> Icons.Default.CallMade
        ElementType.ICON_BUTTON -> Icons.Default.TouchApp
        ElementType.CARD -> Icons.Default.CreditCard
        ElementType.BADGE -> Icons.Default.Label
        ElementType.CHIP -> Icons.Default.Memory
        ElementType.DIVIDER -> Icons.Default.HorizontalRule
        ElementType.SPACER -> Icons.Default.SpaceBar

        ElementType.FORM -> Icons.Default.Description
        ElementType.INPUT -> Icons.Default.Input
        ElementType.TEXTAREA -> Icons.Default.Notes
        ElementType.SELECT -> Icons.Default.ArrowDropDownCircle
        ElementType.CHECKBOX -> Icons.Default.CheckBox
        ElementType.RADIO -> Icons.Default.RadioButtonChecked
        ElementType.LABEL -> Icons.Default.Label
        ElementType.FIELDSET -> Icons.Default.Inventory2

        ElementType.IMAGE -> Icons.Default.Image
        ElementType.VIDEO -> Icons.Default.Videocam
        ElementType.AUDIO -> Icons.Default.Audiotrack
        ElementType.IFRAME -> Icons.Default.WebAsset
        ElementType.CANVAS -> Icons.Default.Brush
        ElementType.SVG -> Icons.Default.Architecture
        ElementType.FIGURE -> Icons.Default.PhotoSizeSelectActual

        ElementType.TABLE -> Icons.Default.TableChart
        ElementType.MODAL -> Icons.Default.OpenInNew
        ElementType.ACCORDION -> Icons.Default.ExpandMore
        ElementType.CAROUSEL -> Icons.Default.ViewCarousel
        ElementType.TOOLTIP -> Icons.Default.Info
        ElementType.PROGRESS -> Icons.Default.LinearScale
        ElementType.SLIDER -> Icons.Default.Tune
        ElementType.MAP -> Icons.Default.Map
        ElementType.BREADCRUMB -> Icons.Default.ChevronRight
        ElementType.TABS -> Icons.Default.Tab
        ElementType.DROPDOWN -> Icons.Default.ArrowDropDown

        else -> Icons.Default.Extension
    }
}
