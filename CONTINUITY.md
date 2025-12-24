# CONTINUITY.md - Weby Android App

## Goal (incl. success criteria)
Build "Weby" - a professional, production-grade no-code web builder Android app with:
- Material 3 Jetpack Compose UI with Poppins fonts
- Visual drag-drop editor with performant canvas (virtualization, Canvas drawing)
- Full code editor (HTML/CSS/JS) with syntax highlighting and large file support
- Project management, asset library, responsive design tools
- Clean code export (HTML/CSS/JS zip)
- Offline-first, local storage
- Crash handler with debug activity
- GitHub Actions CI/CD with APK signing
- Custom app icon with "W" + web/code elements
- Onboarding screens
- Package: com.officialcodingconvention.weby

Success: Fully functional, performant, production-grade app ready for release

## Constraints/Assumptions
- Kotlin + Jetpack Compose + Material 3 + Material Icons
- Poppins font family
- Max 500-1000 lines per file (modular architecture)
- Performance-first: virtualization, efficient rendering, lazy loading
- Offline-only, all data stored locally
- No TODOs or placeholder code - production-ready only
- Public keystore for open-source APK signing

## Key decisions
- Architecture: MVVM + Clean Architecture with modular packages
- Storage: Room database + file system for assets
- Canvas: Compose Canvas API with efficient recomposition strategies
- Code Editor: Custom virtualized text rendering with syntax highlighting
- Export: Zip generation with proper folder structure

## State
- Done: (none yet - starting fresh)
- Now: Creating Android project structure from scratch
- Next: Build core architecture, then UI screens, then editor functionality

## Open questions
- (none currently)

## Working set
- /workspace/repo-58da8839-0f27-4654-b644-9ef8cddc06c5/
