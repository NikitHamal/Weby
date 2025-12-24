package com.officialcodingconvention.weby.presentation.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CodeLanguage(val displayName: String, val extension: String) {
    HTML("HTML", "html"),
    CSS("CSS", "css"),
    JAVASCRIPT("JavaScript", "js")
}

@Composable
fun CodeEditorPanel(
    htmlCode: String,
    cssCode: String,
    jsCode: String,
    onHtmlChange: (String) -> Unit,
    onCssChange: (String) -> Unit,
    onJsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf(CodeLanguage.HTML) }
    var isEditable by remember { mutableStateOf(false) }
    var showLineNumbers by remember { mutableStateOf(true) }
    var wrapLines by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        CodeEditorToolbar(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it },
            isEditable = isEditable,
            onEditableToggle = { isEditable = it },
            showLineNumbers = showLineNumbers,
            onLineNumbersToggle = { showLineNumbers = it },
            wrapLines = wrapLines,
            onWrapLinesToggle = { wrapLines = it },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CodeEditorColors.Background)
        ) {
            when (selectedLanguage) {
                CodeLanguage.HTML -> CodeEditor(
                    code = htmlCode,
                    language = CodeLanguage.HTML,
                    isEditable = isEditable,
                    showLineNumbers = showLineNumbers,
                    wrapLines = wrapLines,
                    onCodeChange = onHtmlChange
                )
                CodeLanguage.CSS -> CodeEditor(
                    code = cssCode,
                    language = CodeLanguage.CSS,
                    isEditable = isEditable,
                    showLineNumbers = showLineNumbers,
                    wrapLines = wrapLines,
                    onCodeChange = onCssChange
                )
                CodeLanguage.JAVASCRIPT -> CodeEditor(
                    code = jsCode,
                    language = CodeLanguage.JAVASCRIPT,
                    isEditable = isEditable,
                    showLineNumbers = showLineNumbers,
                    wrapLines = wrapLines,
                    onCodeChange = onJsChange
                )
            }
        }
    }
}

@Composable
private fun CodeEditorToolbar(
    selectedLanguage: CodeLanguage,
    onLanguageSelected: (CodeLanguage) -> Unit,
    isEditable: Boolean,
    onEditableToggle: (Boolean) -> Unit,
    showLineNumbers: Boolean,
    onLineNumbersToggle: (Boolean) -> Unit,
    wrapLines: Boolean,
    onWrapLinesToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CodeLanguage.entries.forEach { language ->
                FilterChip(
                    selected = language == selectedLanguage,
                    onClick = { onLanguageSelected(language) },
                    label = { Text(language.displayName) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (language) {
                                CodeLanguage.HTML -> Icons.Default.Code
                                CodeLanguage.CSS -> Icons.Default.Style
                                CodeLanguage.JAVASCRIPT -> Icons.Default.Javascript
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { onLineNumbersToggle(!showLineNumbers) }) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = "Toggle line numbers",
                    tint = if (showLineNumbers)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { onWrapLinesToggle(!wrapLines) }) {
                Icon(
                    imageVector = Icons.Default.WrapText,
                    contentDescription = "Toggle word wrap",
                    tint = if (wrapLines)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { onEditableToggle(!isEditable) }) {
                Icon(
                    imageVector = if (isEditable) Icons.Default.Lock else Icons.Default.Edit,
                    contentDescription = if (isEditable) "Lock" else "Edit",
                    tint = if (isEditable)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CodeEditor(
    code: String,
    language: CodeLanguage,
    isEditable: Boolean,
    showLineNumbers: Boolean,
    wrapLines: Boolean,
    onCodeChange: (String) -> Unit
) {
    val lines = remember(code) { code.split("\n") }
    val listState = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()

    var textFieldValue by remember(code) { mutableStateOf(TextFieldValue(code)) }

    LaunchedEffect(code) {
        if (textFieldValue.text != code) {
            textFieldValue = TextFieldValue(code)
        }
    }

    if (isEditable) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onCodeChange(it.text)
            },
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = CodeEditorColors.Text,
                lineHeight = 20.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .then(
                    if (!wrapLines) Modifier.horizontalScroll(horizontalScrollState)
                    else Modifier
                ),
            decorationBox = { innerTextField ->
                Row {
                    if (showLineNumbers) {
                        LineNumbers(
                            lineCount = textFieldValue.text.split("\n").size,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    innerTextField()
                }
            }
        )
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            if (showLineNumbers) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .background(CodeEditorColors.LineNumberBackground)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    itemsIndexed(lines) { index, _ ->
                        Text(
                            text = "${index + 1}",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = CodeEditorColors.LineNumber,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.padding(vertical = 0.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(CodeEditorColors.LineNumberBorder)
                )
            }

            SelectionContainer {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .then(
                            if (!wrapLines) Modifier.horizontalScroll(horizontalScrollState)
                            else Modifier
                        )
                ) {
                    itemsIndexed(lines) { _, line ->
                        Text(
                            text = buildAnnotatedString {
                                highlightCode(line, language)
                            },
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.padding(vertical = 0.dp),
                            softWrap = wrapLines
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LineNumbers(
    lineCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(CodeEditorColors.LineNumberBackground)
            .padding(end = 8.dp)
    ) {
        for (i in 1..lineCount) {
            Text(
                text = "$i",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = CodeEditorColors.LineNumber,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

private fun AnnotatedString.Builder.highlightCode(line: String, language: CodeLanguage) {
    when (language) {
        CodeLanguage.HTML -> highlightHtml(line)
        CodeLanguage.CSS -> highlightCss(line)
        CodeLanguage.JAVASCRIPT -> highlightJavaScript(line)
    }
}

private fun AnnotatedString.Builder.highlightHtml(line: String) {
    var i = 0
    while (i < line.length) {
        when {
            line.startsWith("<!--", i) -> {
                val endIndex = line.indexOf("-->", i)
                val comment = if (endIndex >= 0) {
                    line.substring(i, endIndex + 3)
                } else {
                    line.substring(i)
                }
                withStyle(SpanStyle(color = CodeEditorColors.Comment)) {
                    append(comment)
                }
                i += comment.length
            }
            line[i] == '<' -> {
                val tagEnd = line.indexOf('>', i)
                if (tagEnd >= 0) {
                    val tag = line.substring(i, tagEnd + 1)
                    highlightHtmlTag(tag)
                    i = tagEnd + 1
                } else {
                    withStyle(SpanStyle(color = CodeEditorColors.Tag)) {
                        append(line[i])
                    }
                    i++
                }
            }
            else -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) {
                    append(line[i])
                }
                i++
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightHtmlTag(tag: String) {
    val isClosingTag = tag.startsWith("</")
    val isSelfClosing = tag.endsWith("/>")

    withStyle(SpanStyle(color = CodeEditorColors.Tag)) {
        append(if (isClosingTag) "</" else "<")
    }

    val contentStart = if (isClosingTag) 2 else 1
    val contentEnd = when {
        isSelfClosing -> tag.length - 2
        else -> tag.length - 1
    }

    if (contentStart < contentEnd) {
        val content = tag.substring(contentStart, contentEnd)
        val parts = content.split(Regex("\\s+"), limit = 2)
        val tagName = parts[0]

        withStyle(SpanStyle(color = CodeEditorColors.TagName)) {
            append(tagName)
        }

        if (parts.size > 1) {
            val attrs = parts[1]
            highlightHtmlAttributes(attrs)
        }
    }

    withStyle(SpanStyle(color = CodeEditorColors.Tag)) {
        append(if (isSelfClosing) "/>" else ">")
    }
}

private fun AnnotatedString.Builder.highlightHtmlAttributes(attrs: String) {
    val attrPattern = Regex("""(\w+)(?:=("[^"]*"|'[^']*'|\S+))?""")
    var lastEnd = 0

    attrPattern.findAll(attrs).forEach { match ->
        if (match.range.first > lastEnd) {
            withStyle(SpanStyle(color = CodeEditorColors.Text)) {
                append(attrs.substring(lastEnd, match.range.first))
            }
        }

        val attrName = match.groupValues[1]
        val attrValue = match.groupValues.getOrNull(2) ?: ""

        withStyle(SpanStyle(color = CodeEditorColors.Attribute)) {
            append(attrName)
        }

        if (attrValue.isNotEmpty()) {
            withStyle(SpanStyle(color = CodeEditorColors.Text)) {
                append("=")
            }
            withStyle(SpanStyle(color = CodeEditorColors.String)) {
                append(attrValue)
            }
        }

        lastEnd = match.range.last + 1
    }

    if (lastEnd < attrs.length) {
        withStyle(SpanStyle(color = CodeEditorColors.Text)) {
            append(attrs.substring(lastEnd))
        }
    }
}

private fun AnnotatedString.Builder.highlightCss(line: String) {
    var i = 0
    while (i < line.length) {
        when {
            line.startsWith("/*", i) -> {
                val endIndex = line.indexOf("*/", i)
                val comment = if (endIndex >= 0) {
                    line.substring(i, endIndex + 2)
                } else {
                    line.substring(i)
                }
                withStyle(SpanStyle(color = CodeEditorColors.Comment)) {
                    append(comment)
                }
                i += comment.length
            }
            line[i] == ':' -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) {
                    append(':')
                }
                i++
                val valueStart = i
                while (i < line.length && line[i] != ';' && line[i] != '}') {
                    i++
                }
                if (i > valueStart) {
                    val value = line.substring(valueStart, i)
                    highlightCssValue(value)
                }
            }
            line[i] == '{' || line[i] == '}' || line[i] == ';' -> {
                withStyle(SpanStyle(color = CodeEditorColors.Punctuation)) {
                    append(line[i])
                }
                i++
            }
            line[i].isLetter() || line[i] == '-' || line[i] == '.' || line[i] == '#' || line[i] == '@' -> {
                val start = i
                while (i < line.length && (line[i].isLetterOrDigit() || line[i] in "-_.#@")) {
                    i++
                }
                val word = line.substring(start, i)

                val color = when {
                    word.startsWith("@") -> CodeEditorColors.Keyword
                    word.startsWith(".") || word.startsWith("#") -> CodeEditorColors.Selector
                    cssProperties.contains(word) -> CodeEditorColors.Property
                    else -> CodeEditorColors.Selector
                }

                withStyle(SpanStyle(color = color)) {
                    append(word)
                }
            }
            else -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) {
                    append(line[i])
                }
                i++
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightCssValue(value: String) {
    var i = 0
    while (i < value.length) {
        when {
            value[i].isWhitespace() -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) {
                    append(value[i])
                }
                i++
            }
            value[i] == '#' -> {
                val start = i
                i++
                while (i < value.length && (value[i].isLetterOrDigit())) {
                    i++
                }
                withStyle(SpanStyle(color = CodeEditorColors.Color)) {
                    append(value.substring(start, i))
                }
            }
            value[i].isDigit() -> {
                val start = i
                while (i < value.length && (value[i].isDigit() || value[i] == '.' || value[i].isLetter() || value[i] == '%')) {
                    i++
                }
                withStyle(SpanStyle(color = CodeEditorColors.Number)) {
                    append(value.substring(start, i))
                }
            }
            value[i] == '"' || value[i] == '\'' -> {
                val quote = value[i]
                val start = i
                i++
                while (i < value.length && value[i] != quote) {
                    i++
                }
                if (i < value.length) i++
                withStyle(SpanStyle(color = CodeEditorColors.String)) {
                    append(value.substring(start, i))
                }
            }
            else -> {
                val start = i
                while (i < value.length && !value[i].isWhitespace() && value[i] != ',' && value[i] != '(') {
                    i++
                }
                val word = value.substring(start, i)

                val color = when {
                    cssKeywords.contains(word.lowercase()) -> CodeEditorColors.Keyword
                    else -> CodeEditorColors.Value
                }

                withStyle(SpanStyle(color = color)) {
                    append(word)
                }
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightJavaScript(line: String) {
    var i = 0
    while (i < line.length) {
        when {
            line.startsWith("//", i) -> {
                withStyle(SpanStyle(color = CodeEditorColors.Comment)) {
                    append(line.substring(i))
                }
                return
            }
            line.startsWith("/*", i) -> {
                val endIndex = line.indexOf("*/", i)
                val comment = if (endIndex >= 0) {
                    line.substring(i, endIndex + 2)
                } else {
                    line.substring(i)
                }
                withStyle(SpanStyle(color = CodeEditorColors.Comment)) {
                    append(comment)
                }
                i += comment.length
            }
            line[i] == '"' || line[i] == '\'' || line[i] == '`' -> {
                val quote = line[i]
                val start = i
                i++
                while (i < line.length && line[i] != quote) {
                    if (line[i] == '\\' && i + 1 < line.length) {
                        i += 2
                    } else {
                        i++
                    }
                }
                if (i < line.length) i++
                withStyle(SpanStyle(color = CodeEditorColors.String)) {
                    append(line.substring(start, i))
                }
            }
            line[i].isDigit() -> {
                val start = i
                while (i < line.length && (line[i].isDigit() || line[i] == '.')) {
                    i++
                }
                withStyle(SpanStyle(color = CodeEditorColors.Number)) {
                    append(line.substring(start, i))
                }
            }
            line[i].isLetter() || line[i] == '_' || line[i] == '$' -> {
                val start = i
                while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_' || line[i] == '$')) {
                    i++
                }
                val word = line.substring(start, i)

                val color = when {
                    jsKeywords.contains(word) -> CodeEditorColors.Keyword
                    jsBuiltins.contains(word) -> CodeEditorColors.Builtin
                    else -> CodeEditorColors.Text
                }

                withStyle(SpanStyle(color = color)) {
                    append(word)
                }
            }
            line[i] in "{}[]();,." -> {
                withStyle(SpanStyle(color = CodeEditorColors.Punctuation)) {
                    append(line[i])
                }
                i++
            }
            line[i] in "+-*/%=<>!&|^~?" -> {
                withStyle(SpanStyle(color = CodeEditorColors.Operator)) {
                    append(line[i])
                }
                i++
            }
            else -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) {
                    append(line[i])
                }
                i++
            }
        }
    }
}

private val cssProperties = setOf(
    "display", "position", "top", "right", "bottom", "left", "z-index",
    "width", "height", "min-width", "min-height", "max-width", "max-height",
    "margin", "margin-top", "margin-right", "margin-bottom", "margin-left",
    "padding", "padding-top", "padding-right", "padding-bottom", "padding-left",
    "flex", "flex-direction", "justify-content", "align-items", "gap",
    "grid-template-columns", "grid-template-rows",
    "font-family", "font-size", "font-weight", "line-height", "text-align",
    "color", "background", "background-color", "border", "border-radius",
    "box-shadow", "opacity", "transform", "transition", "cursor", "overflow"
)

private val cssKeywords = setOf(
    "inherit", "initial", "unset", "none", "auto", "block", "inline",
    "flex", "grid", "absolute", "relative", "fixed", "sticky",
    "center", "left", "right", "top", "bottom", "start", "end",
    "row", "column", "wrap", "nowrap", "space-between", "space-around",
    "bold", "normal", "italic", "underline", "pointer", "default"
)

private val jsKeywords = setOf(
    "const", "let", "var", "function", "return", "if", "else", "for", "while",
    "do", "switch", "case", "break", "continue", "default", "throw", "try",
    "catch", "finally", "new", "delete", "typeof", "instanceof", "in", "of",
    "class", "extends", "super", "this", "static", "get", "set", "async",
    "await", "import", "export", "from", "as", "true", "false", "null", "undefined"
)

private val jsBuiltins = setOf(
    "console", "document", "window", "Math", "JSON", "Array", "Object",
    "String", "Number", "Boolean", "Date", "Promise", "setTimeout",
    "setInterval", "fetch", "addEventListener", "querySelector", "getElementById"
)

private object CodeEditorColors {
    val Background = Color(0xFF1E1E1E)
    val LineNumberBackground = Color(0xFF252526)
    val LineNumberBorder = Color(0xFF3C3C3C)
    val LineNumber = Color(0xFF858585)
    val Text = Color(0xFFD4D4D4)
    val Comment = Color(0xFF6A9955)
    val String = Color(0xFFCE9178)
    val Number = Color(0xFFB5CEA8)
    val Keyword = Color(0xFF569CD6)
    val Builtin = Color(0xFF4EC9B0)
    val Operator = Color(0xFFD4D4D4)
    val Punctuation = Color(0xFFD4D4D4)
    val Tag = Color(0xFF808080)
    val TagName = Color(0xFF569CD6)
    val Attribute = Color(0xFF9CDCFE)
    val Selector = Color(0xFFD7BA7D)
    val Property = Color(0xFF9CDCFE)
    val Value = Color(0xFFCE9178)
    val Color = Color(0xFFCE9178)
}
