package com.officialcodingconvention.weby.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weby_preferences")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SHOW_GRID = booleanPreferencesKey("show_grid")
        val SNAP_TO_GRID = booleanPreferencesKey("snap_to_grid")
        val GRID_SIZE = intPreferencesKey("grid_size")
        val SHOW_RULERS = booleanPreferencesKey("show_rulers")
        val SHOW_GUIDES = booleanPreferencesKey("show_guides")
        val AUTO_SAVE_ENABLED = booleanPreferencesKey("auto_save_enabled")
        val AUTO_SAVE_INTERVAL = intPreferencesKey("auto_save_interval")
        val UNDO_HISTORY_LIMIT = intPreferencesKey("undo_history_limit")
        val DEFAULT_BREAKPOINT = stringPreferencesKey("default_breakpoint")
        val CODE_EDITOR_FONT_SIZE = intPreferencesKey("code_editor_font_size")
        val CODE_EDITOR_TAB_SIZE = intPreferencesKey("code_editor_tab_size")
        val CODE_EDITOR_LINE_NUMBERS = booleanPreferencesKey("code_editor_line_numbers")
        val CODE_EDITOR_WORD_WRAP = booleanPreferencesKey("code_editor_word_wrap")
        val CODE_EDITOR_AUTO_INDENT = booleanPreferencesKey("code_editor_auto_indent")
        val CODE_EDITOR_BRACKET_MATCHING = booleanPreferencesKey("code_editor_bracket_matching")
        val LAST_OPENED_PROJECT_ID = stringPreferencesKey("last_opened_project_id")
        val REDUCE_ANIMATIONS = booleanPreferencesKey("reduce_animations")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val EXPORT_FORMAT_MINIFIED = booleanPreferencesKey("export_format_minified")
        val EXPORT_SINGLE_FILE = booleanPreferencesKey("export_single_file")
    }

    private val preferencesFlow: Flow<Preferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    val userPreferences: Flow<UserPreferences> = preferencesFlow
        .map { preferences ->
            UserPreferences(
                onboardingCompleted = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
                themeMode = ThemeMode.valueOf(
                    preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
                ),
                showGrid = preferences[PreferencesKeys.SHOW_GRID] ?: true,
                snapToGrid = preferences[PreferencesKeys.SNAP_TO_GRID] ?: true,
                gridSize = preferences[PreferencesKeys.GRID_SIZE] ?: 8,
                showRulers = preferences[PreferencesKeys.SHOW_RULERS] ?: true,
                showGuides = preferences[PreferencesKeys.SHOW_GUIDES] ?: true,
                autoSaveEnabled = preferences[PreferencesKeys.AUTO_SAVE_ENABLED] ?: true,
                autoSaveInterval = preferences[PreferencesKeys.AUTO_SAVE_INTERVAL] ?: 30,
                undoHistoryLimit = preferences[PreferencesKeys.UNDO_HISTORY_LIMIT] ?: 100,
                defaultBreakpoint = preferences[PreferencesKeys.DEFAULT_BREAKPOINT] ?: "DESKTOP",
                codeEditorFontSize = preferences[PreferencesKeys.CODE_EDITOR_FONT_SIZE] ?: 14,
                codeEditorTabSize = preferences[PreferencesKeys.CODE_EDITOR_TAB_SIZE] ?: 2,
                codeEditorLineNumbers = preferences[PreferencesKeys.CODE_EDITOR_LINE_NUMBERS] ?: true,
                codeEditorWordWrap = preferences[PreferencesKeys.CODE_EDITOR_WORD_WRAP] ?: true,
                codeEditorAutoIndent = preferences[PreferencesKeys.CODE_EDITOR_AUTO_INDENT] ?: true,
                codeEditorBracketMatching = preferences[PreferencesKeys.CODE_EDITOR_BRACKET_MATCHING] ?: true,
                lastOpenedProjectId = preferences[PreferencesKeys.LAST_OPENED_PROJECT_ID],
                reduceAnimations = preferences[PreferencesKeys.REDUCE_ANIMATIONS] ?: false,
                hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
                exportFormatMinified = preferences[PreferencesKeys.EXPORT_FORMAT_MINIFIED] ?: false,
                exportSingleFile = preferences[PreferencesKeys.EXPORT_SINGLE_FILE] ?: false
            )
        }

    val isDarkTheme: Flow<Boolean> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] == ThemeMode.DARK.name
    }

    val autoSaveEnabled: Flow<Boolean> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.AUTO_SAVE_ENABLED] ?: true
    }

    val autoSaveInterval: Flow<Int> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.AUTO_SAVE_INTERVAL] ?: 30
    }

    val showGrid: Flow<Boolean> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.SHOW_GRID] ?: true
    }

    val snapToGrid: Flow<Boolean> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.SNAP_TO_GRID] ?: true
    }

    val gridSize: Flow<Int> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.GRID_SIZE] ?: 8
    }

    val codeEditorFontSize: Flow<Int> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.CODE_EDITOR_FONT_SIZE] ?: 14
    }

    val showLineNumbers: Flow<Boolean> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.CODE_EDITOR_LINE_NUMBERS] ?: true
    }

    val wordWrap: Flow<Boolean> = preferencesFlow.map { preferences ->
        preferences[PreferencesKeys.CODE_EDITOR_WORD_WRAP] ?: true
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun setShowGrid(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_GRID] = show
        }
    }

    suspend fun setSnapToGrid(snap: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SNAP_TO_GRID] = snap
        }
    }

    suspend fun setGridSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GRID_SIZE] = size
        }
    }

    suspend fun setShowRulers(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_RULERS] = show
        }
    }

    suspend fun setShowGuides(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_GUIDES] = show
        }
    }

    suspend fun setAutoSaveEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SAVE_ENABLED] = enabled
        }
    }

    suspend fun setAutoSaveInterval(interval: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SAVE_INTERVAL] = interval
        }
    }

    suspend fun setUndoHistoryLimit(limit: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.UNDO_HISTORY_LIMIT] = limit
        }
    }

    suspend fun setDefaultBreakpoint(breakpoint: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_BREAKPOINT] = breakpoint
        }
    }

    suspend fun setCodeEditorFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_EDITOR_FONT_SIZE] = size
        }
    }

    suspend fun setCodeEditorTabSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_EDITOR_TAB_SIZE] = size
        }
    }

    suspend fun setCodeEditorLineNumbers(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_EDITOR_LINE_NUMBERS] = show
        }
    }

    suspend fun setCodeEditorWordWrap(wrap: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_EDITOR_WORD_WRAP] = wrap
        }
    }

    suspend fun setCodeEditorAutoIndent(autoIndent: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_EDITOR_AUTO_INDENT] = autoIndent
        }
    }

    suspend fun setCodeEditorBracketMatching(match: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_EDITOR_BRACKET_MATCHING] = match
        }
    }

    suspend fun setLastOpenedProjectId(projectId: String?) {
        context.dataStore.edit { preferences ->
            if (projectId != null) {
                preferences[PreferencesKeys.LAST_OPENED_PROJECT_ID] = projectId
            } else {
                preferences.remove(PreferencesKeys.LAST_OPENED_PROJECT_ID)
            }
        }
    }

    suspend fun setReduceAnimations(reduce: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REDUCE_ANIMATIONS] = reduce
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun setExportFormatMinified(minified: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EXPORT_FORMAT_MINIFIED] = minified
        }
    }

    suspend fun setExportSingleFile(singleFile: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EXPORT_SINGLE_FILE] = singleFile
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        setThemeMode(if (isDark) ThemeMode.DARK else ThemeMode.LIGHT)
    }

    suspend fun setShowLineNumbers(show: Boolean) {
        setCodeEditorLineNumbers(show)
    }

    suspend fun setWordWrap(wrap: Boolean) {
        setCodeEditorWordWrap(wrap)
    }
}

data class UserPreferences(
    val onboardingCompleted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showGrid: Boolean = true,
    val snapToGrid: Boolean = true,
    val gridSize: Int = 8,
    val showRulers: Boolean = true,
    val showGuides: Boolean = true,
    val autoSaveEnabled: Boolean = true,
    val autoSaveInterval: Int = 30,
    val undoHistoryLimit: Int = 100,
    val defaultBreakpoint: String = "DESKTOP",
    val codeEditorFontSize: Int = 14,
    val codeEditorTabSize: Int = 2,
    val codeEditorLineNumbers: Boolean = true,
    val codeEditorWordWrap: Boolean = true,
    val codeEditorAutoIndent: Boolean = true,
    val codeEditorBracketMatching: Boolean = true,
    val lastOpenedProjectId: String? = null,
    val reduceAnimations: Boolean = false,
    val hapticFeedback: Boolean = true,
    val exportFormatMinified: Boolean = false,
    val exportSingleFile: Boolean = false
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}
