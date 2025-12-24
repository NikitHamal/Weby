package com.officialcodingconvention.weby.data.local.database.dao

import androidx.room.*
import com.officialcodingconvention.weby.data.local.database.entity.SavedComponentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponentDao {
    @Query("SELECT * FROM saved_components ORDER BY updatedAt DESC")
    fun getAllComponents(): Flow<List<SavedComponentEntity>>

    @Query("SELECT * FROM saved_components WHERE id = :id")
    suspend fun getComponentById(id: String): SavedComponentEntity?

    @Query("SELECT * FROM saved_components WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchComponents(query: String): Flow<List<SavedComponentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(component: SavedComponentEntity)

    @Update
    suspend fun updateComponent(component: SavedComponentEntity)

    @Delete
    suspend fun deleteComponent(component: SavedComponentEntity)

    @Query("DELETE FROM saved_components WHERE id = :id")
    suspend fun deleteComponentById(id: String)

    @Query("SELECT COUNT(*) FROM saved_components")
    suspend fun getComponentCount(): Int
}
