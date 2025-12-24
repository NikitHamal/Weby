package com.officialcodingconvention.weby.presentation.screens.editor

import com.officialcodingconvention.weby.domain.model.*

class CodeGenerator {

    fun generateHtml(page: Page, project: Project): String {
        return buildString {
            appendLine("<!DOCTYPE html>")
            appendLine("<html lang=\"${project.settings.language}\">")
            appendLine("<head>")
            appendLine("    <meta charset=\"${project.settings.charset}\">")
            appendLine("    <meta name=\"viewport\" content=\"${project.settings.viewport}\">")

            if (page.title.isNotEmpty()) {
                appendLine("    <title>${escapeHtml(page.title)}</title>")
            }
            if (page.description.isNotEmpty()) {
                appendLine("    <meta name=\"description\" content=\"${escapeHtml(page.description)}\">")
            }
            if (project.settings.keywords.isNotEmpty()) {
                appendLine("    <meta name=\"keywords\" content=\"${escapeHtml(project.settings.keywords)}\">")
            }
            if (project.settings.author.isNotEmpty()) {
                appendLine("    <meta name=\"author\" content=\"${escapeHtml(project.settings.author)}\">")
            }

            appendLine("    <meta name=\"theme-color\" content=\"${project.settings.themeColor}\">")

            if (project.settings.favicon != null) {
                appendLine("    <link rel=\"icon\" href=\"${project.settings.favicon}\">")
            }

            appendLine("    <link rel=\"stylesheet\" href=\"styles.css\">")

            if (project.settings.customHeadCode.isNotBlank()) {
                appendLine("    ${project.settings.customHeadCode}")
            }

            appendLine("</head>")
            appendLine("<body>")

            if (project.settings.customBodyStartCode.isNotBlank()) {
                appendLine("    ${project.settings.customBodyStartCode}")
            }

            page.elements.forEach { element ->
                append(generateElementHtml(element, 1))
            }

            if (project.settings.customBodyEndCode.isNotBlank()) {
                appendLine("    ${project.settings.customBodyEndCode}")
            }

            appendLine("    <script src=\"scripts.js\"></script>")
            appendLine("</body>")
            appendLine("</html>")
        }
    }

    private fun generateElementHtml(element: WebElement, indentLevel: Int): String {
        val indent = "    ".repeat(indentLevel)
        val componentDef = ComponentLibrary.getComponent(element.type)

        return buildString {
            val tag = element.tag
            val attributes = buildAttributeString(element)
            val classes = buildClassString(element)
            val inlineStyle = if (element.customId != null || element.classes.isEmpty()) {
                buildInlineStyleString(element.styles)
            } else ""

            if (componentDef?.selfClosing == true) {
                appendLine("$indent<$tag$classes$attributes$inlineStyle />")
            } else {
                append("$indent<$tag$classes$attributes$inlineStyle>")

                if (element.content.isNotEmpty() && element.children.isEmpty()) {
                    append(escapeHtml(element.content))
                    appendLine("</$tag>")
                } else if (element.children.isNotEmpty()) {
                    appendLine()
                    element.children.forEach { child ->
                        append(generateElementHtml(child, indentLevel + 1))
                    }
                    appendLine("$indent</$tag>")
                } else {
                    appendLine("</$tag>")
                }
            }
        }
    }

    private fun buildAttributeString(element: WebElement): String {
        val attrs = mutableListOf<String>()

        if (element.customId != null) {
            attrs.add("id=\"${element.customId}\"")
        }

        element.attributes.forEach { (key, value) ->
            if (value.isNotEmpty()) {
                attrs.add("$key=\"${escapeHtml(value)}\"")
            } else {
                attrs.add(key)
            }
        }

        return if (attrs.isNotEmpty()) " ${attrs.joinToString(" ")}" else ""
    }

    private fun buildClassString(element: WebElement): String {
        return if (element.classes.isNotEmpty()) {
            " class=\"${element.classes.joinToString(" ")}\""
        } else ""
    }

    private fun buildInlineStyleString(styles: ElementStyles): String {
        val styleProps = mutableListOf<String>()

        styles.display?.let { styleProps.add("display: $it") }
        styles.position?.let { styleProps.add("position: $it") }
        styles.top?.let { styleProps.add("top: $it") }
        styles.right?.let { styleProps.add("right: $it") }
        styles.bottom?.let { styleProps.add("bottom: $it") }
        styles.left?.let { styleProps.add("left: $it") }
        styles.zIndex?.let { styleProps.add("z-index: $it") }
        styles.width?.let { styleProps.add("width: $it") }
        styles.height?.let { styleProps.add("height: $it") }
        styles.minWidth?.let { styleProps.add("min-width: $it") }
        styles.minHeight?.let { styleProps.add("min-height: $it") }
        styles.maxWidth?.let { styleProps.add("max-width: $it") }
        styles.maxHeight?.let { styleProps.add("max-height: $it") }
        styles.margin?.let { styleProps.add("margin: $it") }
        styles.padding?.let { styleProps.add("padding: $it") }
        styles.flexDirection?.let { styleProps.add("flex-direction: $it") }
        styles.justifyContent?.let { styleProps.add("justify-content: $it") }
        styles.alignItems?.let { styleProps.add("align-items: $it") }
        styles.gap?.let { styleProps.add("gap: $it") }
        styles.gridTemplateColumns?.let { styleProps.add("grid-template-columns: $it") }
        styles.gridTemplateRows?.let { styleProps.add("grid-template-rows: $it") }
        styles.fontFamily?.let { styleProps.add("font-family: $it") }
        styles.fontSize?.let { styleProps.add("font-size: $it") }
        styles.fontWeight?.let { styleProps.add("font-weight: $it") }
        styles.lineHeight?.let { styleProps.add("line-height: $it") }
        styles.textAlign?.let { styleProps.add("text-align: $it") }
        styles.color?.let { styleProps.add("color: $it") }
        styles.backgroundColor?.let { styleProps.add("background-color: $it") }
        styles.border?.let { styleProps.add("border: $it") }
        styles.borderRadius?.let { styleProps.add("border-radius: $it") }
        styles.boxShadow?.let { styleProps.add("box-shadow: $it") }
        styles.opacity?.let { styleProps.add("opacity: $it") }
        styles.transform?.let { styleProps.add("transform: $it") }
        styles.transition?.let { styleProps.add("transition: $it") }
        styles.cursor?.let { styleProps.add("cursor: $it") }

        return if (styleProps.isNotEmpty()) {
            " style=\"${styleProps.joinToString("; ")}\""
        } else ""
    }

    fun generateCss(page: Page, project: Project): String {
        return buildString {
            appendLine("/* Weby Generated Styles */")
            appendLine()
            appendLine(":root {")
            appendLine("    --primary-color: ${project.globalStyles.primaryColor};")
            appendLine("    --secondary-color: ${project.globalStyles.secondaryColor};")
            appendLine("    --background-color: ${project.globalStyles.backgroundColor};")
            appendLine("    --text-color: ${project.globalStyles.textColor};")
            appendLine("    --link-color: ${project.globalStyles.linkColor};")
            appendLine("    --font-family: ${project.globalStyles.fontFamily};")
            appendLine("    --base-font-size: ${project.globalStyles.baseFontSize};")
            appendLine("    --line-height: ${project.globalStyles.lineHeight};")
            appendLine("    --container-max-width: ${project.globalStyles.containerMaxWidth};")

            project.cssVariables.forEach { (name, value) ->
                appendLine("    --$name: $value;")
            }
            appendLine("}")
            appendLine()

            appendLine("*, *::before, *::after {")
            appendLine("    box-sizing: border-box;")
            appendLine("}")
            appendLine()

            appendLine("body {")
            appendLine("    margin: 0;")
            appendLine("    padding: 0;")
            appendLine("    font-family: var(--font-family);")
            appendLine("    font-size: var(--base-font-size);")
            appendLine("    line-height: var(--line-height);")
            appendLine("    color: var(--text-color);")
            appendLine("    background-color: var(--background-color);")
            appendLine("}")
            appendLine()

            appendLine("a {")
            appendLine("    color: var(--link-color);")
            appendLine("    text-decoration: none;")
            appendLine("}")
            appendLine()

            appendLine("img {")
            appendLine("    max-width: 100%;")
            appendLine("    height: auto;")
            appendLine("}")
            appendLine()

            if (project.globalStyles.customCss.isNotBlank()) {
                appendLine("/* Custom Global Styles */")
                appendLine(project.globalStyles.customCss)
                appendLine()
            }

            generateResponsiveCss(page.elements, Breakpoint.TABLET, this)
            generateResponsiveCss(page.elements, Breakpoint.MOBILE, this)

            if (page.customCss.isNotBlank()) {
                appendLine("/* Page Custom Styles */")
                appendLine(page.customCss)
            }
        }
    }

    private fun generateResponsiveCss(
        elements: List<WebElement>,
        breakpoint: Breakpoint,
        builder: StringBuilder
    ) {
        val responsiveStyles = elements.flatMap { element ->
            collectResponsiveStyles(element, breakpoint)
        }

        if (responsiveStyles.isNotEmpty()) {
            builder.appendLine("@media (max-width: ${breakpoint.width}px) {")
            responsiveStyles.forEach { (selector, styles) ->
                builder.appendLine("    $selector {")
                styles.forEach { (prop, value) ->
                    builder.appendLine("        $prop: $value;")
                }
                builder.appendLine("    }")
            }
            builder.appendLine("}")
            builder.appendLine()
        }
    }

    private fun collectResponsiveStyles(
        element: WebElement,
        breakpoint: Breakpoint
    ): List<Pair<String, Map<String, String>>> {
        val result = mutableListOf<Pair<String, Map<String, String>>>()

        element.responsiveStyles[breakpoint]?.let { styles ->
            val selector = element.customId?.let { "#$it" }
                ?: element.classes.firstOrNull()?.let { ".$it" }
                ?: "[data-weby-id=\"${element.id}\"]"

            val styleMap = buildStyleMap(styles)
            if (styleMap.isNotEmpty()) {
                result.add(selector to styleMap)
            }
        }

        element.children.forEach { child ->
            result.addAll(collectResponsiveStyles(child, breakpoint))
        }

        return result
    }

    private fun buildStyleMap(styles: ElementStyles): Map<String, String> {
        val map = mutableMapOf<String, String>()

        styles.display?.let { map["display"] = it }
        styles.width?.let { map["width"] = it }
        styles.height?.let { map["height"] = it }
        styles.padding?.let { map["padding"] = it }
        styles.margin?.let { map["margin"] = it }
        styles.fontSize?.let { map["font-size"] = it }
        styles.flexDirection?.let { map["flex-direction"] = it }
        styles.gridTemplateColumns?.let { map["grid-template-columns"] = it }

        return map
    }

    fun generateJs(page: Page): String {
        return buildString {
            appendLine("// Weby Generated Scripts")
            appendLine()
            appendLine("document.addEventListener('DOMContentLoaded', function() {")
            appendLine("    console.log('Weby site loaded');")
            appendLine("});")
            appendLine()

            if (page.customJs.isNotBlank()) {
                appendLine("// Page Custom Scripts")
                appendLine(page.customJs)
            }
        }
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}
