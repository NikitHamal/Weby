package com.officialcodingconvention.weby.domain.model

import java.util.UUID

data class WebElement(
    val id: String = UUID.randomUUID().toString(),
    val type: ElementType,
    val tag: String,
    val name: String = "",
    val content: String = "",
    val attributes: Map<String, String> = emptyMap(),
    val styles: ElementStyles = ElementStyles(),
    val responsiveStyles: Map<Breakpoint, ElementStyles> = emptyMap(),
    val children: List<WebElement> = emptyList(),
    val parentId: String? = null,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val classes: List<String> = emptyList(),
    val customId: String? = null,
    val animations: List<ElementAnimation> = emptyList(),
    val interactions: List<Interaction> = emptyList()
)

enum class ElementType {
    // Layout
    CONTAINER, SECTION, DIV, ARTICLE, HEADER, FOOTER, MAIN, ASIDE, NAV, GRID, FLEXBOX, COLUMNS, SPACER, DIVIDER,
    // Typography
    H1, H2, H3, H4, H5, H6, P, SPAN, A, BLOCKQUOTE, CODE, PRE, UL, OL, LI, HEADING, PARAGRAPH, TEXT_SPAN, LINK,
    // Basic
    BUTTON, LINK_BUTTON, ICON_BUTTON, CARD, BADGE, CHIP, IMAGE, VIDEO, AUDIO, ICON, EMBED,
    // Forms
    FORM, INPUT, TEXTAREA, SELECT, CHECKBOX, RADIO, LABEL, FIELDSET, FILE_UPLOAD, SUBMIT_BUTTON,
    // Navigation
    NAVBAR, MENU, HAMBURGER_MENU, BREADCRUMB, PAGINATION, TABS, ACCORDION, DROPDOWN,
    // Media
    IMAGE_GALLERY, CAROUSEL, LIGHTBOX, BACKGROUND_VIDEO, SVG_CONTAINER, IFRAME, CANVAS, SVG, FIGURE,
    // Advanced
    CUSTOM_HTML, CODE_SNIPPET, MAP_EMBED, SOCIAL_ICONS, SHARE_BUTTONS, TABLE, LIST, MODAL, TOOLTIP, PROGRESS, SLIDER, MAP
}

enum class Breakpoint(val width: Int, val label: String) {
    MOBILE(480, "Mobile"),
    TABLET(768, "Tablet"),
    DESKTOP(1024, "Desktop"),
    LARGE_DESKTOP(1440, "Large Desktop")
}

data class ElementStyles(
    // Display & Position
    val display: String? = null,
    val position: String? = null,
    val top: String? = null,
    val right: String? = null,
    val bottom: String? = null,
    val left: String? = null,
    val zIndex: Int? = null,
    val float: String? = null,
    val clear: String? = null,
    val overflow: String? = null,
    val overflowX: String? = null,
    val overflowY: String? = null,

    // Dimensions
    val width: String? = null,
    val height: String? = null,
    val minWidth: String? = null,
    val minHeight: String? = null,
    val maxWidth: String? = null,
    val maxHeight: String? = null,
    val aspectRatio: String? = null,

    // Spacing
    val margin: String? = null,
    val marginTop: String? = null,
    val marginRight: String? = null,
    val marginBottom: String? = null,
    val marginLeft: String? = null,
    val padding: String? = null,
    val paddingTop: String? = null,
    val paddingRight: String? = null,
    val paddingBottom: String? = null,
    val paddingLeft: String? = null,

    // Flexbox
    val flexDirection: String? = null,
    val flexWrap: String? = null,
    val justifyContent: String? = null,
    val alignItems: String? = null,
    val alignContent: String? = null,
    val gap: String? = null,
    val rowGap: String? = null,
    val columnGap: String? = null,
    val flex: String? = null,
    val flexGrow: String? = null,
    val flexShrink: String? = null,
    val flexBasis: String? = null,
    val alignSelf: String? = null,
    val order: Int? = null,

    // Grid
    val gridTemplateColumns: String? = null,
    val gridTemplateRows: String? = null,
    val gridTemplateAreas: String? = null,
    val gridColumn: String? = null,
    val gridRow: String? = null,
    val gridArea: String? = null,
    val gridAutoColumns: String? = null,
    val gridAutoRows: String? = null,
    val gridAutoFlow: String? = null,

    // Typography
    val fontFamily: String? = null,
    val fontSize: String? = null,
    val fontWeight: String? = null,
    val fontStyle: String? = null,
    val lineHeight: String? = null,
    val letterSpacing: String? = null,
    val textAlign: String? = null,
    val textDecoration: String? = null,
    val textTransform: String? = null,
    val whiteSpace: String? = null,
    val wordBreak: String? = null,
    val wordSpacing: String? = null,
    val textOverflow: String? = null,
    val textShadow: String? = null,

    // Colors & Backgrounds
    val color: String? = null,
    val backgroundColor: String? = null,
    val backgroundImage: String? = null,
    val backgroundSize: String? = null,
    val backgroundPosition: String? = null,
    val backgroundRepeat: String? = null,
    val backgroundAttachment: String? = null,
    val backgroundClip: String? = null,
    val backgroundOrigin: String? = null,
    val gradient: GradientStyle? = null,
    val opacity: Float? = null,
    val mixBlendMode: String? = null,

    // Borders
    val border: String? = null,
    val borderWidth: String? = null,
    val borderStyle: String? = null,
    val borderColor: String? = null,
    val borderTop: String? = null,
    val borderRight: String? = null,
    val borderBottom: String? = null,
    val borderLeft: String? = null,
    val borderRadius: String? = null,
    val borderTopLeftRadius: String? = null,
    val borderTopRightRadius: String? = null,
    val borderBottomRightRadius: String? = null,
    val borderBottomLeftRadius: String? = null,
    val outline: String? = null,
    val outlineOffset: String? = null,

    // Effects
    val boxShadow: String? = null,
    val filter: String? = null,
    val backdropFilter: String? = null,

    // Transforms
    val transform: String? = null,
    val transformOrigin: String? = null,
    val perspective: String? = null,

    // Transitions & Animations
    val transition: String? = null,
    val animation: String? = null,

    // Other
    val cursor: String? = null,
    val pointerEvents: String? = null,
    val userSelect: String? = null,
    val visibility: String? = null,
    val objectFit: String? = null,
    val objectPosition: String? = null,

    // Table
    val borderCollapse: String? = null,
    val borderSpacing: String? = null,
    val tableLayout: String? = null,
    val captionSide: String? = null,
    val emptyCells: String? = null,

    // Pseudo-state styles
    val hoverStyles: Map<String, String>? = null,
    val activeStyles: Map<String, String>? = null,
    val focusStyles: Map<String, String>? = null,

    // Custom CSS
    val customCss: String? = null
)

data class GradientStyle(
    val type: GradientType = GradientType.LINEAR,
    val angle: Int = 180,
    val stops: List<GradientStop> = emptyList()
)

enum class GradientType { LINEAR, RADIAL, CONIC }

data class GradientStop(
    val color: String,
    val position: Int
)
