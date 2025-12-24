package com.officialcodingconvention.weby.domain.model

import java.util.UUID

data class Template(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val category: TemplateCategory,
    val thumbnailPath: String? = null,
    val previewImages: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isBuiltIn: Boolean = false,
    val isUserCreated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val projectData: String = ""
)

enum class TemplateCategory(val displayName: String) {
    LANDING_PAGE("Landing Pages"),
    PORTFOLIO("Portfolio"),
    BLOG("Blog"),
    BUSINESS("Business"),
    ECOMMERCE("E-Commerce"),
    DOCUMENTATION("Documentation"),
    PERSONAL("Personal"),
    STARTUP("Startup"),
    AGENCY("Agency"),
    SAAS("SaaS"),
    SECTION("Section Templates"),
    COMPONENT("Components"),
    BLANK("Blank")
}

data class SectionTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val category: SectionCategory,
    val thumbnailPath: String? = null,
    val elements: List<WebElement> = emptyList(),
    val isBuiltIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class SectionCategory(val displayName: String) {
    HERO("Hero Sections"),
    FEATURES("Feature Grids"),
    TESTIMONIALS("Testimonials"),
    PRICING("Pricing Tables"),
    CTA("Call to Action"),
    FOOTER("Footers"),
    HEADER("Headers"),
    ABOUT("About Sections"),
    CONTACT("Contact Forms"),
    TEAM("Team Sections"),
    FAQ("FAQ Sections"),
    STATS("Statistics"),
    GALLERY("Galleries"),
    CONTENT("Content Blocks")
}

data class SavedComponent(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val elements: List<WebElement>,
    val thumbnailPath: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class CssClass(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val styles: ElementStyles,
    val responsiveStyles: Map<Breakpoint, ElementStyles> = emptyMap(),
    val pseudoStates: Map<String, ElementStyles> = emptyMap(),
    val description: String = "",
    val isGlobal: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
