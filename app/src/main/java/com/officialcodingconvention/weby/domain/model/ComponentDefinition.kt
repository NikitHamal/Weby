package com.officialcodingconvention.weby.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

data class ComponentDefinition(
    val type: ElementType,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val category: ComponentCategory,
    val defaultTag: String,
    val defaultStyles: ElementStyles = ElementStyles(),
    val defaultContent: String = "",
    val defaultAttributes: Map<String, String> = emptyMap(),
    val allowedChildren: Set<ElementType>? = null,
    val selfClosing: Boolean = false
)

enum class ComponentCategory(val displayName: String, val icon: ImageVector) {
    LAYOUT("Layout", Icons.Outlined.GridView),
    BASIC("Basic", Icons.Outlined.TextFields),
    FORMS("Forms", Icons.Outlined.EditNote),
    NAVIGATION("Navigation", Icons.Outlined.Menu),
    MEDIA("Media", Icons.Outlined.Image),
    ADVANCED("Advanced", Icons.Outlined.Code)
}

object ComponentLibrary {
    val allComponents: List<ComponentDefinition> by lazy { createComponentDefinitions() }

    val componentsByCategory: Map<ComponentCategory, List<ComponentDefinition>> by lazy {
        allComponents.groupBy { it.category }
    }

    fun getComponent(type: ElementType): ComponentDefinition? {
        return allComponents.find { it.type == type }
    }

    private fun createComponentDefinitions(): List<ComponentDefinition> = listOf(
        // Layout Components
        ComponentDefinition(
            type = ElementType.CONTAINER,
            name = "Container",
            description = "A flexible container for grouping elements",
            icon = Icons.Outlined.Crop169,
            category = ComponentCategory.LAYOUT,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                width = "100%",
                maxWidth = "1200px",
                marginLeft = "auto",
                marginRight = "auto",
                paddingLeft = "16px",
                paddingRight = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.SECTION,
            name = "Section",
            description = "A semantic section of your page",
            icon = Icons.Outlined.ViewDay,
            category = ComponentCategory.LAYOUT,
            defaultTag = "section",
            defaultStyles = ElementStyles(
                width = "100%",
                paddingTop = "64px",
                paddingBottom = "64px"
            )
        ),
        ComponentDefinition(
            type = ElementType.DIV,
            name = "Div",
            description = "A generic container element",
            icon = Icons.Outlined.Square,
            category = ComponentCategory.LAYOUT,
            defaultTag = "div"
        ),
        ComponentDefinition(
            type = ElementType.GRID,
            name = "Grid",
            description = "CSS Grid layout container",
            icon = Icons.Outlined.GridOn,
            category = ComponentCategory.LAYOUT,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                display = "grid",
                gridTemplateColumns = "repeat(3, 1fr)",
                gap = "24px"
            )
        ),
        ComponentDefinition(
            type = ElementType.FLEXBOX,
            name = "Flexbox",
            description = "Flexible box layout container",
            icon = Icons.Outlined.ViewColumn,
            category = ComponentCategory.LAYOUT,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                display = "flex",
                flexDirection = "row",
                alignItems = "center",
                gap = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.COLUMNS,
            name = "Columns",
            description = "Multi-column layout",
            icon = Icons.Outlined.ViewWeek,
            category = ComponentCategory.LAYOUT,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                display = "flex",
                flexWrap = "wrap",
                gap = "24px"
            )
        ),
        ComponentDefinition(
            type = ElementType.SPACER,
            name = "Spacer",
            description = "Adds vertical or horizontal space",
            icon = Icons.Outlined.SpaceBar,
            category = ComponentCategory.LAYOUT,
            defaultTag = "div",
            defaultStyles = ElementStyles(height = "32px")
        ),
        ComponentDefinition(
            type = ElementType.DIVIDER,
            name = "Divider",
            description = "A horizontal line separator",
            icon = Icons.Outlined.HorizontalRule,
            category = ComponentCategory.LAYOUT,
            defaultTag = "hr",
            selfClosing = true,
            defaultStyles = ElementStyles(
                borderTop = "1px solid #e5e7eb",
                marginTop = "24px",
                marginBottom = "24px"
            )
        ),

        // Typography Components
        ComponentDefinition(
            type = ElementType.H1,
            name = "Heading 1",
            description = "Primary page heading",
            icon = Icons.Outlined.Title,
            category = ComponentCategory.BASIC,
            defaultTag = "h1",
            defaultContent = "Heading 1",
            defaultStyles = ElementStyles(
                fontSize = "36px",
                fontWeight = "700",
                lineHeight = "1.2",
                marginBottom = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.H2,
            name = "Heading 2",
            description = "Secondary heading",
            icon = Icons.Outlined.Title,
            category = ComponentCategory.BASIC,
            defaultTag = "h2",
            defaultContent = "Heading 2",
            defaultStyles = ElementStyles(
                fontSize = "30px",
                fontWeight = "600",
                lineHeight = "1.2",
                marginBottom = "14px"
            )
        ),
        ComponentDefinition(
            type = ElementType.H3,
            name = "Heading 3",
            description = "Tertiary heading",
            icon = Icons.Outlined.Title,
            category = ComponentCategory.BASIC,
            defaultTag = "h3",
            defaultContent = "Heading 3",
            defaultStyles = ElementStyles(
                fontSize = "24px",
                fontWeight = "600",
                lineHeight = "1.3",
                marginBottom = "12px"
            )
        ),
        ComponentDefinition(
            type = ElementType.H4,
            name = "Heading 4",
            description = "Fourth-level heading",
            icon = Icons.Outlined.Title,
            category = ComponentCategory.BASIC,
            defaultTag = "h4",
            defaultContent = "Heading 4",
            defaultStyles = ElementStyles(
                fontSize = "20px",
                fontWeight = "600",
                lineHeight = "1.4",
                marginBottom = "10px"
            )
        ),
        ComponentDefinition(
            type = ElementType.H5,
            name = "Heading 5",
            description = "Fifth-level heading",
            icon = Icons.Outlined.Title,
            category = ComponentCategory.BASIC,
            defaultTag = "h5",
            defaultContent = "Heading 5",
            defaultStyles = ElementStyles(
                fontSize = "18px",
                fontWeight = "600",
                lineHeight = "1.4",
                marginBottom = "8px"
            )
        ),
        ComponentDefinition(
            type = ElementType.H6,
            name = "Heading 6",
            description = "Sixth-level heading",
            icon = Icons.Outlined.Title,
            category = ComponentCategory.BASIC,
            defaultTag = "h6",
            defaultContent = "Heading 6",
            defaultStyles = ElementStyles(
                fontSize = "16px",
                fontWeight = "600",
                lineHeight = "1.4",
                marginBottom = "8px"
            )
        ),
        ComponentDefinition(
            type = ElementType.P,
            name = "Paragraph",
            description = "A block of text",
            icon = Icons.Outlined.Notes,
            category = ComponentCategory.BASIC,
            defaultTag = "p",
            defaultContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            defaultStyles = ElementStyles(
                lineHeight = "1.6",
                marginBottom = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.SPAN,
            name = "Text Span",
            description = "Inline text element",
            icon = Icons.Outlined.TextFormat,
            category = ComponentCategory.BASIC,
            defaultTag = "span",
            defaultContent = "Inline text"
        ),
        ComponentDefinition(
            type = ElementType.A,
            name = "Link",
            description = "A clickable hyperlink",
            icon = Icons.Outlined.Link,
            category = ComponentCategory.BASIC,
            defaultTag = "a",
            defaultContent = "Click here",
            defaultAttributes = mapOf("href" to "#"),
            defaultStyles = ElementStyles(
                color = "#6366f1",
                textDecoration = "none"
            )
        ),
        ComponentDefinition(
            type = ElementType.BLOCKQUOTE,
            name = "Blockquote",
            description = "A quoted text block",
            icon = Icons.Outlined.FormatQuote,
            category = ComponentCategory.BASIC,
            defaultTag = "blockquote",
            defaultContent = "A wise quote goes here.",
            defaultStyles = ElementStyles(
                borderLeft = "4px solid #6366f1",
                paddingLeft = "16px",
                marginLeft = "0",
                fontStyle = "italic",
                color = "#6b7280"
            )
        ),
        ComponentDefinition(
            type = ElementType.CODE,
            name = "Inline Code",
            description = "Inline code snippet",
            icon = Icons.Outlined.Code,
            category = ComponentCategory.BASIC,
            defaultTag = "code",
            defaultContent = "const x = 42;",
            defaultStyles = ElementStyles(
                backgroundColor = "#f3f4f6",
                padding = "2px 6px",
                borderRadius = "4px",
                fontFamily = "monospace",
                fontSize = "14px"
            )
        ),
        ComponentDefinition(
            type = ElementType.PRE,
            name = "Code Block",
            description = "Preformatted code block",
            icon = Icons.Outlined.Terminal,
            category = ComponentCategory.BASIC,
            defaultTag = "pre",
            defaultContent = "function hello() {\n  console.log('Hello');\n}",
            defaultStyles = ElementStyles(
                backgroundColor = "#1f2937",
                color = "#f9fafb",
                padding = "16px",
                borderRadius = "8px",
                overflow = "auto",
                fontFamily = "monospace",
                fontSize = "14px"
            )
        ),
        ComponentDefinition(
            type = ElementType.UL,
            name = "Unordered List",
            description = "Bulleted list",
            icon = Icons.Outlined.FormatListBulleted,
            category = ComponentCategory.BASIC,
            defaultTag = "ul",
            defaultStyles = ElementStyles(
                paddingLeft = "20px",
                marginBottom = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.OL,
            name = "Ordered List",
            description = "Numbered list",
            icon = Icons.Outlined.FormatListNumbered,
            category = ComponentCategory.BASIC,
            defaultTag = "ol",
            defaultStyles = ElementStyles(
                paddingLeft = "20px",
                marginBottom = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.LI,
            name = "List Item",
            description = "Item in a list",
            icon = Icons.Outlined.FiberManualRecord,
            category = ComponentCategory.BASIC,
            defaultTag = "li",
            defaultContent = "List item",
            defaultStyles = ElementStyles(
                marginBottom = "8px"
            )
        ),
        ComponentDefinition(
            type = ElementType.HEADING,
            name = "Heading",
            description = "Text heading (H1-H6)",
            icon = Icons.Outlined.Title,
            category = ComponentCategory.BASIC,
            defaultTag = "h2",
            defaultContent = "Heading",
            defaultStyles = ElementStyles(
                fontWeight = "600",
                lineHeight = "1.2"
            )
        ),
        ComponentDefinition(
            type = ElementType.PARAGRAPH,
            name = "Paragraph",
            description = "A block of text",
            icon = Icons.Outlined.Notes,
            category = ComponentCategory.BASIC,
            defaultTag = "p",
            defaultContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            defaultStyles = ElementStyles(
                lineHeight = "1.6",
                marginBottom = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.TEXT_SPAN,
            name = "Text Span",
            description = "Inline text element",
            icon = Icons.Outlined.TextFormat,
            category = ComponentCategory.BASIC,
            defaultTag = "span",
            defaultContent = "Inline text"
        ),
        ComponentDefinition(
            type = ElementType.LINK,
            name = "Link",
            description = "A clickable hyperlink",
            icon = Icons.Outlined.Link,
            category = ComponentCategory.BASIC,
            defaultTag = "a",
            defaultContent = "Click here",
            defaultAttributes = mapOf("href" to "#"),
            defaultStyles = ElementStyles(
                color = "#6366f1",
                textDecoration = "none"
            )
        ),
        ComponentDefinition(
            type = ElementType.BUTTON,
            name = "Button",
            description = "A clickable button",
            icon = Icons.Outlined.SmartButton,
            category = ComponentCategory.BASIC,
            defaultTag = "button",
            defaultContent = "Button",
            defaultAttributes = mapOf("type" to "button"),
            defaultStyles = ElementStyles(
                backgroundColor = "#6366f1",
                color = "#ffffff",
                paddingTop = "12px",
                paddingBottom = "12px",
                paddingLeft = "24px",
                paddingRight = "24px",
                borderRadius = "8px",
                border = "none",
                fontWeight = "500",
                cursor = "pointer"
            )
        ),
        ComponentDefinition(
            type = ElementType.IMAGE,
            name = "Image",
            description = "An image element",
            icon = Icons.Outlined.Image,
            category = ComponentCategory.BASIC,
            defaultTag = "img",
            selfClosing = true,
            defaultAttributes = mapOf(
                "src" to "",
                "alt" to "Image description"
            ),
            defaultStyles = ElementStyles(
                maxWidth = "100%",
                height = "auto"
            )
        ),
        ComponentDefinition(
            type = ElementType.VIDEO,
            name = "Video",
            description = "A video player",
            icon = Icons.Outlined.Videocam,
            category = ComponentCategory.BASIC,
            defaultTag = "video",
            defaultAttributes = mapOf("controls" to "true"),
            defaultStyles = ElementStyles(maxWidth = "100%")
        ),
        ComponentDefinition(
            type = ElementType.AUDIO,
            name = "Audio",
            description = "An audio player",
            icon = Icons.Outlined.Audiotrack,
            category = ComponentCategory.BASIC,
            defaultTag = "audio",
            defaultAttributes = mapOf("controls" to "true")
        ),
        ComponentDefinition(
            type = ElementType.ICON,
            name = "Icon",
            description = "An icon element",
            icon = Icons.Outlined.EmojiEmotions,
            category = ComponentCategory.BASIC,
            defaultTag = "i",
            defaultStyles = ElementStyles(fontSize = "24px")
        ),
        ComponentDefinition(
            type = ElementType.EMBED,
            name = "Embed",
            description = "Embed external content (iframe)",
            icon = Icons.Outlined.WebAsset,
            category = ComponentCategory.BASIC,
            defaultTag = "iframe",
            defaultAttributes = mapOf(
                "src" to "",
                "frameborder" to "0",
                "allowfullscreen" to "true"
            ),
            defaultStyles = ElementStyles(
                width = "100%",
                height = "400px"
            )
        ),

        // Form Components
        ComponentDefinition(
            type = ElementType.FORM,
            name = "Form",
            description = "A form container",
            icon = Icons.Outlined.Assignment,
            category = ComponentCategory.FORMS,
            defaultTag = "form",
            defaultAttributes = mapOf("method" to "post")
        ),
        ComponentDefinition(
            type = ElementType.INPUT,
            name = "Text Input",
            description = "A text input field",
            icon = Icons.Outlined.TextFields,
            category = ComponentCategory.FORMS,
            defaultTag = "input",
            selfClosing = true,
            defaultAttributes = mapOf(
                "type" to "text",
                "placeholder" to "Enter text..."
            ),
            defaultStyles = ElementStyles(
                width = "100%",
                paddingTop = "12px",
                paddingBottom = "12px",
                paddingLeft = "16px",
                paddingRight = "16px",
                borderRadius = "8px",
                border = "1px solid #e5e7eb",
                fontSize = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.TEXTAREA,
            name = "Textarea",
            description = "A multi-line text input",
            icon = Icons.Outlined.Article,
            category = ComponentCategory.FORMS,
            defaultTag = "textarea",
            defaultAttributes = mapOf(
                "rows" to "4",
                "placeholder" to "Enter text..."
            ),
            defaultStyles = ElementStyles(
                width = "100%",
                paddingTop = "12px",
                paddingBottom = "12px",
                paddingLeft = "16px",
                paddingRight = "16px",
                borderRadius = "8px",
                border = "1px solid #e5e7eb",
                fontSize = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.SELECT,
            name = "Select",
            description = "A dropdown select menu",
            icon = Icons.Outlined.ArrowDropDownCircle,
            category = ComponentCategory.FORMS,
            defaultTag = "select",
            defaultStyles = ElementStyles(
                width = "100%",
                paddingTop = "12px",
                paddingBottom = "12px",
                paddingLeft = "16px",
                paddingRight = "16px",
                borderRadius = "8px",
                border = "1px solid #e5e7eb",
                fontSize = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.CHECKBOX,
            name = "Checkbox",
            description = "A checkbox input",
            icon = Icons.Outlined.CheckBox,
            category = ComponentCategory.FORMS,
            defaultTag = "input",
            selfClosing = true,
            defaultAttributes = mapOf("type" to "checkbox")
        ),
        ComponentDefinition(
            type = ElementType.RADIO,
            name = "Radio",
            description = "A radio button input",
            icon = Icons.Outlined.RadioButtonChecked,
            category = ComponentCategory.FORMS,
            defaultTag = "input",
            selfClosing = true,
            defaultAttributes = mapOf("type" to "radio")
        ),
        ComponentDefinition(
            type = ElementType.FILE_UPLOAD,
            name = "File Upload",
            description = "A file upload input",
            icon = Icons.Outlined.UploadFile,
            category = ComponentCategory.FORMS,
            defaultTag = "input",
            selfClosing = true,
            defaultAttributes = mapOf("type" to "file")
        ),
        ComponentDefinition(
            type = ElementType.SUBMIT_BUTTON,
            name = "Submit Button",
            description = "A form submit button",
            icon = Icons.Outlined.Send,
            category = ComponentCategory.FORMS,
            defaultTag = "button",
            defaultContent = "Submit",
            defaultAttributes = mapOf("type" to "submit"),
            defaultStyles = ElementStyles(
                backgroundColor = "#6366f1",
                color = "#ffffff",
                paddingTop = "12px",
                paddingBottom = "12px",
                paddingLeft = "24px",
                paddingRight = "24px",
                borderRadius = "8px",
                border = "none",
                fontWeight = "500",
                cursor = "pointer"
            )
        ),

        // Navigation Components
        ComponentDefinition(
            type = ElementType.NAVBAR,
            name = "Navbar",
            description = "A navigation bar",
            icon = Icons.Outlined.WebAsset,
            category = ComponentCategory.NAVIGATION,
            defaultTag = "nav",
            defaultStyles = ElementStyles(
                display = "flex",
                alignItems = "center",
                justifyContent = "space-between",
                padding = "16px 24px",
                backgroundColor = "#ffffff",
                boxShadow = "0 1px 3px rgba(0,0,0,0.1)"
            )
        ),
        ComponentDefinition(
            type = ElementType.MENU,
            name = "Menu",
            description = "A navigation menu",
            icon = Icons.Outlined.MenuOpen,
            category = ComponentCategory.NAVIGATION,
            defaultTag = "ul",
            defaultStyles = ElementStyles(
                display = "flex",
                gap = "24px"
            )
        ),
        ComponentDefinition(
            type = ElementType.HAMBURGER_MENU,
            name = "Hamburger Menu",
            description = "A mobile hamburger menu",
            icon = Icons.Outlined.Menu,
            category = ComponentCategory.NAVIGATION,
            defaultTag = "button",
            defaultStyles = ElementStyles(
                display = "none",
                backgroundColor = "transparent",
                border = "none",
                cursor = "pointer"
            )
        ),
        ComponentDefinition(
            type = ElementType.BREADCRUMB,
            name = "Breadcrumb",
            description = "A breadcrumb navigation",
            icon = Icons.Outlined.ChevronRight,
            category = ComponentCategory.NAVIGATION,
            defaultTag = "nav",
            defaultStyles = ElementStyles(
                display = "flex",
                alignItems = "center",
                gap = "8px"
            )
        ),
        ComponentDefinition(
            type = ElementType.PAGINATION,
            name = "Pagination",
            description = "A pagination component",
            icon = Icons.Outlined.MoreHoriz,
            category = ComponentCategory.NAVIGATION,
            defaultTag = "nav",
            defaultStyles = ElementStyles(
                display = "flex",
                gap = "8px",
                justifyContent = "center"
            )
        ),
        ComponentDefinition(
            type = ElementType.TABS,
            name = "Tabs",
            description = "A tabbed interface",
            icon = Icons.Outlined.Tab,
            category = ComponentCategory.NAVIGATION,
            defaultTag = "div"
        ),
        ComponentDefinition(
            type = ElementType.ACCORDION,
            name = "Accordion",
            description = "A collapsible accordion",
            icon = Icons.Outlined.UnfoldMore,
            category = ComponentCategory.NAVIGATION,
            defaultTag = "div"
        ),

        // Media Components
        ComponentDefinition(
            type = ElementType.IMAGE_GALLERY,
            name = "Image Gallery",
            description = "A grid of images",
            icon = Icons.Outlined.Collections,
            category = ComponentCategory.MEDIA,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                display = "grid",
                gridTemplateColumns = "repeat(3, 1fr)",
                gap = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.CAROUSEL,
            name = "Carousel",
            description = "An image/content slider",
            icon = Icons.Outlined.ViewCarousel,
            category = ComponentCategory.MEDIA,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                position = "relative",
                overflow = "hidden"
            )
        ),
        ComponentDefinition(
            type = ElementType.LIGHTBOX,
            name = "Lightbox",
            description = "A lightbox image viewer",
            icon = Icons.Outlined.ZoomIn,
            category = ComponentCategory.MEDIA,
            defaultTag = "div"
        ),
        ComponentDefinition(
            type = ElementType.BACKGROUND_VIDEO,
            name = "Background Video",
            description = "A fullscreen background video",
            icon = Icons.Outlined.SlowMotionVideo,
            category = ComponentCategory.MEDIA,
            defaultTag = "video",
            defaultAttributes = mapOf(
                "autoplay" to "true",
                "muted" to "true",
                "loop" to "true",
                "playsinline" to "true"
            ),
            defaultStyles = ElementStyles(
                position = "absolute",
                top = "0",
                left = "0",
                width = "100%",
                height = "100%",
                objectFit = "cover",
                zIndex = -1
            )
        ),
        ComponentDefinition(
            type = ElementType.SVG_CONTAINER,
            name = "SVG Container",
            description = "A container for SVG graphics",
            icon = Icons.Outlined.Category,
            category = ComponentCategory.MEDIA,
            defaultTag = "svg"
        ),

        // Advanced Components
        ComponentDefinition(
            type = ElementType.CUSTOM_HTML,
            name = "Custom HTML",
            description = "Insert custom HTML code",
            icon = Icons.Outlined.Code,
            category = ComponentCategory.ADVANCED,
            defaultTag = "div"
        ),
        ComponentDefinition(
            type = ElementType.CODE_SNIPPET,
            name = "Code Snippet",
            description = "Display code with syntax highlighting",
            icon = Icons.Outlined.IntegrationInstructions,
            category = ComponentCategory.ADVANCED,
            defaultTag = "pre",
            defaultStyles = ElementStyles(
                backgroundColor = "#1f2937",
                color = "#f9fafb",
                padding = "16px",
                borderRadius = "8px",
                overflow = "auto"
            )
        ),
        ComponentDefinition(
            type = ElementType.MAP_EMBED,
            name = "Map",
            description = "Embed a map",
            icon = Icons.Outlined.Map,
            category = ComponentCategory.ADVANCED,
            defaultTag = "iframe",
            defaultStyles = ElementStyles(
                width = "100%",
                height = "400px",
                border = "none"
            )
        ),
        ComponentDefinition(
            type = ElementType.SOCIAL_ICONS,
            name = "Social Icons",
            description = "Social media icon links",
            icon = Icons.Outlined.Share,
            category = ComponentCategory.ADVANCED,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                display = "flex",
                gap = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.TABLE,
            name = "Table",
            description = "A data table",
            icon = Icons.Outlined.TableChart,
            category = ComponentCategory.ADVANCED,
            defaultTag = "table",
            defaultStyles = ElementStyles(
                width = "100%",
                borderCollapse = "collapse"
            )
        ),
        ComponentDefinition(
            type = ElementType.LIST,
            name = "List",
            description = "An ordered or unordered list",
            icon = Icons.Outlined.FormatListBulleted,
            category = ComponentCategory.ADVANCED,
            defaultTag = "ul"
        ),
        ComponentDefinition(
            type = ElementType.CARD,
            name = "Card",
            description = "A content card",
            icon = Icons.Outlined.CreditCard,
            category = ComponentCategory.ADVANCED,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                backgroundColor = "#ffffff",
                borderRadius = "12px",
                boxShadow = "0 4px 6px -1px rgba(0,0,0,0.1)",
                padding = "24px"
            )
        ),
        ComponentDefinition(
            type = ElementType.MODAL,
            name = "Modal",
            description = "A popup modal dialog",
            icon = Icons.Outlined.OpenInNew,
            category = ComponentCategory.ADVANCED,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                position = "fixed",
                top = "50%",
                left = "50%",
                transform = "translate(-50%, -50%)",
                backgroundColor = "#ffffff",
                borderRadius = "16px",
                boxShadow = "0 25px 50px -12px rgba(0,0,0,0.25)",
                padding = "32px",
                zIndex = 1000
            )
        ),
        ComponentDefinition(
            type = ElementType.TOOLTIP,
            name = "Tooltip",
            description = "A hover tooltip",
            icon = Icons.Outlined.Info,
            category = ComponentCategory.ADVANCED,
            defaultTag = "div"
        ),

        // Additional Form Components
        ComponentDefinition(
            type = ElementType.LABEL,
            name = "Label",
            description = "A form label",
            icon = Icons.Outlined.Label,
            category = ComponentCategory.FORMS,
            defaultTag = "label",
            defaultContent = "Label",
            defaultStyles = ElementStyles(
                fontWeight = "500",
                marginBottom = "4px",
                display = "block"
            )
        ),
        ComponentDefinition(
            type = ElementType.FIELDSET,
            name = "Fieldset",
            description = "Groups related form elements",
            icon = Icons.Outlined.Inventory2,
            category = ComponentCategory.FORMS,
            defaultTag = "fieldset",
            defaultStyles = ElementStyles(
                border = "1px solid #e5e7eb",
                borderRadius = "8px",
                padding = "16px"
            )
        ),

        // Additional Basic Components
        ComponentDefinition(
            type = ElementType.LINK_BUTTON,
            name = "Link Button",
            description = "A button that looks like a link",
            icon = Icons.Outlined.CallMade,
            category = ComponentCategory.BASIC,
            defaultTag = "a",
            defaultContent = "Link Button",
            defaultAttributes = mapOf("href" to "#"),
            defaultStyles = ElementStyles(
                backgroundColor = "#6366f1",
                color = "#ffffff",
                paddingTop = "12px",
                paddingBottom = "12px",
                paddingLeft = "24px",
                paddingRight = "24px",
                borderRadius = "8px",
                textDecoration = "none",
                display = "inline-block"
            )
        ),
        ComponentDefinition(
            type = ElementType.ICON_BUTTON,
            name = "Icon Button",
            description = "A button with icon only",
            icon = Icons.Outlined.TouchApp,
            category = ComponentCategory.BASIC,
            defaultTag = "button",
            defaultContent = "",
            defaultAttributes = mapOf("type" to "button"),
            defaultStyles = ElementStyles(
                backgroundColor = "transparent",
                border = "none",
                padding = "8px",
                borderRadius = "50%",
                cursor = "pointer"
            )
        ),
        ComponentDefinition(
            type = ElementType.BADGE,
            name = "Badge",
            description = "A small status indicator",
            icon = Icons.Outlined.NewReleases,
            category = ComponentCategory.BASIC,
            defaultTag = "span",
            defaultContent = "Badge",
            defaultStyles = ElementStyles(
                backgroundColor = "#6366f1",
                color = "#ffffff",
                paddingTop = "2px",
                paddingBottom = "2px",
                paddingLeft = "8px",
                paddingRight = "8px",
                borderRadius = "9999px",
                fontSize = "12px",
                fontWeight = "500"
            )
        ),
        ComponentDefinition(
            type = ElementType.CHIP,
            name = "Chip",
            description = "A compact element for tags",
            icon = Icons.Outlined.Memory,
            category = ComponentCategory.BASIC,
            defaultTag = "span",
            defaultContent = "Chip",
            defaultStyles = ElementStyles(
                backgroundColor = "#f3f4f6",
                color = "#374151",
                paddingTop = "4px",
                paddingBottom = "4px",
                paddingLeft = "12px",
                paddingRight = "12px",
                borderRadius = "16px",
                fontSize = "14px"
            )
        ),

        // Additional Media Components
        ComponentDefinition(
            type = ElementType.IFRAME,
            name = "IFrame",
            description = "Embed external content",
            icon = Icons.Outlined.WebAsset,
            category = ComponentCategory.MEDIA,
            defaultTag = "iframe",
            defaultAttributes = mapOf(
                "src" to "",
                "frameborder" to "0"
            ),
            defaultStyles = ElementStyles(
                width = "100%",
                height = "400px",
                border = "none"
            )
        ),
        ComponentDefinition(
            type = ElementType.CANVAS,
            name = "Canvas",
            description = "A drawing canvas",
            icon = Icons.Outlined.Brush,
            category = ComponentCategory.MEDIA,
            defaultTag = "canvas",
            defaultStyles = ElementStyles(
                width = "400px",
                height = "300px",
                border = "1px solid #e5e7eb"
            )
        ),
        ComponentDefinition(
            type = ElementType.SVG,
            name = "SVG",
            description = "Scalable vector graphics",
            icon = Icons.Outlined.Architecture,
            category = ComponentCategory.MEDIA,
            defaultTag = "svg",
            defaultStyles = ElementStyles(
                width = "100px",
                height = "100px"
            )
        ),
        ComponentDefinition(
            type = ElementType.FIGURE,
            name = "Figure",
            description = "Self-contained media with caption",
            icon = Icons.Outlined.PhotoSizeSelectActual,
            category = ComponentCategory.MEDIA,
            defaultTag = "figure",
            defaultStyles = ElementStyles(
                margin = "0",
                textAlign = "center"
            )
        ),

        // Additional Navigation Components
        ComponentDefinition(
            type = ElementType.DROPDOWN,
            name = "Dropdown",
            description = "A dropdown menu",
            icon = Icons.Outlined.ArrowDropDown,
            category = ComponentCategory.NAVIGATION,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                position = "relative",
                display = "inline-block"
            )
        ),

        // Additional Advanced Components
        ComponentDefinition(
            type = ElementType.PROGRESS,
            name = "Progress Bar",
            description = "A progress indicator",
            icon = Icons.Outlined.LinearScale,
            category = ComponentCategory.ADVANCED,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                width = "100%",
                height = "8px",
                backgroundColor = "#e5e7eb",
                borderRadius = "4px",
                overflow = "hidden"
            )
        ),
        ComponentDefinition(
            type = ElementType.SLIDER,
            name = "Slider",
            description = "A range slider input",
            icon = Icons.Outlined.Tune,
            category = ComponentCategory.ADVANCED,
            defaultTag = "input",
            selfClosing = true,
            defaultAttributes = mapOf(
                "type" to "range",
                "min" to "0",
                "max" to "100"
            ),
            defaultStyles = ElementStyles(
                width = "100%"
            )
        ),
        ComponentDefinition(
            type = ElementType.MAP,
            name = "Interactive Map",
            description = "An interactive map component",
            icon = Icons.Outlined.Map,
            category = ComponentCategory.ADVANCED,
            defaultTag = "div",
            defaultStyles = ElementStyles(
                width = "100%",
                height = "400px",
                backgroundColor = "#f3f4f6"
            )
        ),

        // Additional Layout Components
        ComponentDefinition(
            type = ElementType.ARTICLE,
            name = "Article",
            description = "An article section",
            icon = Icons.Outlined.Article,
            category = ComponentCategory.LAYOUT,
            defaultTag = "article",
            defaultStyles = ElementStyles(
                marginBottom = "24px"
            )
        ),
        ComponentDefinition(
            type = ElementType.HEADER,
            name = "Header",
            description = "Page or section header",
            icon = Icons.Outlined.VerticalAlignTop,
            category = ComponentCategory.LAYOUT,
            defaultTag = "header",
            defaultStyles = ElementStyles(
                padding = "16px 0"
            )
        ),
        ComponentDefinition(
            type = ElementType.FOOTER,
            name = "Footer",
            description = "Page or section footer",
            icon = Icons.Outlined.VerticalAlignBottom,
            category = ComponentCategory.LAYOUT,
            defaultTag = "footer",
            defaultStyles = ElementStyles(
                padding = "24px 0",
                backgroundColor = "#f9fafb"
            )
        ),
        ComponentDefinition(
            type = ElementType.MAIN,
            name = "Main",
            description = "Main content area",
            icon = Icons.Outlined.WebAsset,
            category = ComponentCategory.LAYOUT,
            defaultTag = "main",
            defaultStyles = ElementStyles(
                padding = "24px"
            )
        ),
        ComponentDefinition(
            type = ElementType.ASIDE,
            name = "Aside",
            description = "Sidebar content",
            icon = Icons.Outlined.ViewSidebar,
            category = ComponentCategory.LAYOUT,
            defaultTag = "aside",
            defaultStyles = ElementStyles(
                width = "300px",
                padding = "16px"
            )
        ),
        ComponentDefinition(
            type = ElementType.NAV,
            name = "Navigation",
            description = "Navigation container",
            icon = Icons.Outlined.Menu,
            category = ComponentCategory.LAYOUT,
            defaultTag = "nav"
        )
    )
}
