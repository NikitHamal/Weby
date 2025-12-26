# CONTINUITY.md - Weby Android App

## Goal (incl. success criteria)
Completely redesign and rebuild Weby to be a production-grade, high-performance no-code web builder:
1. Fix OOM crash (128MB heap on low-end Itel device)
2. Implement proper Poppins font throughout
3. Redesign UI/UX - clean, compact, professional, user-friendly
4. Make visual editor canvas fully functional (pan, zoom, drag-drop, select)
5. Make code editor performant with virtualization
6. Implement working preview functionality
7. Remove animations from onboarding
8. Fix all non-functional features
9. Optimize for low-end devices (128MB heap)

Success: App runs without OOM on 128MB devices, all core features work, UI is clean/professional

## Constraints/Assumptions
- Max 500-1000 lines per file (modular)
- Production-grade only, no TODOs or stubs
- Poppins font required throughout
- Must work on devices with 128MB heap
- No commits, only file changes
- Material 3 + Jetpack Compose

## Key decisions
1. Canvas rendering: Use viewport culling (only render visible elements)
2. Text virtualization: Chunk-based rendering for code editor
3. Memory limits: Cap undo history to 20 items
4. Font loading: Add actual Poppins font files
5. State management: Use derivedStateOf and remember for expensive computations
6. Element indexing: HashMap cache for element lookup by ID
7. UI redesign: Simplified bottom nav with 4 essential tabs
8. Preview: WebView-based HTML/CSS/JS rendering

## State
- Done: Analysis complete, plan created
- Now: Phase 1 - Critical OOM fixes and performance optimization
- Next: Phase 2 - UI/UX redesign, Phase 3 - Feature completion

## Open questions
- None currently

## Working set (files/ids/commands)
- Phase 1 targets: EditorCanvas.kt, EditorViewModel.kt, Type.kt (fonts), CodeEditorPanel.kt
- Phase 2 targets: EditorScreen.kt, HomeScreen.kt, OnboardingScreen.kt, bottom nav redesign
- Phase 3 targets: Preview, Export, Undo/Redo implementation
