package com.officialcodingconvention.weby.presentation.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.officialcodingconvention.weby.domain.model.ElementStyles
import com.officialcodingconvention.weby.domain.model.WebElement

enum class StyleSection(val title: String, val icon: ImageVector) {
    LAYOUT("Layout", Icons.Default.GridView),
    SPACING("Spacing", Icons.Default.SpaceBar),
    SIZE("Size", Icons.Default.AspectRatio),
    TYPOGRAPHY("Typography", Icons.Default.TextFields),
    BACKGROUND("Background", Icons.Default.FormatColorFill),
    BORDER("Border", Icons.Default.BorderStyle),
    EFFECTS("Effects", Icons.Default.AutoAwesome)
}

@Composable
fun StylePanel(
    selectedElement: WebElement?,
    onStyleChange: (ElementStyles) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSections by remember { mutableStateOf(setOf(StyleSection.LAYOUT, StyleSection.SIZE)) }

    if (selectedElement == null) {
        NoSelectionState(modifier = modifier.fillMaxSize())
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ElementHeader(element = selectedElement)
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        StyleSection.entries.forEach { section ->
            item(key = section.name) {
                StyleSectionCard(
                    section = section,
                    isExpanded = expandedSections.contains(section),
                    onToggleExpand = {
                        expandedSections = if (expandedSections.contains(section)) {
                            expandedSections - section
                        } else {
                            expandedSections + section
                        }
                    },
                    element = selectedElement,
                    onStyleChange = onStyleChange
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun NoSelectionState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.TouchApp,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Select an element",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Tap an element on the canvas to edit its styles",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ElementHeader(element: WebElement) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = element.customId ?: "<${element.tag}>",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (element.classes.isNotEmpty()) {
                    Text(
                        text = element.classes.joinToString(" ") { ".$it" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StyleSectionCard(
    section: StyleSection,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    element: WebElement,
    onStyleChange: (ElementStyles) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(20.dp)
                )
            }

            if (isExpanded) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (section) {
                        StyleSection.LAYOUT -> LayoutSection(element.styles, onStyleChange)
                        StyleSection.SPACING -> SpacingSection(element.styles, onStyleChange)
                        StyleSection.SIZE -> SizeSection(element.styles, onStyleChange)
                        StyleSection.TYPOGRAPHY -> TypographySection(element.styles, onStyleChange)
                        StyleSection.BACKGROUND -> BackgroundSection(element.styles, onStyleChange)
                        StyleSection.BORDER -> BorderSection(element.styles, onStyleChange)
                        StyleSection.EFFECTS -> EffectsSection(element.styles, onStyleChange)
                    }
                }
            }
        }
    }
}

@Composable
private fun LayoutSection(styles: ElementStyles, onStyleChange: (ElementStyles) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StyleDropdown(
            label = "Display",
            value = styles.display ?: "block",
            options = listOf("block", "flex", "grid", "inline", "inline-block", "none"),
            onValueChange = { onStyleChange(styles.copy(display = it)) }
        )

        if (styles.display == "flex") {
            StyleDropdown(
                label = "Direction",
                value = styles.flexDirection ?: "row",
                options = listOf("row", "column", "row-reverse", "column-reverse"),
                onValueChange = { onStyleChange(styles.copy(flexDirection = it)) }
            )

            StyleDropdown(
                label = "Justify",
                value = styles.justifyContent ?: "flex-start",
                options = listOf("flex-start", "center", "flex-end", "space-between", "space-around", "space-evenly"),
                onValueChange = { onStyleChange(styles.copy(justifyContent = it)) }
            )

            StyleDropdown(
                label = "Align",
                value = styles.alignItems ?: "stretch",
                options = listOf("stretch", "flex-start", "center", "flex-end", "baseline"),
                onValueChange = { onStyleChange(styles.copy(alignItems = it)) }
            )

            StyleTextField(
                label = "Gap",
                value = styles.gap ?: "",
                placeholder = "8px",
                onValueChange = { onStyleChange(styles.copy(gap = it.ifEmpty { null })) }
            )
        }

        if (styles.display == "grid") {
            StyleTextField(
                label = "Columns",
                value = styles.gridTemplateColumns ?: "",
                placeholder = "1fr 1fr",
                onValueChange = { onStyleChange(styles.copy(gridTemplateColumns = it.ifEmpty { null })) }
            )

            StyleTextField(
                label = "Rows",
                value = styles.gridTemplateRows ?: "",
                placeholder = "auto",
                onValueChange = { onStyleChange(styles.copy(gridTemplateRows = it.ifEmpty { null })) }
            )

            StyleTextField(
                label = "Gap",
                value = styles.gap ?: "",
                placeholder = "16px",
                onValueChange = { onStyleChange(styles.copy(gap = it.ifEmpty { null })) }
            )
        }

        StyleDropdown(
            label = "Position",
            value = styles.position ?: "static",
            options = listOf("static", "relative", "absolute", "fixed", "sticky"),
            onValueChange = { onStyleChange(styles.copy(position = it)) }
        )

        if (styles.position in listOf("absolute", "fixed", "sticky")) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyleTextField(
                    label = "Top",
                    value = styles.top ?: "",
                    placeholder = "0px",
                    modifier = Modifier.weight(1f),
                    onValueChange = { onStyleChange(styles.copy(top = it.ifEmpty { null })) }
                )
                StyleTextField(
                    label = "Right",
                    value = styles.right ?: "",
                    placeholder = "0px",
                    modifier = Modifier.weight(1f),
                    onValueChange = { onStyleChange(styles.copy(right = it.ifEmpty { null })) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyleTextField(
                    label = "Bottom",
                    value = styles.bottom ?: "",
                    placeholder = "0px",
                    modifier = Modifier.weight(1f),
                    onValueChange = { onStyleChange(styles.copy(bottom = it.ifEmpty { null })) }
                )
                StyleTextField(
                    label = "Left",
                    value = styles.left ?: "",
                    placeholder = "0px",
                    modifier = Modifier.weight(1f),
                    onValueChange = { onStyleChange(styles.copy(left = it.ifEmpty { null })) }
                )
            }

            StyleTextField(
                label = "Z-Index",
                value = styles.zIndex?.toString() ?: "",
                placeholder = "1",
                keyboardType = KeyboardType.Number,
                onValueChange = { onStyleChange(styles.copy(zIndex = it.toIntOrNull())) }
            )
        }
    }
}

@Composable
private fun SpacingSection(styles: ElementStyles, onStyleChange: (ElementStyles) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Margin",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )

        StyleTextField(
            label = "All sides",
            value = styles.margin ?: "",
            placeholder = "0px",
            onValueChange = { onStyleChange(styles.copy(margin = it.ifEmpty { null })) }
        )

        Text(
            text = "Padding",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )

        StyleTextField(
            label = "All sides",
            value = styles.padding ?: "",
            placeholder = "16px",
            onValueChange = { onStyleChange(styles.copy(padding = it.ifEmpty { null })) }
        )
    }
}

@Composable
private fun SizeSection(styles: ElementStyles, onStyleChange: (ElementStyles) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StyleTextField(
                label = "Width",
                value = styles.width ?: "",
                placeholder = "auto",
                modifier = Modifier.weight(1f),
                onValueChange = { onStyleChange(styles.copy(width = it.ifEmpty { null })) }
            )
            StyleTextField(
                label = "Height",
                value = styles.height ?: "",
                placeholder = "auto",
                modifier = Modifier.weight(1f),
                onValueChange = { onStyleChange(styles.copy(height = it.ifEmpty { null })) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StyleTextField(
                label = "Min W",
                value = styles.minWidth ?: "",
                placeholder = "0",
                modifier = Modifier.weight(1f),
                onValueChange = { onStyleChange(styles.copy(minWidth = it.ifEmpty { null })) }
            )
            StyleTextField(
                label = "Min H",
                value = styles.minHeight ?: "",
                placeholder = "0",
                modifier = Modifier.weight(1f),
                onValueChange = { onStyleChange(styles.copy(minHeight = it.ifEmpty { null })) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StyleTextField(
                label = "Max W",
                value = styles.maxWidth ?: "",
                placeholder = "none",
                modifier = Modifier.weight(1f),
                onValueChange = { onStyleChange(styles.copy(maxWidth = it.ifEmpty { null })) }
            )
            StyleTextField(
                label = "Max H",
                value = styles.maxHeight ?: "",
                placeholder = "none",
                modifier = Modifier.weight(1f),
                onValueChange = { onStyleChange(styles.copy(maxHeight = it.ifEmpty { null })) }
            )
        }
    }
}

@Composable
private fun TypographySection(styles: ElementStyles, onStyleChange: (ElementStyles) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StyleDropdown(
            label = "Font Family",
            value = styles.fontFamily ?: "inherit",
            options = listOf(
                "inherit", "system-ui", "Arial", "Helvetica", "Georgia",
                "Times New Roman", "Courier New", "Verdana", "Roboto", "Open Sans"
            ),
            onValueChange = { onStyleChange(styles.copy(fontFamily = it)) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StyleTextField(
                label = "Size",
                value = styles.fontSize ?: "",
                placeholder = "16px",
                modifier = Modifier.weight(1f),
                onValueChange = { onStyleChange(styles.copy(fontSize = it.ifEmpty { null })) }
            )

            StyleDropdown(
                label = "Weight",
                value = styles.fontWeight ?: "normal",
                options = listOf("100", "200", "300", "normal", "500", "600", "bold", "800", "900"),
                modifier = Modifier.weight(1f),
                onValueChange = { onStyleChange(styles.copy(fontWeight = it)) }
            )
        }

        StyleTextField(
            label = "Line Height",
            value = styles.lineHeight ?: "",
            placeholder = "1.5",
            onValueChange = { onStyleChange(styles.copy(lineHeight = it.ifEmpty { null })) }
        )

        StyleDropdown(
            label = "Text Align",
            value = styles.textAlign ?: "left",
            options = listOf("left", "center", "right", "justify"),
            onValueChange = { onStyleChange(styles.copy(textAlign = it)) }
        )

        ColorPicker(
            label = "Color",
            value = styles.color ?: "#000000",
            onValueChange = { onStyleChange(styles.copy(color = it)) }
        )
    }
}

@Composable
private fun BackgroundSection(styles: ElementStyles, onStyleChange: (ElementStyles) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ColorPicker(
            label = "Background Color",
            value = styles.backgroundColor ?: "#ffffff",
            onValueChange = { onStyleChange(styles.copy(backgroundColor = it)) }
        )
    }
}

@Composable
private fun BorderSection(styles: ElementStyles, onStyleChange: (ElementStyles) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StyleTextField(
            label = "Border",
            value = styles.border ?: "",
            placeholder = "1px solid #ccc",
            onValueChange = { onStyleChange(styles.copy(border = it.ifEmpty { null })) }
        )

        StyleTextField(
            label = "Border Radius",
            value = styles.borderRadius ?: "",
            placeholder = "8px",
            onValueChange = { onStyleChange(styles.copy(borderRadius = it.ifEmpty { null })) }
        )
    }
}

@Composable
private fun EffectsSection(styles: ElementStyles, onStyleChange: (ElementStyles) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StyleTextField(
            label = "Box Shadow",
            value = styles.boxShadow ?: "",
            placeholder = "0 4px 6px rgba(0,0,0,0.1)",
            onValueChange = { onStyleChange(styles.copy(boxShadow = it.ifEmpty { null })) }
        )

        StyleTextField(
            label = "Opacity",
            value = styles.opacity?.toString() ?: "",
            placeholder = "1",
            keyboardType = KeyboardType.Decimal,
            onValueChange = { onStyleChange(styles.copy(opacity = it.toFloatOrNull())) }
        )

        StyleTextField(
            label = "Transform",
            value = styles.transform ?: "",
            placeholder = "translateY(-2px)",
            onValueChange = { onStyleChange(styles.copy(transform = it.ifEmpty { null })) }
        )

        StyleTextField(
            label = "Transition",
            value = styles.transition ?: "",
            placeholder = "all 0.2s ease",
            onValueChange = { onStyleChange(styles.copy(transition = it.ifEmpty { null })) }
        )

        StyleDropdown(
            label = "Cursor",
            value = styles.cursor ?: "default",
            options = listOf("default", "pointer", "text", "move", "not-allowed", "grab", "grabbing"),
            onValueChange = { onStyleChange(styles.copy(cursor = it)) }
        )
    }
}

@Composable
private fun StyleTextField(
    label: String,
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyleDropdown(
    label: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(8.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodySmall) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(parseColorSafe(value))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .clickable { showColorPicker = true }
            )

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
        }

        if (showColorPicker) {
            AlertDialog(
                onDismissRequest = { showColorPicker = false },
                title = { Text("Select Color") },
                text = {
                    ColorPickerGrid(
                        selectedColor = value,
                        onColorSelected = {
                            onValueChange(it)
                            showColorPicker = false
                        }
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showColorPicker = false }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
private fun ColorPickerGrid(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "#000000", "#333333", "#666666", "#999999", "#CCCCCC", "#FFFFFF",
        "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3",
        "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
        "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#607D8B"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        colors.chunked(6).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(parseColorSafe(color))
                            .border(
                                width = if (color.equals(selectedColor, ignoreCase = true)) 2.dp else 1.dp,
                                color = if (color.equals(selectedColor, ignoreCase = true))
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

private fun parseColorSafe(value: String): Color {
    return try {
        if (value.startsWith("#") && value.length == 7) {
            val colorLong = value.removePrefix("#").toLong(16)
            Color(colorLong or 0xFF000000)
        } else {
            Color.Gray
        }
    } catch (e: Exception) {
        Color.Gray
    }
}
