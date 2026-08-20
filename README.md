# Tiny Tools

Tiny Tools is a native Android utility app for Velvex Labs • spdedsec. It is intentionally offline-first, local-only, and organized around fast, focused tools rather than a dashboard or account system.

## Implemented in this build

The current debug build includes the black/charcoal/grey/orange foundation, Compose navigation, Home search, favorites persisted with Room, appearance preferences persisted with DataStore, system/light/dark theme choices, the geometric launcher mark, and the following local tools:

| Group | Tools | Status |
|---|---|---|
| Calculate | Calculator, Percentage, Tip, Split Bill | Implemented and unit-tested |
| Convert | Units, Time | Implemented and unit-tested |
| Dates | Age, Date Difference, Countdown | Implemented and unit-tested, including leap-day handling |
| Text | Count, Clean, Case | Implemented and unit-tested |
| Random | Random Picker, Decision Maker | Implemented and unit-tested |
| Other | QR Code generator, Color inspection | Implemented and unit-tested where framework-independent |

The app does not use accounts, cloud sync, AI, ads, telemetry, or network access. QR generation is performed locally with ZXing core. The QR screen currently generates codes from text or URLs; camera scanning, batch QR generation, and a richer save/share flow remain follow-up work rather than being silently represented as complete.

## Build

Use Java 17 and the Android SDK configured by `local.properties`:

```bash
gradle --no-daemon --max-workers=1 -Dorg.gradle.jvmargs='-Xmx768m' :app:assembleDebug
```

The generated APK is:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Validation

The final validation completed successfully with:

```bash
gradle --no-daemon --max-workers=1 -Dorg.gradle.jvmargs='-Xmx768m' :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

The project contains unit tests for arithmetic, percentage operations, conversions, duration parsing, age and date edge cases, text tools, random selection, and color calculations. Android lint completed successfully. The final debug APK was verified with Android package metadata and exposes `com.velvexlabs.tinytools.MainActivity` as the launcher activity with the Tiny Tools label and launcher icon.

## Product notes

The first implementation prioritizes a native, understandable foundation and a complete local initial toolset. Before production release, the next quality pass should exercise the APK on physical or emulated devices for rotation, process recreation, keyboard behavior, large-font settings, accessibility traversal, light/dark/system themes, and end-to-end camera or sharing flows. These checks require a running Android device or emulator, which was not available in the sandbox build environment.
