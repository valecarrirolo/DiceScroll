# DiceScroll Roadmap

## Vision

DiceScroll is a focused tabletop dice tray for Android: fast to use during play, easy to read at a glance, reliable across app restarts, and satisfying without becoming a full RPG character-sheet tool.

The main experience should feel like preparing and rolling dice on a real table:

* choose dice quickly from a clear pool;
* see only the dice currently in the tray;
* roll without distractions;
* review and reroll previous results safely;
* keep the interface readable on common phone screens.

## Guardrails

* Keep the rolling flow tray-first: the tray, dice pool, roll button, and history must support fast tabletop use.
* Prefer clear numeric readability over decorative dice visuals.
* Keep local persistence reliable before adding connected or account-based features.
* History must stay snapshot-based so old rolls and rerolls remain correct even if dice definitions change later.
* Do not turn DiceScroll into a full RPG sheet manager; saved dice trays and RPG character integrations are separate concepts.
* Avoid UI that hides core actions behind too many taps during play.
* Use motion to explain interactions, not as decoration.
* Keep implementation changes scoped to the current product surface unless a data-model change is explicitly promoted.

---

## Things We Want To Do

### Interaction Polish and Layout Correctness

Goal: make the current roller and history experience feel deliberate, readable, and stable on common Android phone layouts.

Tasks:

* [x] Fix top layout and system bar handling.
  * Audit `MainActivity`, `Navigation`, and `MainScreen` inset usage.
  * Keep edge-to-edge transparent system bars enabled.
  * Remove double outer spacing caused by applying safe drawing padding and screen padding at the wrong level.
  * Align the title, clear action, and top tabs to the same horizontal rhythm.
  * Verify the status bar area blends with the app background instead of looking like a separate blocked strip.

* [x] Animate the Roller/History tabs.
  * Replace the instant selected-state jump with a smooth moving indicator.
  * Keep both labels stable, readable, and tappable while the indicator moves.
  * Preserve the current top-tabs navigation model.
  * Use Compose animation primitives that remain reliable across screen sizes.

* [x] Make the empty tray state explicit.
  * If no tray has been saved, start with an empty tray.
  * Keep restored persisted tray state when saved data exists.
  * Remove implicit default `1D6` behavior from default UI state and in-memory test repository where it creates fake initial dice.
  * Keep the roll button disabled while the tray is empty.

* [x] Make tray contents match selected dice exactly.
  * The tray must show only currently selected dice.
  * Tapping `D4` once in the pool shows exactly one `D4` in the tray.
  * Tapping `D4` again adds a second visible `D4`.
  * Tapping a die in the tray removes exactly one die of that type without affecting unrelated dice.
  * Preserve roll-result rendering after a roll and snapshot reroll behavior from history.

* [x] Add targeted pool-to-tray and tray-to-pool motion.
  * Use robust Compose motion, not fragile pixel-perfect coordinate tracking.
  * Animate source pool-card feedback when adding a die.
  * Animate tray item insertion with stable keys, scale/fade, and `animateItem`.
  * Animate removal with clear reverse feedback so it feels like the die returns toward its source pool card.
  * Keep motion short enough for repeated tabletop use.

* [x] Fix dice pool readability and bottom clipping.
  * Review fixed heights, bottom padding, and system inset interactions.
  * Ensure the pool is not cut off at the bottom on common portrait layouts.
  * Keep all seven standard dice readable with responsive cells.
  * Avoid text truncation inside dice cards.
  * Preserve tap-to-add behavior and selected-count badges.

* [ ] Standardize app motion polish.
  * Define shared duration/easing constants for tab indicator, pool feedback, tray insertion/removal, roll button press, and history interactions.
  * Reduce abrupt jumps without making the app feel slow.
  * Keep haptic feedback aligned with meaningful roll state changes.
  * Avoid adding decorative motion that does not support the tabletop workflow.

* [x] Redesign history rows.
  * Reorganize each history item around dice setup, roll values, total, timestamp, and reroll action.
  * Make timestamp readable but visually secondary.
  * Keep total prominent and easy to scan.
  * Keep reroll discoverable without crowding the roll details.
  * Preserve clear-history and snapshot reroll flows.

Acceptance criteria:

* App opens with an empty tray when there is no saved tray.
* Title, top actions, and tabs align cleanly with transparent system/status bar behavior.
* Roller/History tab transition animates smoothly.
* Tray never shows unselected placeholder dice.
* Adding and removing dice has clear directional motion.
* Dice pool is not clipped at the bottom on common portrait layouts.
* History rows separate setup, values, total, time, and reroll clearly.
* Existing persistence, modifier, rolling, and history reroll behavior remain intact unless explicitly covered above.

Verification:

* Preview or manually inspect small and standard portrait layouts.
* Add or update Compose UI tests for empty tray, top tabs, dice add/remove, modifier access, and history layout smoke coverage.
* Run `scripts/verify.sh`.
* Compile android tests with `./gradlew assembleDebugAndroidTest` when UI tests change.

---

## Things We Are Not Doing Now

These ideas are intentionally out of the active roadmap. Some may become future work, but they should not distract from the current rolling experience.

### Custom Numeric Dice and Saved Tray Presets

Status: later, after the current tray interaction is stable.

Why not now:

* The current `DiceType` enum needs a flexible dice definition model before custom dice are clean.
* Custom dice should not be bolted onto the current UI until the standard dice flow is polished.
* Saved tray presets are useful, but they should build on a stable tray model and not replace the immediate tabletop flow.

Future notes:

* `DiceDefinition`: stable id, display name, face count, color, standard/custom kind, and optional skin reference.
* `SavedTray`: stable id, name, selected dice definitions or snapshots, quantities, and modifier state.

### Stats and Analysis

Status: later, after the main history UI is easier to scan.

Why not now:

* Basic stats depend on history being readable and trusted first.
* Advanced charts and probability analysis are secondary to fast rolling.
* Clearing history and future custom dice must have clear behavior before stats are promoted.

### Dice Skins and Visual Customization

Status: later, after a flexible dice definition model exists.

Why not now:

* Skins can make dice harder to read if introduced too early.
* Visual customization should remain metadata, not part of roll correctness.
* Old history entries must still render correctly if skins change or disappear.

### Sound, Wake Lock, Widgets, and Quick Access

Status: parked.

Why not now:

* Sound and wake lock are nice session features, but they do not fix current layout and interaction issues.
* Widgets and Quick Settings tiles should reuse a stable rolling engine and saved tray model.
* These features should remain optional and easy to disable.

### Connected Play and RPG Character Integration

Status: explicitly not part of the current product direction.

Why not now:

* Shared rooms, WebSockets, and multiplayer workflows add complexity before the local experience is fully polished.
* RPG character integration can turn the app into a sheet manager, which is outside the current focus.
* Character sheets should stay separate from generic saved tray presets.

### Bottom Navigation

Status: discarded for now.

Why not now:

* It took too much vertical space from the roller screen.
* Top tabs better support the tray-first layout.
* Reintroducing bottom navigation should require a clear need for more than two primary destinations.

### Pixel-Perfect Dice Travel Animation

Status: discarded for now.

Why not now:

* Exact source-to-target coordinate tracking is more fragile across screen sizes and layout changes.
* Targeted Compose motion is enough to communicate add/remove direction.
* The app should stay responsive during repeated tabletop interactions.
