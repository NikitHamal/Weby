package com.officialcodingconvention.weby.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
    val thumbnailPath: String?,
    val settingsJson: String,
    val globalStylesJson: String,
    val cssVariablesJson: String,
    val customFontsJson: String
)

@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class PageEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val name: String,
    val slug: String,
    val isHomepage: Boolean,
    val title: String,
    val description: String,
    val elementsJson: String,
    val customCss: String,
    val customJs: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "assets",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class AssetEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val name: String,
    val type: String,
    val path: String,
    val size: Long,
    val mimeType: String,
    val width: Int?,
    val height: Int?,
    val createdAt: Long,
    val tagsJson: String
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val thumbnailPath: String?,
    val previewImagesJson: String,
    val tagsJson: String,
    val isBuiltIn: Boolean,
    val isUserCreated: Boolean,
    val createdAt: Long,
    val projectDataJson: String
)

@Entity(tableName = "saved_components")
data class SavedComponentEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val elementsJson: String,
    val thumbnailPath: String?,
    val tagsJson: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "css_classes")
data class CssClassEntity(
    @PrimaryKey
    val id: String,
    val projectId: String?,
    val name: String,
    val stylesJson: String,
    val responsiveStylesJson: String,
    val pseudoStatesJson: String,
    val description: String,
    val isGlobal: Boolean,
    val createdAt: Long
)

@Entity(
    tableName = "version_snapshots",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class VersionSnapshotEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val name: String,
    val description: String,
    val snapshotDataJson: String,
    val createdAt: Long
)
