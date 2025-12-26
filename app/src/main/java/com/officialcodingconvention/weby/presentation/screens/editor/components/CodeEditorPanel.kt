package com.officialcodingconvention.weby.presentation.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            onWrapLinesToggle = { wrapLines = it }
        )

        HorizontalDivider()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CodeEditorColors.Background)
        ) {
            val currentCode = when (selectedLanguage) {
                CodeLanguage.HTML -> htmlCode
                CodeLanguage.CSS -> cssCode
                CodeLanguage.JAVASCRIPT -> jsCode
            }
            val onCodeChange: (String) -> Unit = when (selectedLanguage) {
                CodeLanguage.HTML -> onHtmlChange
                CodeLanguage.CSS -> onCssChange
                CodeLanguage.JAVASCRIPT -> onJsChange
            }

            CodeEditor(
                code = currentCode,
                language = selectedLanguage,
                isEditable = isEditable,
                showLineNumbers = showLineNumbers,
                wrapLines = wrapLines,
                onCodeChange = onCodeChange
            )
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
    onWrapLinesToggle: (Boolean) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
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
    val listState = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()

    // Split lines with memoization - only recompute when code changes
    val lines = remember(code) {
        if (code.isEmpty()) listOf("") else code.split("\n")
    }

    // Cache highlighted lines - compute asynchronously to avoid blocking UI
    var highlightedLines by remember { mutableStateOf<List<AnnotatedString>>(emptyList()) }

    LaunchedEffect(lines, language) {
        highlightedLines = withContext(Dispatchers.Default) {
            lines.map { line -> highlightLine(line, language) }
        }
    }

    // Use plain text while highlighting is computing
    val displayLines = if (highlightedLines.size == lines.size) {
        highlightedLines
    } else {
        lines.map { AnnotatedString(it) }
    }

    if (isEditable) {
        EditableCodeEditor(
            code = code,
            lineCount = lines.size,
            showLineNumbers = showLineNumbers,
            wrapLines = wrapLines,
            horizontalScrollState = horizontalScrollState,
            onCodeChange = onCodeChange
        )
    } else {
        ReadOnlyCodeEditor(
            lines = displayLines,
            showLineNumbers = showLineNumbers,
            wrapLines = wrapLines,
            listState = listState,
            horizontalScrollState = horizontalScrollState
        )
    }
}

@Composable
private fun EditableCodeEditor(
    code: String,
    lineCount: Int,
    showLineNumbers: Boolean,
    wrapLines: Boolean,
    horizontalScrollState: androidx.compose.foundation.ScrollState,
    onCodeChange: (String) -> Unit
) {
    var textFieldValue by remember(code) { mutableStateOf(TextFieldValue(code)) }

    LaunchedEffect(code) {
        if (textFieldValue.text != code) {
            textFieldValue = textFieldValue.copy(text = code)
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onCodeChange(newValue.text)
        },
        textStyle = codeTextStyle,
        cursorBrush = SolidColor(Color(0xFF569CD6)),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .then(if (!wrapLines) Modifier.horizontalScroll(horizontalScrollState) else Modifier),
        decorationBox = { innerTextField ->
            Row {
                if (showLineNumbers) {
                    VirtualizedLineNumbers(
                        lineCount = lineCount,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun ReadOnlyCodeEditor(
    lines: List<AnnotatedString>,
    showLineNumbers: Boolean,
    wrapLines: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    horizontalScrollState: androidx.compose.foundation.ScrollState
) {
    Row(modifier = Modifier.fillMaxSize()) {
        if (showLineNumbers) {
            // Line numbers column - synced with content scroll
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .background(CodeEditorColors.LineNumberBackground)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                items(
                    count = lines.size,
                    key = { it }
                ) { index ->
                    LineNumberText(lineNumber = index + 1)
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
                    .then(if (!wrapLines) Modifier.horizontalScroll(horizontalScrollState) else Modifier)
            ) {
                items(
                    count = lines.size,
                    key = { it }
                ) { index ->
                    Text(
                        text = lines[index],
                        style = codeTextStyle,
                        softWrap = wrapLines
                    )
                }
            }
        }
    }
}

@Composable
private fun VirtualizedLineNumbers(
    lineCount: Int,
    modifier: Modifier = Modifier
) {
    // Use LazyColumn for line numbers to virtualize large files
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
            .background(CodeEditorColors.LineNumberBackground)
            .padding(end = 8.dp),
        userScrollEnabled = false // Scroll is controlled by main content
    ) {
        items(
            count = lineCount,
            key = { it }
        ) { index ->
            LineNumberText(lineNumber = index + 1)
        }
    }
}

@Composable
private fun LineNumberText(lineNumber: Int) {
    Text(
        text = "$lineNumber",
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            color = CodeEditorColors.LineNumber,
            lineHeight = 20.sp
        )
    )
}

private val codeTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 13.sp,
    color = CodeEditorColors.Text,
    lineHeight = 20.sp
)

// Pre-compute highlighted line on background thread
private fun highlightLine(line: String, language: CodeLanguage): AnnotatedString {
    return buildAnnotatedString {
        when (language) {
            CodeLanguage.HTML -> highlightHtml(line)
            CodeLanguage.CSS -> highlightCss(line)
            CodeLanguage.JAVASCRIPT -> highlightJavaScript(line)
        }
    }
}

private fun AnnotatedString.Builder.highlightHtml(line: String) {
    var i = 0
    val len = line.length

    while (i < len) {
        when {
            i + 3 < len && line[i] == '<' && line[i + 1] == '!' && line[i + 2] == '-' && line[i + 3] == '-' -> {
                val endIndex = line.indexOf("-->", i)
                val end = if (endIndex >= 0) endIndex + 3 else len
                withStyle(SpanStyle(color = CodeEditorColors.Comment)) {
                    append(line.substring(i, end))
                }
                i = end
            }
            line[i] == '<' -> {
                val tagEnd = line.indexOf('>', i)
                if (tagEnd >= 0) {
                    highlightHtmlTag(line.substring(i, tagEnd + 1))
                    i = tagEnd + 1
                } else {
                    withStyle(SpanStyle(color = CodeEditorColors.Tag)) { append(line[i]) }
                    i++
                }
            }
            else -> {
                val nextTag = line.indexOf('<', i)
                val end = if (nextTag >= 0) nextTag else len
                withStyle(SpanStyle(color = CodeEditorColors.Text)) {
                    append(line.substring(i, end))
                }
                i = end
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightHtmlTag(tag: String) {
    val isClosing = tag.startsWith("</")
    val isSelfClosing = tag.endsWith("/>")

    withStyle(SpanStyle(color = CodeEditorColors.Tag)) {
        append(if (isClosing) "</" else "<")
    }

    val contentStart = if (isClosing) 2 else 1
    val contentEnd = if (isSelfClosing) tag.length - 2 else tag.length - 1

    if (contentStart < contentEnd) {
        val content = tag.substring(contentStart, contentEnd)
        val spaceIdx = content.indexOfFirst { it.isWhitespace() }

        if (spaceIdx > 0) {
            withStyle(SpanStyle(color = CodeEditorColors.TagName)) {
                append(content.substring(0, spaceIdx))
            }
            highlightHtmlAttributes(content.substring(spaceIdx))
        } else {
            withStyle(SpanStyle(color = CodeEditorColors.TagName)) {
                append(content)
            }
        }
    }

    withStyle(SpanStyle(color = CodeEditorColors.Tag)) {
        append(if (isSelfClosing) "/>" else ">")
    }
}

private fun AnnotatedString.Builder.highlightHtmlAttributes(attrs: String) {
    var i = 0
    val len = attrs.length

    while (i < len) {
        when {
            attrs[i].isWhitespace() -> {
                append(attrs[i])
                i++
            }
            attrs[i].isLetter() -> {
                val start = i
                while (i < len && (attrs[i].isLetterOrDigit() || attrs[i] == '-')) i++
                withStyle(SpanStyle(color = CodeEditorColors.Attribute)) {
                    append(attrs.substring(start, i))
                }
            }
            attrs[i] == '=' -> {
                append('=')
                i++
                // Skip whitespace
                while (i < len && attrs[i].isWhitespace()) {
                    append(attrs[i])
                    i++
                }
                // Parse value
                if (i < len && (attrs[i] == '"' || attrs[i] == '\'')) {
                    val quote = attrs[i]
                    val start = i
                    i++
                    while (i < len && attrs[i] != quote) i++
                    if (i < len) i++
                    withStyle(SpanStyle(color = CodeEditorColors.String)) {
                        append(attrs.substring(start, i))
                    }
                }
            }
            else -> {
                append(attrs[i])
                i++
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightCss(line: String) {
    var i = 0
    val len = line.length

    while (i < len) {
        when {
            i + 1 < len && line[i] == '/' && line[i + 1] == '*' -> {
                val endIdx = line.indexOf("*/", i)
                val end = if (endIdx >= 0) endIdx + 2 else len
                withStyle(SpanStyle(color = CodeEditorColors.Comment)) {
                    append(line.substring(i, end))
                }
                i = end
            }
            line[i] == ':' -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) { append(':') }
                i++
                val valueStart = i
                while (i < len && line[i] != ';' && line[i] != '}') i++
                if (i > valueStart) {
                    highlightCssValue(line.substring(valueStart, i))
                }
            }
            line[i] in "{};," -> {
                withStyle(SpanStyle(color = CodeEditorColors.Punctuation)) { append(line[i]) }
                i++
            }
            line[i].isLetter() || line[i] == '-' || line[i] == '.' || line[i] == '#' || line[i] == '@' -> {
                val start = i
                while (i < len && (line[i].isLetterOrDigit() || line[i] in "-_.#@")) i++
                val word = line.substring(start, i)
                val color = when {
                    word.startsWith("@") -> CodeEditorColors.Keyword
                    word.startsWith(".") || word.startsWith("#") -> CodeEditorColors.Selector
                    word in cssProperties -> CodeEditorColors.Property
                    else -> CodeEditorColors.Selector
                }
                withStyle(SpanStyle(color = color)) { append(word) }
            }
            else -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) { append(line[i]) }
                i++
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightCssValue(value: String) {
    var i = 0
    val len = value.length

    while (i < len) {
        when {
            value[i].isWhitespace() -> {
                append(value[i])
                i++
            }
            value[i] == '#' -> {
                val start = i++
                while (i < len && value[i].isLetterOrDigit()) i++
                withStyle(SpanStyle(color = CodeEditorColors.Color)) {
                    append(value.substring(start, i))
                }
            }
            value[i].isDigit() -> {
                val start = i
                while (i < len && (value[i].isDigit() || value[i] == '.' || value[i].isLetter() || value[i] == '%')) i++
                withStyle(SpanStyle(color = CodeEditorColors.Number)) {
                    append(value.substring(start, i))
                }
            }
            value[i] == '"' || value[i] == '\'' -> {
                val quote = value[i]
                val start = i++
                while (i < len && value[i] != quote) i++
                if (i < len) i++
                withStyle(SpanStyle(color = CodeEditorColors.String)) {
                    append(value.substring(start, i))
                }
            }
            value[i].isLetter() -> {
                val start = i
                while (i < len && (value[i].isLetterOrDigit() || value[i] == '-')) i++
                val word = value.substring(start, i)
                val color = if (word.lowercase() in cssKeywords) CodeEditorColors.Keyword else CodeEditorColors.Value
                withStyle(SpanStyle(color = color)) { append(word) }
            }
            else -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) { append(value[i]) }
                i++
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightJavaScript(line: String) {
    var i = 0
    val len = line.length

    while (i < len) {
        when {
            i + 1 < len && line[i] == '/' && line[i + 1] == '/' -> {
                withStyle(SpanStyle(color = CodeEditorColors.Comment)) {
                    append(line.substring(i))
                }
                return
            }
            i + 1 < len && line[i] == '/' && line[i + 1] == '*' -> {
                val endIdx = line.indexOf("*/", i)
                val end = if (endIdx >= 0) endIdx + 2 else len
                withStyle(SpanStyle(color = CodeEditorColors.Comment)) {
                    append(line.substring(i, end))
                }
                i = end
            }
            line[i] == '"' || line[i] == '\'' || line[i] == '`' -> {
                val quote = line[i]
                val start = i++
                while (i < len && line[i] != quote) {
                    if (line[i] == '\\' && i + 1 < len) i += 2
                    else i++
                }
                if (i < len) i++
                withStyle(SpanStyle(color = CodeEditorColors.String)) {
                    append(line.substring(start, i))
                }
            }
            line[i].isDigit() -> {
                val start = i
                while (i < len && (line[i].isDigit() || line[i] == '.')) i++
                withStyle(SpanStyle(color = CodeEditorColors.Number)) {
                    append(line.substring(start, i))
                }
            }
            line[i].isLetter() || line[i] == '_' || line[i] == '$' -> {
                val start = i
                while (i < len && (line[i].isLetterOrDigit() || line[i] == '_' || line[i] == '$')) i++
                val word = line.substring(start, i)
                val color = when (word) {
                    in jsKeywords -> CodeEditorColors.Keyword
                    in jsBuiltins -> CodeEditorColors.Builtin
                    else -> CodeEditorColors.Text
                }
                withStyle(SpanStyle(color = color)) { append(word) }
            }
            line[i] in "{}[]();,." -> {
                withStyle(SpanStyle(color = CodeEditorColors.Punctuation)) { append(line[i]) }
                i++
            }
            line[i] in "+-*/%=<>!&|^~?" -> {
                withStyle(SpanStyle(color = CodeEditorColors.Operator)) { append(line[i]) }
                i++
            }
            else -> {
                withStyle(SpanStyle(color = CodeEditorColors.Text)) { append(line[i]) }
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
