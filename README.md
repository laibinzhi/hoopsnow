# HoopsNow - NBA Stats App

[中文文档](README_CN.md) | English

**HoopsNow** is a modern Android application for NBA fans, built entirely with **Kotlin** and **Jetpack Compose**. It provides real-time game scores, team information, player stats, and allows users to track their favorite teams and players.

This app follows Android's recommended [architecture guidelines](https://developer.android.com/topic/architecture) and serves as a reference implementation for building production-ready Android applications.

## Features

HoopsNow displays content from the [Ball Don't Lie API](https://www.balldontlie.io/), providing:

- **Games**: View today's NBA games with live scores, final results, and scheduled matchups
- **Teams**: Browse all 30 NBA teams with detailed information
- **Players**: Search and explore NBA players with their stats and team affiliations
- **Favorites**: Save your favorite teams and players for quick access

### Screenshots

<p align="center">
  <img src="screenshots/games.jpg" width="200" alt="Games Screen" />
  <img src="screenshots/teams.jpg" width="200" alt="Teams Screen" />
  <img src="screenshots/players.jpg" width="200" alt="Players Screen" />
  <img src="screenshots/favorites.jpg" width="200" alt="Favorites Screen" />
</p>

<p align="center">
  <img src="screenshots/game_detail.jpg" width="200" alt="Game Detail" />
  <img src="screenshots/team_detail.jpg" width="200" alt="Team Detail" />
  <img src="screenshots/player_detail.jpg" width="200" alt="Player Detail" />
</p>

## Architecture

HoopsNow follows the [Now in Android](https://github.com/android/nowinandroid) architecture pattern with a modular structure that separates public API contracts from internal implementations.

### Module Structure

```
app/                        # Application module - navigation, scaffolding
├── navigation/             # Navigation 3 implementation

build-logic/                # Convention Plugins for consistent build configuration
└── convention/             # Gradle convention plugins

feature/                    # Feature modules (api/impl pattern)
├── games/
│   ├── api/                # Public navigation contracts (NavKeys)
│   └── impl/               # Internal implementation (Screens, ViewModels)
├── teams/
│   ├── api/
│   └── impl/
├── players/
│   ├── api/
│   └── impl/
└── favorites/
    ├── api/
    └── impl/

core/                       # Core modules
├── common/                 # Shared utilities, dispatchers, Result wrapper
├── data/                   # Repositories (interface + offline-first impl)
├── database/               # Room database, DAOs, entities
├── datastore/              # DataStore preferences for user data
├── network/                # Retrofit API implementation
├── model/                  # Domain models (pure Kotlin)
├── designsystem/           # Theme, colors, and reusable components
├── ui/                     # Shared UI components across features
└── testing/                # Test utilities, fakes, and test data
```

### Feature Module Pattern

Each feature module is split into two submodules:

- **api**: Contains public navigation contracts (`NavKey` definitions) that other modules can depend on
- **impl**: Contains internal implementation (Screens, ViewModels, UiState) that should not be exposed

This pattern provides:
- Clear module boundaries and dependencies
- Faster build times through better parallelization
- Encapsulation of implementation details

### Key Architecture Decisions

- **Unidirectional Data Flow (UDF)**: State flows down, events flow up
- **Offline-First**: Local database is the source of truth, synced with remote
- **Repository Pattern**: Interface/Implementation separation for testability
- **StateFlow**: Reactive state management in ViewModels
- **Sealed Interfaces**: Type-safe UI states (Loading, Success, Empty, Error)
- **Navigation 3**: Type-safe navigation with serializable NavKeys
- **Convention Plugins**: Consistent build configuration across modules
- **Typesafe Project Accessors**: Type-safe module dependencies (`projects.core.data`)

### Data Layer Architecture

```
┌─────────────────────────────────────────┐
│              UI Layer                    │
│  (Compose Screens + ViewModels)          │
├─────────────────────────────────────────┤
│            Data Layer                    │
│  (Repository Interfaces)                 │
├─────────────────────────────────────────┤
│     Offline-First Implementations        │
│  (Room Database + Network Sync)          │
└─────────────────────────────────────────┘
```

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation 3 |
| DI | Hilt |
| Database | Room |
| Preferences | DataStore |
| Networking | Retrofit, OkHttp, Kotlin Serialization |
| Async | Kotlin Coroutines, Flow |
| Architecture | MVVM, NIA (Now in Android) Pattern |
| Build | Gradle 8.11.1, AGP 8.9.1, Convention Plugins |
| Testing | JUnit, Turbine, Coroutines Test |

## Development Environment

### Requirements

- Android Studio Ladybug (2024.2.1) or newer
- JDK 17
- Android SDK 36

### Getting Started

1. Clone the repository:
```bash
git clone https://github.com/laibinzhi/hoopsnow.git
cd hoopsnow
```

2. Open the project in Android Studio

3. Sync Gradle and run the app on an emulator or device

### Build

Build the debug APK:
```bash
./gradlew :app:assembleDebug
```

Build the release APK:
```bash
./gradlew :app:assembleRelease
```

### Testing

Run unit tests:
```bash
./gradlew test
```

The `core:testing` module provides:
- **Fake Repositories**: `FakeFavoritesRepository`, `FakePlayersRepository`, `FakeTeamsRepository`, `FakeGamesRepository`
- **Test Utilities**: `MainDispatcherRule` for coroutine testing
- **Test Data**: `TestData` factory for creating test objects

## API

This app uses the [Ball Don't Lie API](https://www.balldontlie.io/) for NBA data. The API provides:

- Teams information
- Players data with search functionality
- Games with scores and schedules

## UI Design

HoopsNow implements a dark theme optimized for sports content viewing:

- **Color Palette**: Slate-based dark theme with blue accents
- **Typography**: Bold, sports-inspired text hierarchy
- **Components**: Custom game cards, team/player list items
- **Edge-to-Edge**: Full immersive experience with proper inset handling

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

```
Copyright 2026 HoopsNow

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Acknowledgments

- [Now in Android](https://github.com/android/nowinandroid) - Architecture reference
- [Ball Don't Lie API](https://www.balldontlie.io/) - NBA data provider
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI toolkit
