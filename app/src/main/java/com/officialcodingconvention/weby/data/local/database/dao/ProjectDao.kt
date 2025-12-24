package com.officialcodingconvention.weby.data.local.database.dao

import androidx.room.*
import com.officialcodingconvention.weby.data.local.database.entity.ProjectEntity
import com.officialcodingconvention.weby.data.local.database.entity.PageEntity
import com.officialcodingconvention.weby.data.local.database.entity.AssetEntity
import com.officialcodingconvention.weby.data.local.database.entity.VersionSnapshotEntity
import com.officialcodingconvention.weby.data.local.database.entity.CssClassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    // Project operations
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentProjects(limit: Int): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectByIdFlow(id: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int

    @Query("SELECT SUM(size) FROM assets")
    suspend fun getTotalStorageUsed(): Long?

    // Page operations
    @Query("SELECT * FROM pages WHERE projectId = :projectId ORDER BY isHomepage DESC, name ASC")
    fun getPagesByProjectId(projectId: String): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE id = :id")
    suspend fun getPageById(id: String): PageEntity?

    @Query("SELECT * FROM pages WHERE projectId = :projectId AND isHomepage = 1 LIMIT 1")
    suspend fun getHomepage(projectId: String): PageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity)

    @Update
    suspend fun updatePage(page: PageEntity)

    @Delete
    suspend fun deletePage(page: PageEntity)

    @Query("DELETE FROM pages WHERE id = :id")
    suspend fun deletePageById(id: String)

    @Query("UPDATE pages SET isHomepage = 0 WHERE projectId = :projectId")
    suspend fun clearHomepage(projectId: String)

    // Asset operations
    @Query("SELECT * FROM assets WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getAssetsByProjectId(projectId: String): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: String): AssetEntity?

    @Query("SELECT * FROM assets WHERE projectId = :projectId AND type = :type")
    fun getAssetsByType(projectId: String, type: String): Flow<List<AssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity)

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Delete
    suspend fun deleteAsset(asset: AssetEntity)

    @Query("DELETE FROM assets WHERE id = :id")
    suspend fun deleteAssetById(id: String)

    @Query("SELECT SUM(size) FROM assets WHERE projectId = :projectId")
    suspend fun getProjectStorageUsed(projectId: String): Long?

    // Version snapshots
    @Query("SELECT * FROM version_snapshots WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getSnapshotsByProjectId(projectId: String): Flow<List<VersionSnapshotEntity>>

    @Query("SELECT * FROM version_snapshots WHERE id = :id")
    suspend fun getSnapshotById(id: String): VersionSnapshotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: VersionSnapshotEntity)

    @Delete
    suspend fun deleteSnapshot(snapshot: VersionSnapshotEntity)

    @Query("DELETE FROM version_snapshots WHERE projectId = :projectId")
    suspend fun deleteAllSnapshots(projectId: String)

    // CSS Classes
    @Query("SELECT * FROM css_classes WHERE projectId = :projectId OR isGlobal = 1 ORDER BY name ASC")
    fun getCssClassesByProjectId(projectId: String): Flow<List<CssClassEntity>>

    @Query("SELECT * FROM css_classes WHERE isGlobal = 1 ORDER BY name ASC")
    fun getGlobalCssClasses(): Flow<List<CssClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCssClass(cssClass: CssClassEntity)

    @Update
    suspend fun updateCssClass(cssClass: CssClassEntity)

    @Delete
    suspend fun deleteCssClass(cssClass: CssClassEntity)

    // Transaction for complete project save
    @Transaction
    suspend fun saveProjectWithPages(project: ProjectEntity, pages: List<PageEntity>) {
        insertProject(project)
        pages.forEach { insertPage(it) }
    }
}
