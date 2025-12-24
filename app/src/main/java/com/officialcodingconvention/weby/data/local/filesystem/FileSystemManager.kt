package com.officialcodingconvention.weby.data.local.filesystem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class FileSystemManager(private val context: Context) {

    private val projectsDir: File
        get() = File(context.filesDir, PROJECTS_DIR).also { it.mkdirs() }

    private val assetsDir: File
        get() = File(context.filesDir, ASSETS_DIR).also { it.mkdirs() }

    private val templatesDir: File
        get() = File(context.filesDir, TEMPLATES_DIR).also { it.mkdirs() }

    private val backupsDir: File
        get() = File(context.filesDir, BACKUPS_DIR).also { it.mkdirs() }

    private val exportsDir: File
        get() = File(context.getExternalFilesDir(null), EXPORTS_DIR).also { it.mkdirs() }

    private val cacheDir: File
        get() = File(context.cacheDir, CACHE_DIR).also { it.mkdirs() }

    fun getProjectDirectory(projectId: String): File {
        return File(projectsDir, projectId).also { it.mkdirs() }
    }

    fun getProjectAssetsDirectory(projectId: String): File {
        return File(getProjectDirectory(projectId), "assets").also { it.mkdirs() }
    }

    suspend fun saveAsset(
        projectId: String,
        fileName: String,
        inputStream: InputStream
    ): String = withContext(Dispatchers.IO) {
        val assetsDir = getProjectAssetsDirectory(projectId)
        val file = File(assetsDir, fileName)
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        file.absolutePath
    }

    suspend fun saveAssetFromUri(
        projectId: String,
        uri: Uri,
        fileName: String
    ): String = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            saveAsset(projectId, fileName, inputStream)
        } ?: throw IOException("Cannot open input stream for URI: $uri")
    }

    suspend fun saveOptimizedImage(
        projectId: String,
        uri: Uri,
        fileName: String,
        maxWidth: Int = 1920,
        maxHeight: Int = 1080,
        quality: Int = 85
    ): ImageSaveResult = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Cannot open input stream for URI: $uri")

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val scaledBitmap = scaleBitmap(originalBitmap, maxWidth, maxHeight)
        val assetsDir = getProjectAssetsDirectory(projectId)

        val webpFileName = fileName.substringBeforeLast(".") + ".webp"
        val file = File(assetsDir, webpFileName)

        file.outputStream().use { output ->
            scaledBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, output)
        }

        val originalSize = getFileSizeFromUri(uri)
        val newSize = file.length()

        originalBitmap.recycle()
        if (scaledBitmap != originalBitmap) scaledBitmap.recycle()

        ImageSaveResult(
            path = file.absolutePath,
            width = scaledBitmap.width,
            height = scaledBitmap.height,
            size = newSize,
            originalSize = originalSize
        )
    }

    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun getFileSizeFromUri(uri: Uri): Long {
        return context.contentResolver.openFileDescriptor(uri, "r")?.use {
            it.statSize
        } ?: 0L
    }

    suspend fun deleteAsset(path: String): Boolean = withContext(Dispatchers.IO) {
        File(path).delete()
    }

    suspend fun deleteProjectDirectory(projectId: String): Boolean = withContext(Dispatchers.IO) {
        getProjectDirectory(projectId).deleteRecursively()
    }

    suspend fun exportProject(
        projectId: String,
        projectName: String,
        htmlContent: Map<String, String>,
        cssContent: String,
        jsContent: String,
        assets: List<File>,
        singleFile: Boolean = false,
        minified: Boolean = false
    ): File = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val sanitizedName = projectName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val zipFile = File(exportsDir, "${sanitizedName}_$timestamp.zip")

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            if (singleFile) {
                val combinedHtml = buildSingleFileHtml(htmlContent, cssContent, jsContent, minified)
                addToZip(zos, "index.html", combinedHtml)
            } else {
                htmlContent.forEach { (pageName, content) ->
                    addToZip(zos, "$pageName.html", content)
                }

                if (cssContent.isNotBlank()) {
                    addToZip(zos, "css/styles.css", cssContent)
                }

                if (jsContent.isNotBlank()) {
                    addToZip(zos, "js/scripts.js", jsContent)
                }
            }

            assets.forEach { asset ->
                if (asset.exists()) {
                    addFileToZip(zos, "assets/${asset.name}", asset)
                }
            }
        }

        zipFile
    }

    private fun buildSingleFileHtml(
        htmlContent: Map<String, String>,
        cssContent: String,
        jsContent: String,
        minified: Boolean
    ): String {
        val mainHtml = htmlContent["index"] ?: htmlContent.values.firstOrNull() ?: ""
        val indent = if (minified) "" else "    "
        val newline = if (minified) "" else "\n"

        return buildString {
            append("<!DOCTYPE html>$newline")
            append("<html lang=\"en\">$newline")
            append("<head>$newline")
            append("${indent}<meta charset=\"UTF-8\">$newline")
            append("${indent}<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">$newline")
            if (cssContent.isNotBlank()) {
                append("${indent}<style>$newline")
                append(if (minified) cssContent.minifyCss() else cssContent)
                append("$newline${indent}</style>$newline")
            }
            append("</head>$newline")
            append("<body>$newline")
            append(mainHtml)
            if (jsContent.isNotBlank()) {
                append("$newline${indent}<script>$newline")
                append(if (minified) jsContent.minifyJs() else jsContent)
                append("$newline${indent}</script>$newline")
            }
            append("</body>$newline")
            append("</html>")
        }
    }

    private fun String.minifyCss(): String {
        return this
            .replace(Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("\\s+"), " ")
            .replace(Regex("\\s*([{}:;,>~+])\\s*"), "$1")
            .trim()
    }

    private fun String.minifyJs(): String {
        return this
            .replace(Regex("//.*?\n"), "\n")
            .replace(Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun addToZip(zos: ZipOutputStream, path: String, content: String) {
        val entry = ZipEntry(path)
        zos.putNextEntry(entry)
        zos.write(content.toByteArray(Charsets.UTF_8))
        zos.closeEntry()
    }

    private fun addFileToZip(zos: ZipOutputStream, path: String, file: File) {
        val entry = ZipEntry(path)
        zos.putNextEntry(entry)
        file.inputStream().use { input ->
            input.copyTo(zos)
        }
        zos.closeEntry()
    }

    suspend fun createBackup(projectId: String): File = withContext(Dispatchers.IO) {
        val projectDir = getProjectDirectory(projectId)
        val timestamp = System.currentTimeMillis()
        val backupFile = File(backupsDir, "${projectId}_$timestamp.zip")

        ZipOutputStream(BufferedOutputStream(FileOutputStream(backupFile))).use { zos ->
            projectDir.walkTopDown().forEach { file ->
                if (file.isFile) {
                    val relativePath = file.relativeTo(projectDir).path
                    addFileToZip(zos, relativePath, file)
                }
            }
        }

        backupFile
    }

    suspend fun restoreBackup(backupFile: File, projectId: String): Boolean = withContext(Dispatchers.IO) {
        val projectDir = getProjectDirectory(projectId)
        projectDir.deleteRecursively()
        projectDir.mkdirs()

        try {
            java.util.zip.ZipFile(backupFile).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val destFile = File(projectDir, entry.name)
                    if (entry.isDirectory) {
                        destFile.mkdirs()
                    } else {
                        destFile.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            destFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getStorageStats(): StorageStats = withContext(Dispatchers.IO) {
        val projectsSize = calculateDirectorySize(projectsDir)
        val cacheSize = calculateDirectorySize(cacheDir)
        val backupsSize = calculateDirectorySize(backupsDir)
        val exportsSize = calculateDirectorySize(exportsDir)

        StorageStats(
            projectsSize = projectsSize,
            cacheSize = cacheSize,
            backupsSize = backupsSize,
            exportsSize = exportsSize,
            totalSize = projectsSize + cacheSize + backupsSize + exportsSize
        )
    }

    private fun calculateDirectorySize(dir: File): Long {
        if (!dir.exists()) return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    suspend fun clearCache(): Long = withContext(Dispatchers.IO) {
        val size = calculateDirectorySize(cacheDir)
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
        size
    }

    suspend fun clearExports(): Long = withContext(Dispatchers.IO) {
        val size = calculateDirectorySize(exportsDir)
        exportsDir.deleteRecursively()
        exportsDir.mkdirs()
        size
    }

    companion object {
        private const val PROJECTS_DIR = "projects"
        private const val ASSETS_DIR = "assets"
        private const val TEMPLATES_DIR = "templates"
        private const val BACKUPS_DIR = "backups"
        private const val EXPORTS_DIR = "exports"
        private const val CACHE_DIR = "weby_cache"
    }
}

data class ImageSaveResult(
    val path: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val originalSize: Long
)

data class StorageStats(
    val projectsSize: Long,
    val cacheSize: Long,
    val backupsSize: Long,
    val exportsSize: Long,
    val totalSize: Long
)
