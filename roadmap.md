# DiceScroll Roadmap

DiceScroll is a Jetpack Compose Android dice roller optimized for tabletop sessions: fast tray building, readable dice, reliable local history, and a rolling flow that stays usable during play.

This document has two sections:

* **Active Roadmap**: committed tasks to implement next.
* **Nice to Have Backlog**: detailed ideas that are not scheduled yet.

---

## Current Baseline

The app already has a Compose/Material 3 main screen, a ViewModel-driven tray, standard dice (`D4`, `D6`, `D8`, `D10`, `D12`, `D20`, `D100`), roll animation, modifiers, haptics, and session history.

Important implementation gaps:

* `DiceType` is still an enum, so it cannot represent user-created dice without a future model change.
* `DefaultDataRepository` is in-memory, so tray state and roll history are lost after process death.
* Roll history is still shown in a modal bottom sheet instead of a dedicated navigation tab.
* The dice pool is still a horizontal row with add/remove buttons instead of a visible grid with direct selection.
* Roll history should store snapshots of dice data so old results and rerolls stay correct after dice definitions change in the future.

---

## Active Roadmap

Only the milestones in this section are committed work.

### v0.2 - Tabletop Tray UX

Goal: make the main rolling flow fast and readable during an active tabletop session.

Tasks:

* [x] Replace the horizontal dice pool with a grid that keeps all standard dice visible on typical phone screens.
* [x] Remove add/delete controls from dice pool cards.
* [x] Add dice by tapping a die in the pool.
* [x] Remove a specific die by tapping it in the tray.
* [x] Add a basic pool-to-tray selection animation.
* [x] Make the tray responsive so dice never overlap and remain visible through a grid or scrollable layout.
* [x] Move modifiers into a collapsible section that can be hidden, expanded, enabled, or disabled.

Acceptance criteria:

* All seven standard dice are visible in the pool without horizontal scrolling on common portrait layouts.
* Tapping a pool die adds exactly one matching die to the tray.
* Tapping a tray die removes that die without affecting unrelated dice.
* The roll button remains disabled for an empty tray.
* Large trays remain usable without visual overlap.
* Modifier controls do not dominate the default rolling screen.

### v0.3 - Local Persistence and History Tab

Goal: make session data durable and make history useful during play.

Tasks:

* [x] Introduce Room as the local persistence layer.
* [x] Persist roll history.
* [ ] Persist and restore the current tray state.
* [ ] Replace the history bottom sheet with a dedicated bottom navigation tab.
* [ ] Show full local date and time for each roll.
* [ ] Store roll history as immutable snapshots of the dice definitions used at roll time.
* [ ] Support rerolling from history by executing the stored snapshot, not a mutable current definition.

Conceptual interfaces:

* `RollSnapshot`: dice definitions at roll time, selected quantities, modifier state, roll results, and total.
* `RollHistoryEntry`: id, timestamp, snapshot, and display metadata for list rendering.
* `PersistedTray`: current selected dice and modifier state restored on app start.

Acceptance criteria:

* Restarting the app restores the current tray.
* Restarting the app keeps previous roll history.
* History displays readable timestamps with date and time.
* Reroll from history reproduces the same dice setup even if dice definitions are later edited or deleted.
* Clearing history does not delete saved tray state or future custom dice data.

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
