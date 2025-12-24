# CONTINUITY.md - Weby Android App

## Goal (incl. success criteria)
Transform Weby into a production-grade, high-performance no-code web builder with:
- Fix critical OOM crash (device with 128MB heap limit)
- Remove onboarding animations as requested
- Implement proper Poppins font integration (currently using SansSerif fallback)
- Redesign cluttered bottom nav and editor UI for better UX
- Make canvas pan/drag/touch interactions work properly
- Enable functional preview with WebView rendering
- Fix code sync between visual editor and code editor
- Optimize for low-end devices (virtualization, lazy loading, memory management)
- Clean, compact, professional UI/UX throughout

Success: Fully functional, performant app that works on 128MB heap devices

## Constraints/Assumptions
- Kotlin + Jetpack Compose + Material 3 + Material Icons
- Poppins font family (must be properly loaded from resources)
- Max 500-1000 lines per file (modular architecture)
- Performance-first: virtualization, efficient rendering, lazy loading
- Offline-only, all data stored locally
- No TODOs or placeholder code - production-ready only
- Target: Low-end devices (128MB heap like Itel A662LM)

## Key decisions
- Architecture: MVVM + Clean Architecture with modular packages
- Storage: Room database + file system for assets
- Canvas: Compose Canvas API with efficient recomposition strategies
- Code Editor: Virtualized line rendering (LazyColumn) with syntax highlighting
- Export: Zip generation with proper folder structure
- OOM Prevention: Aggressive memory management, string pooling, bitmap recycling

## State
- Done: Initial project structure exists
- Now: Analyzing OOM crash root cause and planning comprehensive fixes
- Next:
  1. Fix OOM crash with memory optimization
  2. Remove onboarding animations
  3. Integrate Poppins fonts properly
  4. Redesign editor UI/bottom nav
  5. Fix canvas interactions (pan/drag)
  6. Implement functional preview
  7. Fix code editor sync

## Open questions
- (none currently)

## Working set
- /workspace/repo-faa7b617-2ae3-46e4-8596-ed54ee05e3af/
- Key files: EditorScreen.kt, EditorCanvas.kt, CodeEditorPanel.kt, OnboardingScreen.kt, Type.kt
