package com.officialcodingconvention.weby.presentation.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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

enum class CodeLanguage(val displayName: String) {
    HTML("HTML"),
    CSS("CSS"),
    JAVASCRIPT("JS")
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

    Column(modifier = modifier.fillMaxSize()) {
        CompactToolbar(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it },
            isEditable = isEditable,
            onEditableToggle = { isEditable = it },
            showLineNumbers = showLineNumbers,
            onLineNumbersToggle = { showLineNumbers = it }
        )

        HorizontalDivider(thickness = 0.5.dp, color = EditorColors.Border)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EditorColors.Background)
        ) {
            val currentCode = when (selectedLanguage) {
                CodeLanguage.HTML -> htmlCode
                CodeLanguage.CSS -> cssCode
                CodeLanguage.JAVASCRIPT -> jsCode
            }
            val currentOnChange = when (selectedLanguage) {
                CodeLanguage.HTML -> onHtmlChange
                CodeLanguage.CSS -> onCssChange
                CodeLanguage.JAVASCRIPT -> onJsChange
            }

            MemoryEfficientCodeEditor(
                code = currentCode,
                language = selectedLanguage,
                isEditable = isEditable,
                showLineNumbers = showLineNumbers,
                onCodeChange = currentOnChange
            )
        }
    }
}

@Composable
private fun CompactToolbar(
    selectedLanguage: CodeLanguage,
    onLanguageSelected: (CodeLanguage) -> Unit,
    isEditable: Boolean,
    onEditableToggle: (Boolean) -> Unit,
    showLineNumbers: Boolean,
    onLineNumbersToggle: (Boolean) -> Unit
) {
    Surface(color = EditorColors.ToolbarBg) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CodeLanguage.entries.forEach { lang ->
                    val isSelected = lang == selectedLanguage
                    TextButton(
                        onClick = { onLanguageSelected(lang) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isSelected) EditorColors.Accent
                                else EditorColors.TextMuted
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = lang.displayName,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                IconButton(
                    onClick = { onLineNumbersToggle(!showLineNumbers) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatListNumbered,
                        contentDescription = "Lines",
                        modifier = Modifier.size(18.dp),
                        tint = if (showLineNumbers) EditorColors.Accent
                            else EditorColors.TextMuted
                    )
                }
                IconButton(
                    onClick = { onEditableToggle(!isEditable) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isEditable) Icons.Default.EditOff
                            else Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp),
                        tint = if (isEditable) EditorColors.Accent
                            else EditorColors.TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryEfficientCodeEditor(
    code: String,
    language: CodeLanguage,
    isEditable: Boolean,
    showLineNumbers: Boolean,
    onCodeChange: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()

    // Memory optimization: limit lines and truncate long lines
    val processedLines = remember(code) {
        processCodeForDisplay(code)
    }

    if (isEditable) {
        EditModeEditor(
            code = code,
            showLineNumbers = showLineNumbers,
            horizontalScrollState = horizontalScrollState,
            onCodeChange = onCodeChange
        )
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            if (showLineNumbers) {
                LineNumberGutter(
                    lineCount = processedLines.size,
                    listState = listState
                )
            }

            SelectionContainer {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalScrollState)
                        .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
                ) {
                    items(
                        count = processedLines.size,
                        key = { it }
                    ) { index ->
                        CodeLine(
                            line = processedLines[index],
                            language = language
                        )
                    }
                }
            }
        }
    }
}

private fun processCodeForDisplay(code: String): List<String> {
    if (code.isEmpty()) return listOf("")

    val lines = code.split('\n')
    val maxLines = 1000 // Limit for memory
    val maxLineLength = 500 // Truncate very long lines

    return if (lines.size > maxLines) {
        lines.take(maxLines).map { truncateLine(it, maxLineLength) } +
            "// ... ${lines.size - maxLines} more lines"
    } else {
        lines.map { truncateLine(it, maxLineLength) }
    }
}

private fun truncateLine(line: String, maxLength: Int): String {
    return if (line.length > maxLength) {
        line.substring(0, maxLength) + "..."
    } else line
}

@Composable
private fun EditModeEditor(
    code: String,
    showLineNumbers: Boolean,
    horizontalScrollState: androidx.compose.foundation.ScrollState,
    onCodeChange: (String) -> Unit
) {
    var textFieldValue by remember(code) { mutableStateOf(TextFieldValue(code)) }

    LaunchedEffect(code) {
        if (textFieldValue.text != code) {
            textFieldValue = TextFieldValue(code)
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onCodeChange(newValue.text)
        },
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = EditorColors.Text,
            lineHeight = 18.sp
        ),
        cursorBrush = SolidColor(EditorColors.Accent),
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(horizontalScrollState)
            .padding(8.dp),
        decorationBox = { innerTextField ->
            Row {
                if (showLineNumbers) {
                    val lineCount = remember(textFieldValue.text) {
                        textFieldValue.text.count { it == '\n' } + 1
                    }
                    SimpleLineNumbers(lineCount = minOf(lineCount, 1000))
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun SimpleLineNumbers(lineCount: Int) {
    Column(
        modifier = Modifier
            .background(EditorColors.GutterBg)
            .padding(end = 8.dp, start = 8.dp)
    ) {
        for (i in 1..lineCount) {
            Text(
                text = "$i",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = EditorColors.LineNum,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
private fun LineNumberGutter(
    lineCount: Int,
    listState: LazyListState
) {
    Box(
        modifier = Modifier
            .background(EditorColors.GutterBg)
            .width(44.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            userScrollEnabled = false
        ) {
            items(lineCount) { index ->
                Text(
                    text = "${index + 1}",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = EditorColors.LineNum,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(1.dp)
                .fillMaxHeight()
                .background(EditorColors.Border)
        )
    }
}

@Composable
private fun CodeLine(
    line: String,
    language: CodeLanguage
) {
    val highlighted = remember(line, language) {
        buildAnnotatedString {
            highlightSafe(line, language)
        }
    }

    Text(
        text = highlighted,
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 18.sp
        ),
        maxLines = 1,
        softWrap = false
    )
}

// Safe highlighting that prevents OOM
private fun AnnotatedString.Builder.highlightSafe(line: String, language: CodeLanguage) {
    if (line.isEmpty()) {
        append(" ")
        return
    }

    when (language) {
        CodeLanguage.HTML -> highlightHtmlSafe(line)
        CodeLanguage.CSS -> highlightCssSafe(line)
        CodeLanguage.JAVASCRIPT -> highlightJsSafe(line)
    }
}

private fun AnnotatedString.Builder.highlightHtmlSafe(line: String) {
    var i = 0
    val len = line.length

    while (i < len) {
        when {
            line.regionMatches(i, "<!--", 0, 4, ignoreCase = false) -> {
                val end = line.indexOf("-->", i).let { if (it < 0) len else it + 3 }
                withStyle(SpanStyle(color = EditorColors.Comment)) {
                    append(line.substring(i, minOf(end, len)))
                }
                i = minOf(end, len)
            }
            line[i] == '<' -> {
                val tagEnd = findTagEnd(line, i)
                if (tagEnd > i) {
                    highlightTag(line.substring(i, tagEnd))
                    i = tagEnd
                } else {
                    withStyle(SpanStyle(color = EditorColors.Tag)) { append('<') }
                    i++
                }
            }
            else -> {
                val next = line.indexOf('<', i).let { if (it < 0) len else it }
                withStyle(SpanStyle(color = EditorColors.Text)) {
                    append(line.substring(i, next))
                }
                i = next
            }
        }
    }
}

private fun findTagEnd(line: String, start: Int): Int {
    var i = start + 1
    val len = line.length
    var inQuote = false
    var quoteChar = ' '

    while (i < len && i < start + 200) { // Limit search
        val c = line[i]
        when {
            inQuote -> {
                if (c == quoteChar) inQuote = false
            }
            c == '"' || c == '\'' -> {
                inQuote = true
                quoteChar = c
            }
            c == '>' -> return i + 1
        }
        i++
    }
    return start
}

private fun AnnotatedString.Builder.highlightTag(tag: String) {
    val isClosing = tag.startsWith("</")
    val isSelfClosing = tag.endsWith("/>")

    withStyle(SpanStyle(color = EditorColors.Tag)) {
        append(if (isClosing) "</" else "<")
    }

    val contentStart = if (isClosing) 2 else 1
    val contentEnd = when {
        isSelfClosing -> tag.length - 2
        else -> tag.length - 1
    }

    if (contentStart < contentEnd) {
        val content = tag.substring(contentStart, contentEnd)
        val spaceIdx = content.indexOfFirst { it.isWhitespace() }

        if (spaceIdx > 0) {
            withStyle(SpanStyle(color = EditorColors.TagName)) {
                append(content.substring(0, spaceIdx))
            }
            highlightAttrs(content.substring(spaceIdx))
        } else {
            withStyle(SpanStyle(color = EditorColors.TagName)) {
                append(content)
            }
        }
    }

    withStyle(SpanStyle(color = EditorColors.Tag)) {
        append(if (isSelfClosing) "/>" else ">")
    }
}

private fun AnnotatedString.Builder.highlightAttrs(attrs: String) {
    var i = 0
    var inString = false
    var stringChar = ' '

    while (i < attrs.length) {
        val c = attrs[i]
        when {
            inString -> {
                withStyle(SpanStyle(color = EditorColors.String)) { append(c) }
                if (c == stringChar) inString = false
            }
            c == '"' || c == '\'' -> {
                inString = true
                stringChar = c
                withStyle(SpanStyle(color = EditorColors.String)) { append(c) }
            }
            c == '=' -> {
                withStyle(SpanStyle(color = EditorColors.Text)) { append(c) }
            }
            c.isLetter() || c == '-' -> {
                val start = i
                while (i < attrs.length && (attrs[i].isLetterOrDigit() || attrs[i] == '-')) i++
                withStyle(SpanStyle(color = EditorColors.Attr)) {
                    append(attrs.substring(start, i))
                }
                continue
            }
            else -> {
                withStyle(SpanStyle(color = EditorColors.Text)) { append(c) }
            }
        }
        i++
    }
}

private fun AnnotatedString.Builder.highlightCssSafe(line: String) {
    var i = 0
    val len = line.length

    while (i < len) {
        val c = line[i]
        when {
            line.regionMatches(i, "/*", 0, 2, ignoreCase = false) -> {
                val end = line.indexOf("*/", i).let { if (it < 0) len else it + 2 }
                withStyle(SpanStyle(color = EditorColors.Comment)) {
                    append(line.substring(i, end))
                }
                i = end
            }
            c == ':' -> {
                withStyle(SpanStyle(color = EditorColors.Punct)) { append(c) }
                i++
                val valStart = i
                while (i < len && line[i] != ';' && line[i] != '}') i++
                if (i > valStart) {
                    withStyle(SpanStyle(color = EditorColors.Value)) {
                        append(line.substring(valStart, i))
                    }
                }
            }
            c in "{};," -> {
                withStyle(SpanStyle(color = EditorColors.Punct)) { append(c) }
                i++
            }
            c.isLetter() || c == '-' || c == '.' || c == '#' || c == '@' -> {
                val start = i
                while (i < len && (line[i].isLetterOrDigit() || line[i] in "-_.#@")) i++
                val word = line.substring(start, i)
                val color = when {
                    word.startsWith("@") -> EditorColors.Keyword
                    word.startsWith(".") || word.startsWith("#") -> EditorColors.Selector
                    else -> EditorColors.Prop
                }
                withStyle(SpanStyle(color = color)) { append(word) }
            }
            else -> {
                withStyle(SpanStyle(color = EditorColors.Text)) { append(c) }
                i++
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightJsSafe(line: String) {
    var i = 0
    val len = line.length

    while (i < len) {
        val c = line[i]
        when {
            line.regionMatches(i, "//", 0, 2, ignoreCase = false) -> {
                withStyle(SpanStyle(color = EditorColors.Comment)) {
                    append(line.substring(i))
                }
                return
            }
            line.regionMatches(i, "/*", 0, 2, ignoreCase = false) -> {
                val end = line.indexOf("*/", i).let { if (it < 0) len else it + 2 }
                withStyle(SpanStyle(color = EditorColors.Comment)) {
                    append(line.substring(i, end))
                }
                i = end
            }
            c == '"' || c == '\'' || c == '`' -> {
                val quote = c
                val start = i
                i++
                while (i < len && line[i] != quote) {
                    if (line[i] == '\\' && i + 1 < len) i += 2 else i++
                }
                if (i < len) i++
                withStyle(SpanStyle(color = EditorColors.String)) {
                    append(line.substring(start, i))
                }
            }
            c.isDigit() -> {
                val start = i
                while (i < len && (line[i].isDigit() || line[i] == '.')) i++
                withStyle(SpanStyle(color = EditorColors.Number)) {
                    append(line.substring(start, i))
                }
            }
            c.isLetter() || c == '_' || c == '$' -> {
                val start = i
                while (i < len && (line[i].isLetterOrDigit() || line[i] in "_$")) i++
                val word = line.substring(start, i)
                val color = when (word) {
                    in JS_KEYWORDS -> EditorColors.Keyword
                    in JS_BUILTINS -> EditorColors.Builtin
                    else -> EditorColors.Text
                }
                withStyle(SpanStyle(color = color)) { append(word) }
            }
            c in "{}[]();,." -> {
                withStyle(SpanStyle(color = EditorColors.Punct)) { append(c) }
                i++
            }
            c in "+-*/%=<>!&|^~?" -> {
                withStyle(SpanStyle(color = EditorColors.Op)) { append(c) }
                i++
            }
            else -> {
                withStyle(SpanStyle(color = EditorColors.Text)) { append(c) }
                i++
            }
        }
    }
}

private val JS_KEYWORDS = setOf(
    "const", "let", "var", "function", "return", "if", "else", "for", "while",
    "do", "switch", "case", "break", "continue", "default", "throw", "try",
    "catch", "finally", "new", "delete", "typeof", "instanceof", "in", "of",
    "class", "extends", "super", "this", "static", "async", "await",
    "import", "export", "from", "as", "true", "false", "null", "undefined"
)

private val JS_BUILTINS = setOf(
    "console", "document", "window", "Math", "JSON", "Array", "Object",
    "String", "Number", "Boolean", "Date", "Promise", "setTimeout",
    "setInterval", "fetch", "addEventListener", "querySelector"
)

private object EditorColors {
    val Background = Color(0xFF1E1E1E)
    val ToolbarBg = Color(0xFF252526)
    val GutterBg = Color(0xFF1E1E1E)
    val Border = Color(0xFF3C3C3C)
    val LineNum = Color(0xFF6E7681)
    val Text = Color(0xFFD4D4D4)
    val TextMuted = Color(0xFF858585)
    val Accent = Color(0xFF569CD6)
    val Comment = Color(0xFF6A9955)
    val String = Color(0xFFCE9178)
    val Number = Color(0xFFB5CEA8)
    val Keyword = Color(0xFF569CD6)
    val Builtin = Color(0xFF4EC9B0)
    val Op = Color(0xFFD4D4D4)
    val Punct = Color(0xFFD4D4D4)
    val Tag = Color(0xFF808080)
    val TagName = Color(0xFF569CD6)
    val Attr = Color(0xFF9CDCFE)
    val Selector = Color(0xFFD7BA7D)
    val Prop = Color(0xFF9CDCFE)
    val Value = Color(0xFFCE9178)
}
