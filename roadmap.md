# DiceScroll Roadmap

DiceScroll is a Jetpack Compose Android dice roller optimized for tabletop sessions: fast tray building, readable dice, reliable local history, and a rolling flow that stays usable during play.

This document has three sections:

* **Current Baseline**: the product and technical state the roadmap starts from.
* **Active Roadmap**: committed tasks to implement next.
* **Nice to Have Backlog**: detailed ideas that are not scheduled yet.

---

## Current Baseline

The app already has a Compose/Material 3 main screen, a ViewModel-driven tray, standard dice (`D4`, `D6`, `D8`, `D10`, `D12`, `D20`, `D100`), roll animation, modifiers, haptics, Room persistence, restored tray state, persisted roll history, snapshot-based reroll, a dedicated History tab, and a visible dice pool grid.

Completed milestone outcomes:

* `v0.2 - Tabletop Tray UX`: grid dice pool, tap-to-add, tap-tray-to-remove, responsive tray layout, basic selection feedback, and compact modifier access.
* `v0.3 - Local Persistence and History Tab`: Room storage, restored tray state, persisted history, timestamps, roll snapshots, and snapshot-based history reroll.

Remaining implementation gaps:

* `DiceType` is still an enum, so it cannot represent user-created dice without a future model change.
* The top title row, tabs, and system inset handling need alignment and edge-to-edge polish.
* The Roller/History tab change is currently too abrupt and needs a smooth moving indicator.
* A fresh app state should show an empty tray, not an unexplained default die.
* Tray motion should clearly show dice moving into and out of the active tray.
* The dice pool can still look clipped on small screens and needs final readability tuning.
* History rows need a cleaner layout for setup, values, total, timestamp, and reroll action.

---

## Active Roadmap

Only the milestones in this section are committed work.

### v0.4 - Interaction Polish and Layout Correctness

Goal: make the current roller and history experience feel deliberate, readable, and stable on common Android phone layouts before adding larger product features.

Tasks:

* [ ] Fix top layout and system bar handling.
  * Audit `MainActivity`, `Navigation`, and `MainScreen` inset usage.
  * Keep edge-to-edge transparent system bars enabled.
  * Remove double outer spacing caused by applying safe drawing padding and screen padding at the wrong level.
  * Align the title, clear action, and top tabs to the same horizontal rhythm.
  * Verify the status bar area blends with the app background instead of looking like a separate blocked strip.

* [ ] Animate the Roller/History tabs.
  * Replace the instant selected-state jump with a smooth moving indicator.
  * Keep both labels stable, readable, and tappable while the indicator moves.
  * Preserve the current top-tabs navigation model; do not restore bottom navigation.
  * Use Compose animation primitives that remain reliable across screen sizes.

* [ ] Make the empty tray state explicit.
  * If no tray has been saved, start with an empty tray.
  * Keep restored persisted tray state when saved data exists.
  * Remove implicit default `1D6` behavior from default UI state and in-memory test repository where it creates fake initial dice.
  * Keep the roll button disabled while the tray is empty.

* [ ] Make tray contents match selected dice exactly.
  * The tray must show only currently selected dice.
  * Tapping `D4` once in the pool shows exactly one `D4` in the tray.
  * Tapping `D4` again adds a second visible `D4`.
  * Tapping a die in the tray removes exactly one die of that type without affecting unrelated dice.
  * Preserve roll-result rendering after a roll and snapshot reroll behavior from history.

* [ ] Add targeted pool-to-tray and tray-to-pool motion.
  * Use robust Compose motion, not fragile pixel-perfect coordinate tracking.
  * Animate source pool-card feedback when adding a die.
  * Animate tray item insertion with stable keys, scale/fade, and `animateItem`.
  * Animate removal with clear reverse feedback so it feels like the die returns toward its source pool card.
  * Keep motion short enough for repeated tabletop use.

* [ ] Fix dice pool readability and bottom clipping.
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

* [ ] Redesign history rows.
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

## Nice to Have Backlog

Items in this section are not committed roadmap work. They should stay here until explicitly promoted into the Active Roadmap.

### Future: Custom Numeric Dice and Saved Trays

Why it matters: players can create reusable tabletop tools without needing RPG-sheet integration.

Potential work:

* Replace or extend the enum-based dice model with a flexible dice definition model.
* Support standard and user-created numeric dice through the same rolling pipeline.
* Add an expert custom dice section with name, face count, and color fields.
* Validate custom numeric dice before saving.
* Add saved tray presets for reusable dice combinations and modifiers.
* Keep RPG character sheets separate from saved tray presets.

Future design notes:

* `DiceDefinition`: stable id, display name, face count, color, standard/custom kind, and optional skin reference.
* `SavedTray`: stable id, name, selected dice definitions or snapshots, quantities, and modifier state.
* History must remain snapshot-based so old rolls stay correct after custom dice are edited or deleted.

### Future: Basic Aggregate Stats

Why it matters: players can review useful roll summaries from reliable local history.

Potential work:

* Show total roll count.
* Show rolls by die type.
* Show average total, minimum total, and maximum total.
* Support standard dice, future custom dice, modifiers, and empty history states.
* Keep this secondary to the main rolling workflow.

Future design notes:

* Stats should be computed from persisted history.
* Clearing history should reset aggregate stats.
* Advanced distribution charts and probability visualizations should remain separate from basic aggregates.

### Future: Dice Skins and Visual Customization

Why it matters: skins can make dice more personal once the data model is stable.

Potential work:

* Add per-die skins after `DiceDefinition` exists.
* Support textures, fonts, edge styles, and visual themes.
* Keep skins optional so numeric readability remains the default.
* Avoid coupling skins to roll correctness or history storage.

Future design notes:

* Skin references should be metadata on dice definitions, not part of roll result math.
* Old history entries should render correctly even if a skin is removed or changed.

### Future: Advanced Analysis

Why it matters: advanced analysis can help players inspect long-term rolling patterns.

Potential work:

* Add distribution charts.
* Add trend views and richer filters.
* Add fairness summaries after enough persisted history exists.
* Compare expected probability against observed history where mathematically useful.

Future design notes:

* Advanced analysis depends on stable persisted roll snapshots.
* Charting should not block the core roll/history workflow.

### Future: Sound, Wake Lock, and Quick Access

Why it matters: these improve the tabletop feel and reduce friction during sessions.

Potential work:

* Add sound profiles such as plastic on felt, metal on wood, or crystal chime.
* Add a screen wake lock option for long sessions.
* Add a home screen widget or Quick Settings tile for fast common rolls.

Future design notes:

* Sound should be optional and easy to disable.
* Wake lock should be explicit, visible, and reversible.
* Quick access should reuse the same rolling engine as the main app.

### Future: Connected Play and RPG Integration

Why it matters: remote or character-driven workflows can make DiceScroll useful beyond solo device rolling.

Potential work:

* Add shared rooms over local Wi-Fi or WebSockets.
* Share a virtual dice tray with other players.
* Add RPG character integration as a separate feature area from saved tray presets.
* Import or model simple stats for checks and saves.

Future design notes:

* Connected play should not be started until the local roll model and persistence layer are stable.
* RPG integration should not complicate the generic saved tray workflow.
