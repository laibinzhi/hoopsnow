# HoopsNow - NBA Stats App

[中文文档](README_CN.md) | English

**HoopsNow** is a cross-platform NBA application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**, sharing a single codebase for both Android and iOS. It provides real-time game scores, team information, player stats, and allows users to track their favorite teams and players.

> This project was migrated from a traditional multi-module Android architecture (Hilt + Navigation3 + Room) to a KMP shared module architecture. See [Migration Guide](docs/ANDROID_TO_KMP_MIGRATION_GUIDE.md) for details.

## Features

HoopsNow displays content from the [Ball Don't Lie API](https://www.balldontlie.io/), providing:

- **Games**: View today's NBA games with live scores, final results, and scheduled matchups
- **Teams**: Browse all 30 NBA teams with detailed information
- **Players**: Search and explore NBA players with their stats and team affiliations
- **Favorites**: Save your favorite teams and players for quick access

### Screenshots

<p align="center">
  <img src="screenshots/cmp_game_list.png" width="200" alt="Games Screen" />
  <img src="screenshots/cmp_teams_list.png" width="200" alt="Teams Screen" />
  <img src="screenshots/cmp_player_list.png" width="200" alt="Players Screen" />
  <img src="screenshots/cmp_favorite.png" width="200" alt="Favorites Screen" />
</p>

<p align="center">
  <img src="screenshots/cmp_game_detail.png" width="200" alt="Game Detail" />
  <img src="screenshots/cmp_team_detail.png" width="200" alt="Team Detail" />
  <img src="screenshots/cmp_player_detail.png" width="200" alt="Player Detail" />
</p>

## Architecture

HoopsNow uses a **KMP shared module** architecture — all business logic and UI code lives in the `shared` module, with thin platform-specific entry points for Android and iOS.

### Project Structure

```
hoopsnow/
├── app/                                # Android entry point (minimal)
│   └── src/main/java/.../
│       ├── MainActivity.kt             # Hosts HoopsNowApp()
│       └── HoopsNowApplication.kt      # Initializes Koin
│
├── shared/                             # KMP shared module (all logic + UI)
│   └── src/
│       ├── commonMain/                 # Cross-platform shared code
│       │   ├── kotlin/.../
│       │   │   ├── core/
│       │   │   │   ├── common/         # Result wrapper, exceptions
│       │   │   │   ├── data/           # Repository interfaces + implementations
│       │   │   │   ├── database/       # DatabaseDriverFactory (expect)
│       │   │   │   ├── model/          # Domain models (Game, Team, Player)
│       │   │   │   └── network/        # Ktor network layer
│       │   │   ├── di/                 # Koin module definitions
│       │   │   └── ui/
│       │   │       ├── HoopsNowApp.kt  # Main Composable entry point
│       │   │       ├── component/      # Shared UI components
│       │   │       ├── navigation/     # Voyager Tab definitions
│       │   │       ├── theme/          # Colors, typography, theme
│       │   │       ├── games/          # Games screens + ScreenModels
│       │   │       ├── teams/          # Teams screens + ScreenModels
│       │   │       ├── players/        # Players screens + ScreenModels
│       │   │       └── favorites/      # Favorites screens + ScreenModels
│       │   └── sqldelight/             # .sq schema & query files
│       ├── androidMain/                # Android: OkHttp engine, SQLite driver
│       └── iosMain/                    # iOS: Darwin engine, Native driver
│
├── iosApp/                             # iOS entry point (SwiftUI shell)
│   └── iosApp/
│       ├── iosApp.xcodeproj/           # Xcode project
│       ├── iOSApp.swift                # Initializes Koin
│       ├── ContentView.swift           # Embeds ComposeUIViewController
│       └── Info.plist                  # iOS app config
│
├── build-logic/                        # Convention Plugins
└── gradle/libs.versions.toml           # Dependency version catalog
```

### Key Architecture Decisions

- **Single Shared Module**: All business logic and UI in one KMP module, platform entry points are minimal
- **Unidirectional Data Flow (UDF)**: State flows down, events flow up
- **Offline-First**: Local database as source of truth, synced with remote API
- **Repository Pattern**: Interface/implementation separation for testability
- **Voyager Navigation**: `TabNavigator` for bottom tabs, nested `Navigator` per tab for page stacks
- **ScreenModel**: Voyager's lifecycle-aware state holder (replaces ViewModel)
- **Koin DI**: Cross-platform dependency injection with `expect/actual` platform modules
- **expect/actual**: Platform-specific implementations for database drivers and HTTP engines

### Data Layer Architecture

```
┌─────────────────────────────────────────┐
│              UI Layer                    │
│  (Compose Screens + ScreenModels)       │
├─────────────────────────────────────────┤
│            Data Layer                    │
│  (Repository Interfaces)                │
├─────────────────────────────────────────┤
│     Offline-First Implementations       │
│  (SQLDelight Database + Ktor Network)   │
└─────────────────────────────────────────┘
```

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 2.0.21 |
| UI | Compose Multiplatform 1.7.3, Material 3 |
| Navigation | Voyager 1.1.0-beta03 |
| DI | Koin 4.0.0 |
| Database | SQLDelight 2.0.2 |
| Networking | Ktor 3.0.3, Kotlin Serialization 1.7.3 |
| Image Loading | Coil 3.0.4 (KMP) |
| Async | Kotlin Coroutines 1.9.0, Flow |
| Date/Time | kotlinx-datetime 0.6.1 |
| Architecture | UDF, Offline-First, Repository Pattern |
| Build | Gradle 8.11.1, AGP 8.9.1, Convention Plugins |
| Platforms | Android, iOS |

## Development Environment

### Requirements

- Android Studio Ladybug (2024.2.1) or newer
- JDK 17
- Android SDK 36
- Xcode 15.0+ (for iOS)

### Getting Started

1. Clone the repository:
```bash
git clone https://github.com/laibinzhi/hoopsnow.git
cd hoopsnow
git checkout cmp
```

2. Open the project in Android Studio

3. Sync Gradle and run the app on an emulator or device

### Build

Build Android Debug APK:
```bash
./gradlew :app:assembleDebug
```

Build Android Release APK:
```bash
./gradlew :app:assembleRelease
```

Build iOS Framework (Apple Silicon simulator):
```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

### Run on iOS

1. Build the shared framework (see above)
2. Open `iosApp/iosApp/iosApp.xcodeproj` in Xcode
3. Select a simulator and press ⌘R

For detailed iOS setup, see [iOS Integration Guide](docs/IOS_INTEGRATION_GUIDE.md).

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

## Documentation

- [Android to KMP Migration Guide](docs/ANDROID_TO_KMP_MIGRATION_GUIDE.md) — Full migration walkthrough from multi-module Android to KMP
- [iOS Integration Guide](docs/IOS_INTEGRATION_GUIDE.md) — How to build, configure, and run the iOS app

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

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) - Cross-platform framework
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) - Shared UI toolkit
- [Voyager](https://voyager.adriel.cafe/) - Multiplatform navigation
- [Koin](https://insert-koin.io/) - Dependency injection
- [SQLDelight](https://cashapp.github.io/sqldelight/) - Multiplatform database
- [Ball Don't Lie API](https://www.balldontlie.io/) - NBA data provider
