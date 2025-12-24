package com.officialcodingconvention.weby.data.local.database.dao

import androidx.room.*
import com.officialcodingconvention.weby.data.local.database.entity.TemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY createdAt DESC")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE category = :category ORDER BY createdAt DESC")
    fun getTemplatesByCategory(category: String): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE isBuiltIn = 1 ORDER BY category, name")
    fun getBuiltInTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE isUserCreated = 1 ORDER BY createdAt DESC")
    fun getUserCreatedTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE id = :id")
    suspend fun getTemplateById(id: String): TemplateEntity?

    @Query("SELECT * FROM templates WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTemplates(query: String): Flow<List<TemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<TemplateEntity>)

    @Update
    suspend fun updateTemplate(template: TemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: TemplateEntity)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteTemplateById(id: String)

    @Query("DELETE FROM templates WHERE isUserCreated = 1")
    suspend fun deleteAllUserTemplates()

    @Query("SELECT COUNT(*) FROM templates WHERE category = :category")
    suspend fun getTemplateCountByCategory(category: String): Int
}
