# Implementation Plan - DiceScroll Android Application

Create a modern, feature-rich, and beautiful Android dice-rolling application using Jetpack Compose, Material 3, and ViewModel. The app will support multiple dice types (D4, D6, D8, D10, D12, D20, D100) with beautiful rolling animations and history logging.

## User Review Required

> [!IMPORTANT]
> The app will be generated using the standard `android create empty-activity` template. By default, this template sets up a standard Material 3 Compose theme. We will migrate it to `com.github.valecarrirolo.dicescroll` and enhance this theme to introduce a premium dark/light mode with custom gradients and micro-animations.

> [!TIP]
> We will implement a custom shake-to-roll animation using Jetpack Compose's transition and graphics APIs (like canvas or scale/rotation transitions) to provide a visually stunning experience.

## Open Questions

No blocker open questions, but we will assume:
1. Target package name: `com.github.valecarrirolo.dicescroll` (we will rename/adjust the generated package structure if the template generates a default package name like `com.example.dicescroll`).
2. MinSdk: API level 26 (Android 8.0) or higher, to support modern Compose APIs and haptic feedback.

---

## Proposed Changes

We will first generate the project skeleton, then build the business logic, and finally craft the premium user interface.

### Project Setup & Configuration

We will initialize the app using the Android CLI tool.

#### [NEW] [Project Files](file:///Users/nemsi/AndroidStudioProjects/DiceScroll)
Running the template creation command:
`android create empty-activity --name="DiceScroll" --output=.`

---

### Core Domain & Data Layer

We will define model classes for the dice types and rolling logic.

#### [NEW] [DiceType.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/data/model/DiceType.kt)
Define enum/sealed class for the supported dice (D4, D6, D8, D10, D12, D20, D100) with corresponding names, maximum values, and vector resource references.

#### [NEW] [RollResult.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/data/model/RollResult.kt)
Define a data class representing a single roll or session roll event (timestamp, dice rolled, individual values, modifiers, and final total).

---

### Presentation Layer (MVVM)

We will implement the ViewModel to manage state flows for user interactions.

#### [NEW] [DiceViewModel.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/ui/viewmodel/DiceViewModel.kt)
* Holds a StateFlow of `DiceUiState` containing:
  * Selected dice configuration (e.g., how many of which dice to roll).
  * Current roll results and animation state (isRolling, current temporary values).
  * Roll history log.
* Functions for:
  * Rolling the selected dice (triggering animation state then delivering results).
  * Modifying dice counts.
  * Adding/removing dice from the tray.
  * Clearing the roll tray or history.

---

### User Interface (Jetpack Compose & Material 3)

We will create a premium, visually stunning user interface with smooth transitions, custom colors, and dark-mode defaults.

#### [NEW] [Color.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/ui/theme/Color.kt)
Introduce a custom color palette featuring deep midnight blues, vibrant neon purples, and electric teals for a modern, gaming-oriented dark aesthetic.

#### [NEW] [Theme.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/ui/theme/Theme.kt)
Provide custom Material 3 Dark and Light color schemes using the colors defined above.

#### [NEW] [MainActivity.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/MainActivity.kt)
Update the main entry point to set up the ViewModel, inject haptic feedback, and render the overall layout.

#### [NEW] [MainScreen.kt](file:///Users/nemsi/AndroidStudioProjects/DiceScroll/app/src/main/java/com/github/valecarrirolo/dicescroll/ui/screens/MainScreen.kt)
Implement the core UI containing:
* **Header**: Title with current modifier total.
* **Tray Area**: Large, responsive grid/tray showing active dice. Highlighting the results when the roll finishes.
* **Roll Button**: Prominent neon-styled rolling button that scales and animates on click.
* **Dice Selector**: A horizontal slider/dock at the bottom showing D4, D6, D8, etc., with badge counts.
* **History Bottom Sheet**: Slide-up sheet to review the history of rolls with clean typography and time tags.

---

## Verification Plan

### Automated Tests
* We will verify compilation by building the project:
  * `./gradlew assembleDebug`
* We will run a journey test using the android CLI tool if applicable once the emulator is running:
  * `android run`

### Manual Verification
* Deploy and run the app on the emulator or device:
  * Select different dice types (e.g., D6, D20).
  * Tap roll, verify that numbers cycle rapidly during the animation phase.
  * Verify that results display clearly once finished.
  * Verify haptic feedback/haptic triggers on roll start and completion.
  * Open the history panel to verify previous roll outcomes are stored and clearable.
