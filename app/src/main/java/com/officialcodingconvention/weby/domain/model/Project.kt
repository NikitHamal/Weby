package com.officialcodingconvention.weby.domain.model

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailPath: String? = null,
    val settings: ProjectSettings = ProjectSettings(),
    val pages: List<Page> = listOf(Page.createDefault()),
    val globalStyles: GlobalStyles = GlobalStyles(),
    val assets: List<Asset> = emptyList(),
    val cssVariables: Map<String, String> = emptyMap(),
    val customFonts: List<CustomFont> = emptyList()
)

data class ProjectSettings(
    val title: String = "",
    val description: String = "",
    val keywords: String = "",
    val author: String = "",
    val favicon: String? = null,
    val charset: String = "UTF-8",
    val viewport: String = "width=device-width, initial-scale=1.0",
    val themeColor: String = "#6366F1",
    val language: String = "en",
    val customHeadCode: String = "",
    val customBodyStartCode: String = "",
    val customBodyEndCode: String = ""
)

data class GlobalStyles(
    val primaryColor: String = "#6366F1",
    val secondaryColor: String = "#EC4899",
    val backgroundColor: String = "#FFFFFF",
    val textColor: String = "#18181B",
    val linkColor: String = "#6366F1",
    val fontFamily: String = "Inter, system-ui, sans-serif",
    val baseFontSize: String = "16px",
    val lineHeight: String = "1.5",
    val containerMaxWidth: String = "1200px",
    val customCss: String = ""
)

data class Page(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val slug: String,
    val isHomepage: Boolean = false,
    val title: String = "",
    val description: String = "",
    val elements: List<WebElement> = emptyList(),
    val customCss: String = "",
    val customJs: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun createDefault() = Page(
            name = "Home",
            slug = "index",
            isHomepage = true,
            title = "Home"
        )
    }
}

data class Asset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AssetType,
    val path: String,
    val size: Long,
    val mimeType: String,
    val width: Int? = null,
    val height: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList()
)

enum class AssetType {
    IMAGE, VIDEO, AUDIO, FONT, DOCUMENT, SVG, OTHER
}

data class CustomFont(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val family: String,
    val weight: String = "400",
    val style: String = "normal",
    val path: String,
    val format: String
)
