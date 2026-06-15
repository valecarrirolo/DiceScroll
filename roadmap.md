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
* Keep Compose entry points thin: screen wiring, layout, controls, previews, and data access should live in focused files.
* Name UI files by responsibility, not by implementation accident: shell, tabs, tray, pool, controls, history content, history items, and summaries should remain easy to find.
* Avoid catch-all composable files. Split a file when it starts owning unrelated layout, controls, formatting, and preview code.
* Keep screen entry points free from previews; store broad previews in preview files and leaf previews beside the component they exercise.
* Keep repository contracts, Room implementations, and test fakes separated so persistence changes are easy to review.
* Treat formatter, lint, and warning failures as technical debt; fix the cause instead of adding baselines or suppressions.
* Keep bitmap resources in density-aware or `drawable-nodpi` folders so the resource tree stays lint-clean.

---

## Things We Want To Do

No committed implementation milestone is active right now.

Use this section only for work that has been explicitly promoted into the next milestone. When a milestone ships, remove it from this active list so the roadmap stays focused on what still needs to be done.

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

### Advanced Stats and Analysis

Status: later. The compact history summary exists; deeper analysis is not scheduled.

Why not now:

* Advanced charts and probability analysis are secondary to fast rolling.
* Per-die breakdowns should wait until the compact stats summary proves useful in real sessions.
* Clearing history and future custom dice must have clear behavior before deeper analysis is promoted.

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
