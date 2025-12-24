package com.officialcodingconvention.weby.domain.repository

import com.officialcodingconvention.weby.data.repository.VersionSnapshot
import com.officialcodingconvention.weby.domain.model.Asset
import com.officialcodingconvention.weby.domain.model.Page
import com.officialcodingconvention.weby.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    fun getRecentProjects(limit: Int): Flow<List<Project>>
    suspend fun getProjectById(id: String): Project?
    fun getProjectByIdFlow(id: String): Flow<Project?>
    suspend fun createProject(project: Project): String
    suspend fun updateProject(project: Project)
    suspend fun deleteProject(projectId: String)
    suspend fun duplicateProject(projectId: String, newName: String): String

    fun getPagesByProjectId(projectId: String): Flow<List<Page>>
    suspend fun getPageById(id: String): Page?
    suspend fun createPage(projectId: String, page: Page)
    suspend fun updatePage(projectId: String, page: Page)
    suspend fun deletePage(pageId: String)
    suspend fun setHomepage(projectId: String, pageId: String)

    fun getAssetsByProjectId(projectId: String): Flow<List<Asset>>
    suspend fun addAsset(projectId: String, asset: Asset)
    suspend fun deleteAsset(assetId: String)

    suspend fun getProjectCount(): Int
    suspend fun getTotalStorageUsed(): Long
    suspend fun getProjectStorageUsed(projectId: String): Long

    fun getSnapshotsByProjectId(projectId: String): Flow<List<VersionSnapshot>>
    suspend fun createSnapshot(projectId: String, name: String, description: String)
    suspend fun restoreSnapshot(snapshotId: String): Boolean
    suspend fun deleteSnapshot(snapshotId: String)
}
