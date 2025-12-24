package com.officialcodingconvention.weby.presentation.screens.project

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.officialcodingconvention.weby.R
import com.officialcodingconvention.weby.core.theme.WebyPrimary

enum class ProjectCreationType {
    SCRATCH, TEMPLATE, IMPORT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCreationScreen(
    onNavigateBack: () -> Unit,
    onProjectCreated: (String) -> Unit,
    viewModel: ProjectCreationViewModel = viewModel(factory = ProjectCreationViewModel.Factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.createdProjectId) {
        uiState.createdProjectId?.let { projectId ->
            onProjectCreated(projectId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = context.getString(R.string.project_create),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = context.getString(R.string.close)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Project name input
            OutlinedTextField(
                value = uiState.projectName,
                onValueChange = viewModel::updateProjectName,
                label = { Text(context.getString(R.string.project_name)) },
                placeholder = { Text(context.getString(R.string.project_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Outlined.DriveFileRenameOutline, contentDescription = null)
                }
            )

            // Project description input
            OutlinedTextField(
                value = uiState.projectDescription,
                onValueChange = viewModel::updateProjectDescription,
                label = { Text(context.getString(R.string.project_description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                leadingIcon = {
                    Icon(Icons.Outlined.Description, contentDescription = null)
                }
            )

            // Creation type selection
            Text(
                text = "How would you like to start?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CreationTypeCard(
                    title = context.getString(R.string.project_from_scratch),
                    description = "Start with a blank canvas",
                    icon = Icons.Outlined.AddCircleOutline,
                    isSelected = uiState.creationType == ProjectCreationType.SCRATCH,
                    onClick = { viewModel.updateCreationType(ProjectCreationType.SCRATCH) }
                )

                CreationTypeCard(
                    title = context.getString(R.string.project_from_template),
                    description = "Choose from pre-designed templates",
                    icon = Icons.Outlined.Dashboard,
                    isSelected = uiState.creationType == ProjectCreationType.TEMPLATE,
                    onClick = { viewModel.updateCreationType(ProjectCreationType.TEMPLATE) }
                )

                CreationTypeCard(
                    title = context.getString(R.string.project_import),
                    description = "Import existing HTML/CSS/JS files",
                    icon = Icons.Outlined.FileUpload,
                    isSelected = uiState.creationType == ProjectCreationType.IMPORT,
                    onClick = { viewModel.updateCreationType(ProjectCreationType.IMPORT) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Create button
            Button(
                onClick = viewModel::createProject,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState.projectName.isNotBlank() && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = WebyPrimary),
                shape = MaterialTheme.shapes.large
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = context.getString(R.string.project_create),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CreationTypeCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isSelected) {
        WebyPrimary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) WebyPrimary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) WebyPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = WebyPrimary
                )
            }
        }
    }
}
