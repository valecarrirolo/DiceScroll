# DiceScroll Feature Roadmap

This document outlines the planned roadmap for **DiceScroll**, a modern, responsive dice-rolling application for Android built with Jetpack Compose.

---

## Phase 1: MVP Core Roller (Current Plan)
* **Standard Dice Set**: Support for standard polyhedral dice: D4, D6, D8, D10, D12, D20, D100.
* **Modern Material 3 UI**: Clean, glassmorphic UI with smooth animations, dark mode support, and an elegant color scheme.
* **Multi-Dice Selection**: Ability to roll multiple dice of the same or different types simultaneously.
* **Dynamic Roll Animations**: Visual "shaking/rolling" effect using Compose animations prior to showing final results.
* **Roll History**: Keep track of previous rolls during the session with quick-clear and re-roll options.
* **Haptic Feedback & Sound**: Subtle physical feedback when rolling and clicking.

---

## Phase 2: Modifiers & Customization
* **Dice Modifiers**: Add/subtract values to rolls (e.g., `2d6 + 4`, `d20 - 1`).
* **Custom Dice Creator**: Create dice with arbitrary face counts (e.g., D7, D30) or custom labels/emojis (e.g., Fate/Fudge dice, Yes/No decision dice).
* **Advantage / Disadvantage**: D&D 5e mechanics for rolling D20 with advantage or disadvantage.
* **Dice Sets / Presets**: Save custom combinations of dice (e.g., "Fireball: 8d6", "Sneak Attack: 1d20 + 3d6 + 3") for single-tap rolling.

---

## Phase 3: Immersive UI & Stats
* **Sound Board**: Custom sound profiles (plastic dice clacking on felt, heavy metal rolling on wood, crystal chime).
* **Dice Styling (Skins)**: Unlockable/selectable color themes, marble textures, glowing edges, and custom fonts for dice faces.
* **Statistics & Analysis**: View graphs of roll history, showing distribution charts (bell curves vs. flat distribution) and averages to prove fairness.
* **Screen Wake Lock**: Option to keep the screen active during tabletop sessions.

---

## Phase 4: Social & Connected Play
* **Shared Rooms**: Connect with other players over local Wi-Fi or WebSockets to share a virtual dice tray. Perfect for remote tabletop RPG sessions.
* **RPG Character Integration**: Import simple stats or D&D Beyond character sheets to automatically roll checks/saves with a single tap.
* **Widgets & Quick Tile**: Homescreen widget or Quick Settings tile to quickly roll a D20/D6 without opening the full app.
