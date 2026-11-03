# NOCTIS OS — Galaxy Edition (Phase 1–3 foundation)

Kotlin + Jetpack Compose launcher. Level 1 of the spec: a real Android HOME app.

## Implemented
- Home screen: minute-accurate clock/date, wallpaper-through scrim, persistent 4-slot dock
- App drawer: swipe up (or tap handle), live search by name/package, adaptive grid, loading/empty/error states
- Long-press an app (drawer) to pin/unpin; long-press a dock app to unpin; stored in DataStore
- Live refresh on app install/uninstall; Home press closes the drawer; Back closes the drawer
- NOCTIS colour tokens, edge-to-edge, TalkBack labels

## Not yet implemented (later phases)
Themes switcher, folders, widgets, notification companion, control center, settings app, Galaxy integrations, foldable layouts.
Reduced-motion handling is pending (Phase 10).

## Build
1. Open this folder in Android Studio (Ladybug or newer, JDK 17). It generates the Gradle wrapper on sync.
2. Run on a device/emulator, then press Home and choose NOCTIS as the default launcher.
CLI: `gradle wrapper && ./gradlew assembleDebug`

Status: written without access to a build environment, so it has not been compiled yet. Expect to fix small version/import issues on first sync.
