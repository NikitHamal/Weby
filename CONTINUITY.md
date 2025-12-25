# CONTINUITY.md - Weby Android App

## Goal (incl. success criteria)
Build a production-grade, fully functional no-code web builder Android app that:
1. Launches without blank screen issues
2. Provides a complete visual drag-and-drop editor
3. Supports HTML/CSS/JS code editing with syntax highlighting
4. Exports clean, production-ready code
5. Is performant even on low-end devices

## Constraints/Assumptions
- Production-grade code only, no TODOs or placeholders
- Max 500-1000 lines per file (modular architecture)
- No git commits (Tembo platform handles this)
- Material 3 + Jetpack Compose + Material Icons
- Package: com.officialcodingconvention.weby
- Offline-first, all data stored locally on device

## Key decisions
- Use `UserPreferences()` as initial value in MainActivity to prevent blank screen
- Use `androidx.compose.ui.geometry.Offset` for all offset operations
- Use `Rect.center.x/y` for center coordinates
- Use `component.defaultTag` in ComponentPanel
- Set `exportSchema = false` in Room database
- CrashActivity runs in separate process for crash handling
- Canvas-based rendering for editor performance
- LazyColumn/LazyVerticalGrid for virtualized lists

## State

### Done:
- Fixed blank screen issue by providing default UserPreferences() initial value
- Fixed WebyLogo animation state initialization
- Verified CrashActivity and CrashHandler implementation
- Verified comprehensive editor functionality:
  - EditorCanvas with zoom, pan, element selection, resize handles
  - ComponentPanel with categorized components and search
  - LayerPanel with hierarchy, visibility, lock controls
  - StylePanel with layout, spacing, typography, background, border, effects
  - CodeEditorPanel with HTML/CSS/JS syntax highlighting and virtualization
  - CodeGenerator producing proper HTML, CSS, and JS output
- Verified navigation flow (Splash -> Onboarding/Home -> Editor)
- Verified all screens are properly implemented:
  - SplashScreen with animations
  - OnboardingScreen with multi-page introduction
  - HomeScreen with project list and templates
  - EditorScreen with full editing capabilities
  - SettingsScreen (referenced in navigation)

### Now:
- Session complete. All core issues fixed and functionality verified.

### Next (for future sessions):
- Add more component templates
- Implement asset management (images, fonts, icons)
- Add animation/interaction builder
- Implement project import/export functionality
- Add more responsive preview options
- Performance profiling and optimization

## Open questions
- (none currently)

## Working set (files/ids/commands)
Key files modified/verified:
- `app/src/main/java/com/officialcodingconvention/weby/MainActivity.kt` - Fixed initial preferences
- `app/src/main/java/com/officialcodingconvention/weby/presentation/components/common/WebyLogo.kt` - Fixed animation state
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/splash/SplashScreen.kt`
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/editor/EditorScreen.kt`
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/editor/components/EditorCanvas.kt`
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/editor/components/CodeEditorPanel.kt`
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/editor/components/ComponentPanel.kt`
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/editor/components/LayerPanel.kt`
- `app/src/main/java/com/officialcodingconvention/weby/presentation/screens/editor/components/StylePanel.kt`
- `app/src/main/java/com/officialcodingconvention/weby/CrashActivity.kt`
- `app/src/main/java/com/officialcodingconvention/weby/core/crash/CrashHandler.kt`

## Architecture Overview

### Layers
1. **Presentation** - Compose UI screens and components
2. **Domain** - Business models (Project, Page, WebElement, ElementStyles)
3. **Data** - Room database, DataStore preferences, FileSystem

### Key Components
- **EditorCanvas**: Canvas-based rendering for performance, handles zoom/pan/selection
- **CodeGenerator**: Converts WebElement tree to HTML/CSS/JS
- **PreferencesManager**: DataStore-backed user settings
- **CrashHandler**: Global exception handler with separate process CrashActivity
