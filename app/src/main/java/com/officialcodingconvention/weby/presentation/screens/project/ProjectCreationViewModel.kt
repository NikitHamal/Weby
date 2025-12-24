package com.officialcodingconvention.weby.presentation.screens.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.officialcodingconvention.weby.WebyApplication
import com.officialcodingconvention.weby.data.repository.ProjectRepositoryImpl
import com.officialcodingconvention.weby.domain.model.Project
import com.officialcodingconvention.weby.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectCreationUiState(
    val projectName: String = "",
    val projectDescription: String = "",
    val creationType: ProjectCreationType = ProjectCreationType.SCRATCH,
    val isLoading: Boolean = false,
    val createdProjectId: String? = null,
    val error: String? = null
)

class ProjectCreationViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectCreationUiState())
    val uiState: StateFlow<ProjectCreationUiState> = _uiState.asStateFlow()

    fun updateProjectName(name: String) {
        _uiState.update { it.copy(projectName = name) }
    }

    fun updateProjectDescription(description: String) {
        _uiState.update { it.copy(projectDescription = description) }
    }

    fun updateCreationType(type: ProjectCreationType) {
        _uiState.update { it.copy(creationType = type) }
    }

    fun createProject() {
        val currentState = _uiState.value
        if (currentState.projectName.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val project = Project(
                    name = currentState.projectName.trim(),
                    description = currentState.projectDescription.trim()
                )

                val projectId = projectRepository.createProject(project)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        createdProjectId = projectId
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = WebyApplication.getInstance()
                val repository = ProjectRepositoryImpl(
                    projectDao = app.database.projectDao(),
                    fileSystemManager = app.fileSystemManager,
                    gson = Gson()
                )
                return ProjectCreationViewModel(repository) as T
            }
        }
    }
}
