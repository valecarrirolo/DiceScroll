# Implementation Plan - DiceScroll Architecture Optimization

This plan outlines the steps to optimize the Jetpack Compose architecture, extract reusable components, and organize the file structure for better maintainability.

## User Review Required

> [!IMPORTANT]
> This plan proposes a significant package restructuring. We will move away from a flat `ui/main/` structure to a feature-based structure (`ui/screens/roller/`, `ui/screens/history/`, `ui/components/`). This will affect imports across the app. 

## Proposed Changes

### 1. Library and Import Updates
- Verify current `libs.versions.toml` and build configurations.
- Ensure all Kotlin and Compose imports are optimized and remove any unused ones.
- Run spotless or default formatting to maintain clean code.

### 2. Component Extraction
We will extract highly reusable generic UI components into a new `ui/components/` package.
- **`DieItem`**: Extract from `TrayPanel.kt` into `ui/components/DieItem.kt`. It's a fundamental unit that could be reused anywhere.
- **`DiceSelectionCard`**: Extract from `DiceComponents.kt` into `ui/components/DiceSelectionCard.kt`.
- **`ModifierChip`**: Extract from `CommonButtons.kt` into `ui/components/ModifierChip.kt`.
- **`RollButton`**: Extract from `RollControls.kt` into `ui/components/RollButton.kt`.

### 3. File Reorganization and Merging
We will restructure the presentation and data layers to be more cohesive, merging trivial files into related ones.

#### [NEW] `com.github.valecarrirolo.dicescroll.ui.components`
Create this package for the extracted components mentioned above.

#### [NEW] `com.github.valecarrirolo.dicescroll.ui.theme.MotionTokens`
- Move `MainMotionTokens.kt` here as `MotionTokens.kt` to consolidate theme-related constants.

#### [MODIFY] `com.github.valecarrirolo.dicescroll.ui.screens.roller` (Formerly `ui/main`)
- Move `MainScreen.kt`, `MainScreenViewModel.kt`, `TrayPanel.kt`, `DiceComponents.kt`, `RollControls.kt`, and `ModifierSheet.kt` here.
- **Merge**: Move `MainTab` enum directly into `MainScreen.kt` to reduce file clutter.

#### [MODIFY] `com.github.valecarrirolo.dicescroll.ui.screens.history`
- Move `HistoryScreen.kt` and `HistoryStats.kt` here.
- **Merge**: If `HistoryStats.kt` is small, merge it into `HistoryScreen.kt`.

#### [MODIFY] `com.github.valecarrirolo.dicescroll.data.repository`
- Move `DataRepository.kt`, `DefaultDataRepository.kt`, and `InMemoryDataRepository.kt` into a new `repository` subpackage inside `data`.

#### [DELETE]
- `ui/main/MainMotionTokens.kt` (moved)
- `ui/main/CommonButtons.kt` (moved/extracted)
- `ui/main/MainTab.kt` (merged)
- `ui/main/` directory will be completely removed.

### 4. Documentation Updates
- Update `walkthrough.md` to document the new architecture and components.
- Update `roadmap.md` to reflect that the architectural cleanup is complete.
- Update the project root's `implementation_plan.md` to match this artifact.

## Verification Plan

### Automated Tests
- Run `./gradlew testDebugUnitTest` to ensure the ViewModel logic remains intact after the move.
- Run `./gradlew spotlessApply` to ensure formatting.

### Manual Verification
- Build and run the app.
- Check that the UI looks identical (shake animations, rendering).
- Ensure the dice tray interaction, modifier sheet, and history tab all work as expected with the new component locations.
