# SimulationForm — Lumiform Android Code Challenge

An Android app that downloads a hierarchical inspection form from a remote JSON endpoint and renders it as a nested tree of **pages → sections (any depth) → questions** (text, image and choice).

## Features

- **Hierarchical rendering.** Pages, nested sections and questions are indented and connected by colored guide lines. Font sizes follow the hierarchy: page 26sp, then sections that shrink by nesting level (22sp → 16sp min), then questions at 14sp.
- **Image questions.** Each shows a thumbnail. Tapping it opens a full-screen viewer with the image title, pinch-to-zoom and pan.
- **Choice questions.** They honour `multiple_selection`: multi-select toggles each response independently, while single-select replaces the previous answer. Tapping a selected response clears it. Scores are shown when present, since `score` may be `null` or missing.
- **Offline support.** The form, the user's answers and the images are cached locally (Room + Coil disk cache), so the app works fully offline after the first successful load.
- **Network failure handling**:
  - Typed errors: no internet, timeout, server (HTTP code), malformed data and unknown.
  - Automatic retry with exponential backoff for transient failures (I/O, timeouts, 5xx, 408, 429).
  - A full-screen error with **Retry** when there is no cached data.
  - A non-blocking offline / stale-data banner with **Retry** when cached data is shown.
  - Pull-to-refresh.
  - Automatic refresh as soon as connectivity is restored.

## Architecture

The app uses Clean Architecture with three layers, a **single Activity**, and **MVI** (unidirectional data flow).

```
┌──────────────────────────── presentation ────────────────────────────┐
│  Compose UI ──Intent──▶ MviViewModel ──State──▶ Compose UI          │
│                              │  └─Effect (one-off: navigation)       │
└──────────────────────────────┼───────────────────────────────────────┘
                               ▼
┌────────────────────────────── domain ────────────────────────────────┐
│  Use cases ──▶ FormRepository / NetworkMonitor (interfaces)          │
│  Pure Kotlin models: Page, Section, Text/Image/ChoiceQuestion, …     │
└──────────────────────────────▲───────────────────────────────────────┘
                               │ implements
┌────────────────────────────── data ──────────────────────────────────┐
│  FormRepositoryImpl                                                  │
│   ├─ Retrofit FormApi ──(refresh)──▶ Room FormDatabase               │
│   └─ observe* ◀──────────── Room (single source of truth)            │
│  ConnectivityNetworkMonitor (ConnectivityManager callbacks)          │
└──────────────────────────────────────────────────────────────────────┘
```

- **Domain**:
  - Framework-free models and repository contracts.
  - One use case per action.
  - The selection rule lives in `ToggleResponseUseCase.toggle`, a pure function.
- **Data** (offline-first):
  - The UI only observes Room. The network only writes into Room.
  - A failed refresh never wipes cached data.
  - The tree is stored relationally:
    - `form_items` is an adjacency list (`parent_id` + `position`).
    - `response_sets` and `responses` hold the choice data.
    - `selections` holds the answers. It deliberately has no foreign key, so answers survive a form refresh.
- **Presentation**:
  - `MviViewModel<State, Intent, Effect>` exposes an immutable `StateFlow` state and a `Channel` of one-off effects.
  - Screens are stateless composables (`FormScreen(state, onIntent)`), wrapped by a `*Route` that connects the ViewModel.
  - The tree is flattened into `FormRow`s so it renders in a `LazyColumn`.
- **Navigation**: one `MainActivity` hosts a type-safe Navigation Compose graph (form → image detail).
- **DI**: Hilt modules for network, database, dispatchers and repository bindings.

## Tech stack

| Concern | Library |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose (type-safe routes) |
| DI | Dagger Hilt |
| Networking | Retrofit 3, OkHttp 4, kotlinx.serialization |
| Persistence | Room (KSP) |
| Images | Coil 3 (OkHttp fetcher + disk cache) |
| Async | Kotlin Coroutines & Flow |
| Tests | JUnit 4, MockK, Turbine, kotlinx-coroutines-test |

## Project structure

```
app/src/main/java/chingiz/simulationform/
├── data/
│   ├── local/        # Room database, DAO, entities
│   ├── mapper/       # DTO → domain, domain ⇄ entities
│   ├── network/      # Connectivity monitor
│   ├── remote/       # Retrofit API, DTOs, safeApiCall + retry/backoff
│   └── repository/   # Offline-first FormRepositoryImpl
├── di/               # Hilt modules
├── domain/
│   ├── model/        # FormItem tree, DataResult, DataError
│   ├── repository/   # Contracts
│   └── usecase/
├── presentation/
│   ├── common/       # Error → message mapping
│   ├── form/         # Form screen, ViewModel, contract, components
│   ├── image/        # Full-size image screen
│   ├── mvi/          # MVI base classes
│   └── navigation/   # NavHost & destinations
├── ui/theme/         # Colors, hierarchy typography, theme
├── MainActivity.kt   # Single activity
└── SimulationFormApp.kt
```

## Setup

### Requirements

- **Android Studio** (a recent stable or newer release that supports AGP 9.x)
- **JDK 17+**. The JetBrains Runtime bundled with Android Studio works.
- Android SDK with **API 37** (compileSdk). `minSdk` is 24.

### Run from Android Studio

1. Clone the repository and open the project root in Android Studio.
2. Let Gradle sync finish.
3. Select the `app` run configuration and run it on an emulator or a device.

### Command line

```bash
# If JAVA_HOME isn't set, you can use Android Studio's bundled JDK (macOS example):
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

./gradlew assembleDebug          # build the debug APK
./gradlew installDebug           # install on a connected device/emulator
./gradlew testDebugUnitTest      # run unit tests
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

### Trying the offline behaviour

1. Launch the app once while online, so the form and images are cached.
2. Enable airplane mode and relaunch. The cached form appears with an offline banner, and answers can still be given.
3. Disable airplane mode. The form refreshes automatically.
4. Optionally, uninstall the app and launch it while offline. You'll see the full-screen error with Retry.

## Tests

Unit tests in `app/src/test` cover:

- the selection toggle rules
- the remote mapper (unknown item types are skipped; `null` or missing scores are handled)
- a domain ⇄ Room entity round trip
- tree flattening (depth and section level)
- error mapping and retry/backoff
- the offline-first repository (cache is kept on failure; client errors are not retried)
- `FormViewModel` (loading, error, stale data, auto-retry on reconnect, selections, navigation effect)

## Commit history

Each step of the implementation is a separate commit, prefixed `SM-00`, `SM-01`, and so on.
