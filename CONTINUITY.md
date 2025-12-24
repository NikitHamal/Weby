# CONTINUITY.md - Weby Android App

## Goal (incl. success criteria)
Fix build errors and simplify workflow:
1. Fix Offset type mismatch in EditorScreen.kt and EditorViewModel.kt - DONE
2. Fix unresolved 'tag' reference in ComponentPanel.kt - DONE (changed to defaultTag)
3. Fix centerX/centerY references in EditorCanvas.kt - DONE (changed to center.x/center.y)
4. Fix all unresolved references in SettingsScreen.kt - DONE (added flows and methods to PreferencesManager)
5. Fix Room schema export warning in WebyDatabase.kt - DONE (set exportSchema = false)
6. Simplify workflow to release-only - DONE (removed lint, test, debug jobs)

Success: Build should compile without errors, workflow only produces release APK

## Constraints/Assumptions
- Production-grade fixes only, no TODOs
- Max 500-1000 lines per file (modular architecture)
- No commits, only file changes

## Key decisions
- Removed custom `Offset` data class from domain.model.EditorState and use `androidx.compose.ui.geometry.Offset` instead
- Use `Rect.center.x` and `Rect.center.y` instead of non-existent `centerX`/`centerY` properties
- Use `component.defaultTag` instead of `component.tag` in ComponentPanel
- Added convenience flows to PreferencesManager: isDarkTheme, autoSaveEnabled, autoSaveInterval, showGrid, snapToGrid, gridSize, codeEditorFontSize, showLineNumbers, wordWrap
- Added convenience methods to PreferencesManager: setDarkTheme, setShowLineNumbers, setWordWrap
- Set `exportSchema = false` in WebyDatabase to silence Room warning
- Simplified workflow to single `build-release` job + `release` job (removed lint, test, debug)

## State
- Done: All fixes applied
- Now: Complete
- Next: N/A

## Open questions
- (none)

## Working set (files changed)
- `app/src/main/java/com/officialcodingconvention/weby/domain/model/EditorState.kt` - Replaced custom Offset with Compose Offset
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/editor/components/EditorCanvas.kt` - Fixed centerX/centerY to center.x/center.y
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/editor/components/ComponentPanel.kt` - Fixed tag to defaultTag
- `app/src/main/java/com/officialcodingconvention/weby/data/local/database/WebyDatabase.kt` - Set exportSchema = false
- `app/src/main/java/com/officialcodingconvention/weby/data/local/datastore/PreferencesManager.kt` - Added flows and methods
- `.github/workflows/android.yml` - Simplified to release-only
