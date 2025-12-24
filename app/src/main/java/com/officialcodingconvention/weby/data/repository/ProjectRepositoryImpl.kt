package com.officialcodingconvention.weby.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.officialcodingconvention.weby.data.local.database.dao.ProjectDao
import com.officialcodingconvention.weby.data.local.database.entity.*
import com.officialcodingconvention.weby.data.local.filesystem.FileSystemManager
import com.officialcodingconvention.weby.domain.model.*
import com.officialcodingconvention.weby.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao,
    private val fileSystemManager: FileSystemManager,
    private val gson: Gson = Gson()
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentProjects(limit: Int): Flow<List<Project>> {
        return projectDao.getRecentProjects(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProjectById(id: String): Project? {
        return projectDao.getProjectById(id)?.toDomain()
    }

    override fun getProjectByIdFlow(id: String): Flow<Project?> {
        return projectDao.getProjectByIdFlow(id).map { it?.toDomain() }
    }

    override suspend fun createProject(project: Project): String {
        val entity = project.toEntity()
        projectDao.insertProject(entity)
        project.pages.forEach { page ->
            projectDao.insertPage(page.toEntity(project.id))
        }
        fileSystemManager.getProjectDirectory(project.id)
        return project.id
    }

    override suspend fun updateProject(project: Project) {
        projectDao.updateProject(project.toEntity())
    }

    override suspend fun deleteProject(projectId: String) {
        projectDao.deleteProjectById(projectId)
        fileSystemManager.deleteProjectDirectory(projectId)
    }

    override suspend fun duplicateProject(projectId: String, newName: String): String {
        val originalProject = getProjectById(projectId) ?: throw IllegalArgumentException("Project not found")
        val newProject = originalProject.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = newName,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            pages = originalProject.pages.map { page ->
                page.copy(
                    id = java.util.UUID.randomUUID().toString(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }
        )
        return createProject(newProject)
    }

    override fun getPagesByProjectId(projectId: String): Flow<List<Page>> {
        return projectDao.getPagesByProjectId(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPageById(id: String): Page? {
        return projectDao.getPageById(id)?.toDomain()
    }

    override suspend fun createPage(projectId: String, page: Page) {
        projectDao.insertPage(page.toEntity(projectId))
    }

    override suspend fun updatePage(projectId: String, page: Page) {
        projectDao.updatePage(page.toEntity(projectId))
    }

    override suspend fun deletePage(pageId: String) {
        projectDao.deletePageById(pageId)
    }

    override suspend fun setHomepage(projectId: String, pageId: String) {
        projectDao.clearHomepage(projectId)
        projectDao.getPageById(pageId)?.let { page ->
            projectDao.updatePage(page.copy(isHomepage = true))
        }
    }

    override fun getAssetsByProjectId(projectId: String): Flow<List<Asset>> {
        return projectDao.getAssetsByProjectId(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addAsset(projectId: String, asset: Asset) {
        projectDao.insertAsset(asset.toEntity(projectId))
    }

    override suspend fun deleteAsset(assetId: String) {
        projectDao.getAssetById(assetId)?.let { asset ->
            fileSystemManager.deleteAsset(asset.path)
            projectDao.deleteAssetById(assetId)
        }
    }

    override suspend fun getProjectCount(): Int {
        return projectDao.getProjectCount()
    }

    override suspend fun getTotalStorageUsed(): Long {
        return projectDao.getTotalStorageUsed() ?: 0L
    }

    override suspend fun getProjectStorageUsed(projectId: String): Long {
        return projectDao.getProjectStorageUsed(projectId) ?: 0L
    }

    override fun getSnapshotsByProjectId(projectId: String): Flow<List<VersionSnapshot>> {
        return projectDao.getSnapshotsByProjectId(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createSnapshot(projectId: String, name: String, description: String) {
        val project = getProjectById(projectId) ?: return
        val snapshot = VersionSnapshotEntity(
            id = java.util.UUID.randomUUID().toString(),
            projectId = projectId,
            name = name,
            description = description,
            snapshotDataJson = gson.toJson(project),
            createdAt = System.currentTimeMillis()
        )
        projectDao.insertSnapshot(snapshot)
    }

    override suspend fun restoreSnapshot(snapshotId: String): Boolean {
        val snapshot = projectDao.getSnapshotById(snapshotId) ?: return false
        val project = gson.fromJson(snapshot.snapshotDataJson, Project::class.java)
        updateProject(project.copy(updatedAt = System.currentTimeMillis()))
        return true
    }

    override suspend fun deleteSnapshot(snapshotId: String) {
        projectDao.getSnapshotById(snapshotId)?.let {
            projectDao.deleteSnapshot(it)
        }
    }

    private fun ProjectEntity.toDomain(): Project {
        return Project(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt,
            thumbnailPath = thumbnailPath,
            settings = gson.fromJson(settingsJson, ProjectSettings::class.java),
            globalStyles = gson.fromJson(globalStylesJson, GlobalStyles::class.java),
            cssVariables = gson.fromJson(
                cssVariablesJson,
                object : TypeToken<Map<String, String>>() {}.type
            ),
            customFonts = gson.fromJson(
                customFontsJson,
                object : TypeToken<List<CustomFont>>() {}.type
            )
        )
    }

    private fun Project.toEntity(): ProjectEntity {
        return ProjectEntity(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt,
            thumbnailPath = thumbnailPath,
            settingsJson = gson.toJson(settings),
            globalStylesJson = gson.toJson(globalStyles),
            cssVariablesJson = gson.toJson(cssVariables),
            customFontsJson = gson.toJson(customFonts)
        )
    }

    private fun PageEntity.toDomain(): Page {
        return Page(
            id = id,
            name = name,
            slug = slug,
            isHomepage = isHomepage,
            title = title,
            description = description,
            elements = gson.fromJson(
                elementsJson,
                object : TypeToken<List<WebElement>>() {}.type
            ) ?: emptyList(),
            customCss = customCss,
            customJs = customJs,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Page.toEntity(projectId: String): PageEntity {
        return PageEntity(
            id = id,
            projectId = projectId,
            name = name,
            slug = slug,
            isHomepage = isHomepage,
            title = title,
            description = description,
            elementsJson = gson.toJson(elements),
            customCss = customCss,
            customJs = customJs,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun AssetEntity.toDomain(): Asset {
        return Asset(
            id = id,
            name = name,
            type = AssetType.valueOf(type),
            path = path,
            size = size,
            mimeType = mimeType,
            width = width,
            height = height,
            createdAt = createdAt,
            tags = gson.fromJson(tagsJson, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        )
    }

    private fun Asset.toEntity(projectId: String): AssetEntity {
        return AssetEntity(
            id = id,
            projectId = projectId,
            name = name,
            type = type.name,
            path = path,
            size = size,
            mimeType = mimeType,
            width = width,
            height = height,
            createdAt = createdAt,
            tagsJson = gson.toJson(tags)
        )
    }

    private fun VersionSnapshotEntity.toDomain(): VersionSnapshot {
        return VersionSnapshot(
            id = id,
            projectId = projectId,
            name = name,
            description = description,
            createdAt = createdAt
        )
    }
}

data class VersionSnapshot(
    val id: String,
    val projectId: String,
    val name: String,
    val description: String,
    val createdAt: Long
)
