package com.officialcodingconvention.weby.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.officialcodingconvention.weby.WebyApplication
import com.officialcodingconvention.weby.data.local.datastore.PreferencesManager
import com.officialcodingconvention.weby.data.local.filesystem.StorageStats
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as WebyApplication
    val preferencesManager = application.preferencesManager
    val fileSystemManager = application.fileSystemManager
    val scope = rememberCoroutineScope()

    var isDarkTheme by remember { mutableStateOf(false) }
    var autoSaveEnabled by remember { mutableStateOf(true) }
    var autoSaveInterval by remember { mutableStateOf(30) }
    var showGrid by remember { mutableStateOf(true) }
    var snapToGrid by remember { mutableStateOf(true) }
    var gridSize by remember { mutableStateOf(8) }
    var codeEditorFontSize by remember { mutableStateOf(14) }
    var showLineNumbers by remember { mutableStateOf(true) }
    var wordWrap by remember { mutableStateOf(false) }
    var storageStats by remember { mutableStateOf<StorageStats?>(null) }

    LaunchedEffect(Unit) {
        isDarkTheme = preferencesManager.isDarkTheme.first()
        autoSaveEnabled = preferencesManager.autoSaveEnabled.first()
        autoSaveInterval = preferencesManager.autoSaveInterval.first()
        showGrid = preferencesManager.showGrid.first()
        snapToGrid = preferencesManager.snapToGrid.first()
        gridSize = preferencesManager.gridSize.first()
        codeEditorFontSize = preferencesManager.codeEditorFontSize.first()
        showLineNumbers = preferencesManager.showLineNumbers.first()
        wordWrap = preferencesManager.wordWrap.first()
        storageStats = fileSystemManager.getStorageStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSection(title = "Appearance") {
                    SwitchSettingItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Theme",
                        subtitle = "Use dark color scheme",
                        checked = isDarkTheme,
                        onCheckedChange = {
                            isDarkTheme = it
                            scope.launch { preferencesManager.setDarkTheme(it) }
                        }
                    )
                }
            }

            item {
                SettingsSection(title = "Editor") {
                    SwitchSettingItem(
                        icon = Icons.Default.Save,
                        title = "Auto-save",
                        subtitle = "Automatically save changes",
                        checked = autoSaveEnabled,
                        onCheckedChange = {
                            autoSaveEnabled = it
                            scope.launch { preferencesManager.setAutoSaveEnabled(it) }
                        }
                    )

                    if (autoSaveEnabled) {
                        SliderSettingItem(
                            icon = Icons.Default.Timer,
                            title = "Auto-save Interval",
                            subtitle = "$autoSaveInterval seconds",
                            value = autoSaveInterval.toFloat(),
                            valueRange = 10f..120f,
                            steps = 10,
                            onValueChange = {
                                autoSaveInterval = it.toInt()
                                scope.launch { preferencesManager.setAutoSaveInterval(it.toInt()) }
                            }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SwitchSettingItem(
                        icon = Icons.Default.GridOn,
                        title = "Show Grid",
                        subtitle = "Display grid on canvas",
                        checked = showGrid,
                        onCheckedChange = {
                            showGrid = it
                            scope.launch { preferencesManager.setShowGrid(it) }
                        }
                    )

                    SwitchSettingItem(
                        icon = Icons.Default.GridGoldenratio,
                        title = "Snap to Grid",
                        subtitle = "Align elements to grid",
                        checked = snapToGrid,
                        onCheckedChange = {
                            snapToGrid = it
                            scope.launch { preferencesManager.setSnapToGrid(it) }
                        }
                    )

                    SliderSettingItem(
                        icon = Icons.Default.SpaceBar,
                        title = "Grid Size",
                        subtitle = "$gridSize pixels",
                        value = gridSize.toFloat(),
                        valueRange = 4f..32f,
                        steps = 6,
                        onValueChange = {
                            gridSize = it.toInt()
                            scope.launch { preferencesManager.setGridSize(it.toInt()) }
                        }
                    )
                }
            }

            item {
                SettingsSection(title = "Code Editor") {
                    SliderSettingItem(
                        icon = Icons.Default.FormatSize,
                        title = "Font Size",
                        subtitle = "$codeEditorFontSize px",
                        value = codeEditorFontSize.toFloat(),
                        valueRange = 10f..24f,
                        steps = 13,
                        onValueChange = {
                            codeEditorFontSize = it.toInt()
                            scope.launch { preferencesManager.setCodeEditorFontSize(it.toInt()) }
                        }
                    )

                    SwitchSettingItem(
                        icon = Icons.Default.FormatListNumbered,
                        title = "Line Numbers",
                        subtitle = "Show line numbers in code editor",
                        checked = showLineNumbers,
                        onCheckedChange = {
                            showLineNumbers = it
                            scope.launch { preferencesManager.setShowLineNumbers(it) }
                        }
                    )

                    SwitchSettingItem(
                        icon = Icons.Default.WrapText,
                        title = "Word Wrap",
                        subtitle = "Wrap long lines",
                        checked = wordWrap,
                        onCheckedChange = {
                            wordWrap = it
                            scope.launch { preferencesManager.setWordWrap(it) }
                        }
                    )
                }
            }

            item {
                SettingsSection(title = "Storage") {
                    storageStats?.let { stats ->
                        StorageInfoItem(
                            icon = Icons.Default.Folder,
                            title = "Projects",
                            size = formatBytes(stats.projectsSize)
                        )

                        StorageInfoItem(
                            icon = Icons.Default.Cached,
                            title = "Cache",
                            size = formatBytes(stats.cacheSize)
                        )

                        StorageInfoItem(
                            icon = Icons.Default.Backup,
                            title = "Backups",
                            size = formatBytes(stats.backupsSize)
                        )

                        StorageInfoItem(
                            icon = Icons.Default.Download,
                            title = "Exports",
                            size = formatBytes(stats.exportsSize)
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        StorageInfoItem(
                            icon = Icons.Default.Storage,
                            title = "Total",
                            size = formatBytes(stats.totalSize),
                            isTotal = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    fileSystemManager.clearCache()
                                    storageStats = fileSystemManager.getStorageStats()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Cache")
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    fileSystemManager.clearExports()
                                    storageStats = fileSystemManager.getStorageStats()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Exports")
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "About") {
                    InfoSettingItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        value = "1.0.0"
                    )

                    InfoSettingItem(
                        icon = Icons.Default.Code,
                        title = "Build",
                        value = "Production"
                    )

                    ClickableSettingItem(
                        icon = Icons.Default.Policy,
                        title = "Privacy Policy",
                        onClick = { }
                    )

                    ClickableSettingItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = { }
                    )

                    ClickableSettingItem(
                        icon = Icons.Default.BugReport,
                        title = "Report a Bug",
                        onClick = { }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun SwitchSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SliderSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.padding(start = 36.dp)
        )
    }
}

@Composable
private fun StorageInfoItem(
    icon: ImageVector,
    title: String,
    size: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = size,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Medium else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoSettingItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ClickableSettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
