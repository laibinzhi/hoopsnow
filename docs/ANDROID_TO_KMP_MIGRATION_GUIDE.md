# Android 迁移 KMP + Compose Multiplatform 详细说明书

## 1. 项目概述

HoopsNow 是一个 NBA 数据展示应用，原本采用纯 Android 多模块架构（Hilt + Navigation3 + Room），现已完成向 Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) 的全面迁移，实现 Android/iOS 共享 UI 和业务逻辑。

## 2. 迁移前后架构对比

### 迁移前（纯 Android 多模块）

```
hoopsnow/
├── app/                          # Android 入口
├── core/
│   ├── common/                   # 通用工具
│   ├── data/                     # Repository 层
│   ├── database/                 # Room 数据库
│   ├── datastore/                # DataStore 偏好存储
│   ├── designsystem/             # 主题/颜色/字体
│   ├── model/                    # 数据模型
│   ├── network/                  # Ktor 网络层
│   ├── testing/                  # 测试工具
│   └── ui/                       # 通用 UI 组件
├── feature/
│   ├── games/   (api + impl)     # 比赛功能
│   ├── teams/   (api + impl)     # 球队功能
│   ├── players/ (api + impl)     # 球员功能
│   └── favorites/ (api + impl)   # 收藏功能
└── build-logic/                  # Convention Plugins
```

技术栈：Hilt (DI) + Navigation3 + Room (DB) + ViewModel + Coil

### 迁移后（KMP + CMP 单 shared 模块）

```
hoopsnow/
├── app/                          # Android 入口（极简）
├── shared/                       # KMP 共享模块（全部业务逻辑 + UI）
│   └── src/
│       ├── commonMain/           # 跨平台共享代码
│       ├── androidMain/          # Android 平台实现
│       └── iosMain/              # iOS 平台实现
├── iosApp/                       # iOS 入口（SwiftUI 壳）
└── build-logic/                  # Convention Plugins（精简）
```

技术栈：Koin (DI) + Voyager (导航) + SQLDelight (DB) + ScreenModel + Coil 3

## 3. 技术栈替换清单

| 功能 | 迁移前 (Android) | 迁移后 (KMP) | 说明 |
|------|------------------|--------------|------|
| 依赖注入 | Hilt | Koin 4.0.0 | Koin 原生支持 KMP |
| 导航 | Navigation3 | Voyager 1.1.0-beta03 | TabNavigator + Navigator |
| 数据库 | Room | SQLDelight 2.0.2 | .sq 文件定义 SQL |
| 状态管理 | ViewModel | Voyager ScreenModel | screenModelScope 替代 viewModelScope |
| 图片加载 | Coil (Android) | Coil 3.0.4 (KMP) | AsyncImage / SubcomposeAsyncImage |
| 偏好存储 | DataStore | multiplatform-settings 1.2.0 | 跨平台 key-value 存储 |
| 网络 | Ktor (Android) | Ktor 3.0.3 (KMP) | OkHttp(Android) / Darwin(iOS) |
| 序列化 | kotlinx-serialization | kotlinx-serialization 1.7.3 | 无变化 |
| UI 框架 | Jetpack Compose | Compose Multiplatform 1.7.3 | 共享 UI 代码 |
| 生命周期 | collectAsStateWithLifecycle | collectAsState | CMP 中无 lifecycle 依赖 |

## 4. 迁移步骤详解

### 4.1 创建 shared 模块

在项目根目录创建 `shared/` 模块，配置 `build.gradle.kts`：

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)

            // Ktor / SQLDelight / Koin / Voyager / Coil ...
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}
```

### 4.2 迁移数据层

#### 4.2.1 Model 层
直接将 `core/model/` 下的 data class 复制到 `shared/src/commonMain/`，无需修改。

#### 4.2.2 Network 层
Ktor 本身就是 KMP 库，将 `core/network/` 代码移入 commonMain，仅需：
- Android 使用 `OkHttp` 引擎
- iOS 使用 `Darwin` 引擎

通过 `expect/actual` 或 Koin 平台模块注入不同引擎。

#### 4.2.3 Database 层（Room → SQLDelight）

Room 不支持 KMP，需替换为 SQLDelight：

1. 创建 `.sq` 文件定义表结构和查询：

```sql
-- Team.sq
CREATE TABLE TeamEntity (
    id INTEGER PRIMARY KEY NOT NULL,
    conference TEXT NOT NULL,
    division TEXT NOT NULL,
    city TEXT NOT NULL,
    name TEXT NOT NULL,
    fullName TEXT NOT NULL,
    abbreviation TEXT NOT NULL
);

getAll: SELECT * FROM TeamEntity;
getById: SELECT * FROM TeamEntity WHERE id = ?;
upsert: INSERT OR REPLACE INTO TeamEntity VALUES (?, ?, ?, ?, ?, ?, ?);
```

2. SQLDelight 会自动生成 `NbaDatabase`、`TeamQueries` 等类
3. 通过 `expect/actual` 实现平台 Driver：

```kotlin
// commonMain
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// androidMain
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(NbaDatabase.Schema, context, "nba.db")
}

// iosMain
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(NbaDatabase.Schema, "nba.db")
}
```

#### 4.2.4 Repository 层
将 Repository 接口和实现移入 commonMain，主要改动：
- `database.gameEntityQueries` → `database.gameQueries`（SQLDelight 生成的属性名基于 .sq 文件名）
- Room 的 `Flow` 返回 → SQLDelight 的 `asFlow().mapToList()`

### 4.3 迁移 DI（Hilt → Koin）

```kotlin
// commonMain - KoinModules.kt
val sharedModule = module {
    // Network
    single<NbaNetworkDataSource> { KtorNbaNetwork(get()) }

    // Database
    single { get<DatabaseDriverFactory>().createDriver() }
    single { NbaDatabase(get()) }

    // Repositories
    single<GamesRepository> { OfflineFirstGamesRepository(get(), get()) }
    single<TeamsRepository> { OfflineFirstTeamsRepository(get(), get()) }
    single<PlayersRepository> { OfflineFirstPlayersRepository(get(), get()) }
    single<FavoritesRepository> { OfflineFirstFavoritesRepository(get(), get()) }

    // ScreenModels
    factory { GamesListScreenModel(get()) }
    factory { params -> GameDetailScreenModel(params.get(), get()) }
    // ...
}

// platformModule() 通过 expect/actual 提供平台特定依赖
expect fun platformModule(): Module
```

### 4.4 迁移 UI 层

#### 4.4.1 ViewModel → ScreenModel

```kotlin
// 迁移前
@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val gamesRepository: GamesRepository,
) : ViewModel() {
    // viewModelScope.launch { ... }
    // stateIn(viewModelScope, ...)
}

// 迁移后
class GamesListScreenModel(
    private val gamesRepository: GamesRepository,
) : ScreenModel {
    // screenModelScope.launch { ... }
    // stateIn(screenModelScope, ...)
}
```

关键变化：
- 移除 `@HiltViewModel` 和 `@Inject constructor`
- `ViewModel()` → `ScreenModel`
- `viewModelScope` → `screenModelScope`
- `collectAsStateWithLifecycle()` → `collectAsState()`

#### 4.4.2 Screen 迁移（Navigation3 → Voyager）

```kotlin
// 迁移前 — NavKey + entryProvider
@Serializable data object GamesNavKey : NavKey
// 在 EntryProviders.kt 中注册 entry

// 迁移后 — Voyager Screen
class GamesListScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<GamesListScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        // UI 实现...
    }
}
```

#### 4.4.3 导航结构（TabNavigator）

```kotlin
// Tabs.kt — 定义 4 个底部 Tab
object GamesTab : Tab {
    override val options @Composable get() = TabOptions(
        index = 0u,
        title = "Games",
        icon = rememberVectorPainter(Icons.Default.SportBasketball),
    )

    @Composable
    override fun Content() {
        Navigator(GamesListScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

// HoopsNowApp.kt — 主入口
@Composable
fun HoopsNowApp() {
    HoopsNowTheme {
        TabNavigator(GamesTab) {
            Scaffold(
                bottomBar = { /* NavigationBar with TabNavigationItem */ },
            ) {
                CurrentTab()
            }
        }
    }
}
```

每个 Tab 内嵌独立的 `Navigator`，实现 Tab 内的页面栈管理。页面间导航使用 `navigator.push(DetailScreen(id))`。

#### 4.4.4 图片加载（Coil → Coil 3 KMP）

```kotlin
// 迁移前 (Coil Android)
AsyncImage(model = url, contentDescription = null)

// 迁移后 (Coil 3 KMP) — 基本相同
AsyncImage(model = url, contentDescription = null)

// 或使用 SubcomposeAsyncImage 自定义 loading/error 状态
SubcomposeAsyncImage(
    model = url,
    contentDescription = null,
    loading = { CircularProgressIndicator() },
    error = { /* fallback */ },
)
```

### 4.5 精简 app 模块

迁移后 app 模块只保留 Android 入口：

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(...)
        setContent {
            CompositionLocalProvider(
                LocalTeamLogos provides TeamLogoProvider.getAllLogos(),
                LocalPlayerHeadshot provides PlayerHeadshotProvider::getHeadshotUrl,
            ) {
                HoopsNowApp()  // 来自 shared 模块
            }
        }
    }
}

// HoopsNowApplication.kt
class HoopsNowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HoopsNowApplication)
            modules(sharedModule, platformModule())
        }
    }
}
```

### 4.6 清理旧代码

迁移完成后建议清理（当前仓库仍保留部分 `core/`、`feature/` 历史代码用于参考，但不在 `settings.gradle.kts` 中参与构建）：
- `core/` — 所有旧 Android 模块（data、database、network、model、ui、designsystem 等）
- `feature/` — 所有旧 feature 模块（games、teams、players、favorites）
- `app/` 中的旧导航文件（HoopsNowApp.kt、navigation/）
- `build-logic/` 中不再使用的 Convention Plugin（Hilt、Room、Feature、Library 等）
- `libs.versions.toml` 中的 Hilt、KSP 相关依赖
- `build.gradle.kts` 中的 Hilt、KSP plugin 声明

## 5. 迁移后项目结构

```
hoopsnow/
├── app/                                    # Android 入口
│   └── src/main/java/.../
│       ├── MainActivity.kt                 # Activity，调用 HoopsNowApp()
│       └── HoopsNowApplication.kt          # Application，初始化 Koin
│
├── shared/                                 # KMP 共享模块
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/.../
│       │   │   ├── core/
│       │   │   │   ├── common/             # Result, NbaException
│       │   │   │   ├── data/               # Repository 接口 + 实现
│       │   │   │   ├── database/           # DatabaseDriverFactory (expect)
│       │   │   │   ├── model/              # Game, Team, Player
│       │   │   │   └── network/            # Ktor 网络层
│       │   │   ├── di/                     # Koin 模块定义
│       │   │   └── ui/
│       │   │       ├── HoopsNowApp.kt      # 主入口 Composable
│       │   │       ├── component/          # 通用 UI 组件
│       │   │       ├── navigation/         # Voyager Tabs
│       │   │       ├── theme/              # 主题/颜色/字体
│       │   │       ├── games/              # 比赛 Screen + ScreenModel
│       │   │       ├── teams/              # 球队 Screen + ScreenModel
│       │   │       ├── players/            # 球员 Screen + ScreenModel
│       │   │       └── favorites/          # 收藏 Screen + ScreenModel
│       │   └── sqldelight/.../             # .sq 文件
│       ├── androidMain/                    # Android Driver + platformModule
│       └── iosMain/                        # iOS Driver + platformModule + MainViewController
│
├── iosApp/                                 # iOS SwiftUI 壳
│   └── iosApp/
│       ├── iosApp.xcodeproj/               # Xcode 工程文件
│       ├── iOSApp.swift                    # 初始化 Koin
│       ├── ContentView.swift               # 嵌入 ComposeUIViewController
│       └── Info.plist                      # iOS 配置
│
├── build-logic/                            # Convention Plugins（仅保留 Application 相关）
├── gradle/libs.versions.toml              # 依赖版本管理
└── settings.gradle.kts                     # 仅 include :shared 和 :app
```

## 6. 常见问题

### Q: SQLDelight 生成的属性名是什么规则？
A: 基于 `.sq` 文件名。例如 `Game.sq` 生成 `database.gameQueries`，`Team.sq` 生成 `database.teamQueries`。不是基于 `CREATE TABLE` 的表名。

### Q: collectAsStateWithLifecycle 为什么要换成 collectAsState？
A: `collectAsStateWithLifecycle` 是 AndroidX Lifecycle 的扩展，不支持 KMP。在 CMP 中使用 `collectAsState()` 即可，Voyager 的 ScreenModel 会在 Screen dispose 时自动取消 scope。

### Q: Voyager 中如何实现 Tab 内独立导航栈？
A: 每个 Tab 的 `Content()` 中创建独立的 `Navigator`，Tab 切换时各自的导航栈互不影响：
```kotlin
object GamesTab : Tab {
    @Composable
    override fun Content() {
        Navigator(GamesListScreen()) { SlideTransition(it) }
    }
}
```

### Q: 如何在 Screen 之间传参？
A: Voyager Screen 通过构造函数传参：
```kotlin
class GameDetailScreen(private val gameId: Int) : Screen { ... }
// 导航时
navigator.push(GameDetailScreen(gameId = 123))
```

### Q: Koin 中 ScreenModel 如何获取参数？
A: 使用 `parametersOf`：
```kotlin
// 定义
factory { params -> GameDetailScreenModel(params.get(), get()) }
// 使用
val screenModel = koinScreenModel<GameDetailScreenModel> { parametersOf(gameId) }
```

## 7. 依赖版本参考

| 库 | 版本 |
|----|------|
| Kotlin | 2.0.21 |
| Compose Multiplatform | 1.7.3 |
| Ktor | 3.0.3 |
| SQLDelight | 2.0.2 |
| Koin | 4.0.0 |
| Voyager | 1.1.0-beta03 |
| Coil 3 | 3.0.4 |
| multiplatform-settings | 1.2.0 |
| kotlinx-serialization | 1.7.3 |
| kotlinx-datetime | 0.6.1 |
| Coroutines | 1.9.0 |
