package com.officialcodingconvention.weby.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.officialcodingconvention.weby.WebyApplication
import com.officialcodingconvention.weby.data.local.filesystem.FileSystemManager
import com.officialcodingconvention.weby.data.repository.ProjectRepositoryImpl
import com.officialcodingconvention.weby.domain.model.Project
import com.officialcodingconvention.weby.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val recentProjects: List<Project> = emptyList(),
    val storageUsed: Long = 0L,
    val error: String? = null
)

class HomeViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                projectRepository.getAllProjects(),
                projectRepository.getRecentProjects(5)
            ) { allProjects, recentProjects ->
                Pair(allProjects, recentProjects)
            }.collect { (allProjects, recentProjects) ->
                val storageUsed = projectRepository.getTotalStorageUsed()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        projects = allProjects,
                        recentProjects = recentProjects,
                        storageUsed = storageUsed
                    )
                }
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                projectRepository.deleteProject(projectId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun duplicateProject(projectId: String) {
        viewModelScope.launch {
            try {
                val originalProject = projectRepository.getProjectById(projectId)
                originalProject?.let { project ->
                    val newName = "${project.name} (Copy)"
                    projectRepository.duplicateProject(projectId, newName)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
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
                return HomeViewModel(repository) as T
            }
        }
    }
}
