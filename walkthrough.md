# DiceScroll Walkthrough

I have successfully created and configured the **DiceScroll** application. The app features a premium neon dark/light theme, modern MVVM architecture, dynamic shake animations, haptic feedback integration, and a comprehensive roll history sheet.

## What Was accomplished

1. **Project Setup & Package Configuration**:
   - Initialized Android Compose empty-activity template using the Android CLI tool.
   - Refactored the entire package structure from default `com.example.dicescroll` to the user-specified `com.github.valecarrirolo.dicescroll`.
   - Updated configuration files, manifests, and import paths to align with the new package name.
   - Added `material-icons-core` and `material-icons-extended` to `app/build.gradle.kts`.

2. **Core Domain & Data Layer**:
   - [DiceType.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/data/model/DiceType.kt): Created enum defining standard polyhedral dice types (D4, D6, D8, D10, D12, D20, D100) with their custom neon colors.
   - [RollResult.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/data/model/RollResult.kt): Model for roll outcomes, tracking timestamp, modifier, individual values, and total.
   - [DataRepository.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/data/repository/DataRepository.kt): Implemented reactive `MutableStateFlow` data storage to preserve roll history.

3. **MVVM Presentation Layer**:
   - Refactored architecture into `ui/components/`, `ui/screens/roller/`, and `ui/screens/history/` for better separation of concerns.
   - Extracted common UI elements (`DieItem`, `DiceSelectionCard`, `ModifierChip`, `RollButton`) to `ui/components/`.
   - [RollerViewModel.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/ui/screens/roller/RollerViewModel.kt):
     - Exposes state via unified `DiceUiState` Flow.
     - Implements roll logic with a timed coroutine loop to simulate rolling clatter animations.
     - Supports adding/removing multiple dice to the tray.
     - Handles modifiers and clears tray or history.

4. **UI styling & Theme**:
   - [Color.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/theme/Color.kt) & [Theme.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/theme/Theme.kt): Implemented a premium custom theme default featuring midnight blues, neon teals, and vibrant purples.

  - [MainScreen.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/ui/screens/roller/MainScreen.kt): Custom Compose UI refactored into a stateless `MainScreenContent` and stateful wrapper:
      - TopAppBar showing clear tray/history triggers.
      - A glassmorphic tray with border gradients.
      - Rolling shake animations on the dice elements.
      - Haptic feedback pulses when the roll starts and finishes.
      - A slider dock at the bottom to easily increment/decrement dice in the pool.
      - Modal bottom sheet displaying history with precise timestamps and individual dice values.
      - **Relocated Previews**: Moved all `@Preview` blocks directly above the corresponding `@Composable` functions they reference.
      - **Entrance Animations**: Implemented stable item key mappings (`TrayDieInstance`) and integrated `Modifier.animateItem()` in the lazy tray grid so that adding/removing dice is smoothly animated.

---

## Verification Results

### Automated Unit Tests
All unit tests compile and pass successfully:
- `uiState_initialState_hasOneD6` - Verifies the app boots with a default 1 D6 configuration.
- `addDie_increasesCount` - Verifies clicking/adding dice correctly updates state.
- `removeDie_decreasesCount` - Verifies removing dice updates bounds.
- `rollTray_generatesResultsAndLogsHistory` - Verifies roll animation timing, final results generation, and history insertion.

Run command used:
```bash
./gradlew testDebugUnitTest
```
**Outcome**: `BUILD SUCCESSFUL` with `4 tests completed, 0 failed`.

### Build Compilation
Run command used:
```bash
./gradlew assembleDebug
```
**Outcome**: `BUILD SUCCESSFUL`.
