package com.officialcodingconvention.weby.presentation.screens.editor.components

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.officialcodingconvention.weby.domain.model.Breakpoint

@Composable
fun PreviewPanel(
    htmlCode: String,
    cssCode: String,
    jsCode: String,
    modifier: Modifier = Modifier
) {
    var selectedBreakpoint by remember { mutableStateOf(Breakpoint.MOBILE) }
    var refreshKey by remember { mutableIntStateOf(0) }

    val fullHtml = remember(htmlCode, cssCode, jsCode) {
        buildPreviewHtml(htmlCode, cssCode, jsCode)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        PreviewToolbar(
            selectedBreakpoint = selectedBreakpoint,
            onBreakpointSelected = { selectedBreakpoint = it },
            onRefresh = { refreshKey++ }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            val previewWidth = when (selectedBreakpoint) {
                Breakpoint.MOBILE -> 375.dp
                Breakpoint.TABLET -> 768.dp
                Breakpoint.DESKTOP -> 1024.dp
                Breakpoint.LARGE_DESKTOP -> 1200.dp
            }

            Surface(
                modifier = Modifier
                    .widthIn(max = previewWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp)),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                WebViewPreview(
                    htmlContent = fullHtml,
                    refreshKey = refreshKey,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun PreviewToolbar(
    selectedBreakpoint: Breakpoint,
    onBreakpointSelected: (Breakpoint) -> Unit,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedBreakpoint == Breakpoint.MOBILE,
                    onClick = { onBreakpointSelected(Breakpoint.MOBILE) },
                    label = { Text("Mobile") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                FilterChip(
                    selected = selectedBreakpoint == Breakpoint.TABLET,
                    onClick = { onBreakpointSelected(Breakpoint.TABLET) },
                    label = { Text("Tablet") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Tablet,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                FilterChip(
                    selected = selectedBreakpoint == Breakpoint.DESKTOP,
                    onClick = { onBreakpointSelected(Breakpoint.DESKTOP) },
                    label = { Text("Desktop") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DesktopWindows,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh preview"
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewPreview(
    htmlContent: String,
    refreshKey: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    key(refreshKey) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.WHITE)

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = false
                        allowContentAccess = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        cacheMode = WebSettings.LOAD_NO_CACHE
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return true
                        }
                    }
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(
                    null,
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            },
            modifier = modifier
        )
    }

    DisposableEffect(Unit) {
        onDispose { }
    }
}

private fun buildPreviewHtml(html: String, css: String, js: String): String {
    val processedHtml = if (html.contains("<!DOCTYPE") || html.contains("<html")) {
        html
    } else {
        """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                * {
                    box-sizing: border-box;
                    margin: 0;
                    padding: 0;
                }
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
                    line-height: 1.5;
                    color: #333;
                }
                $css
            </style>
        </head>
        <body>
            $html
            <script>
                $js
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    return processedHtml
}
