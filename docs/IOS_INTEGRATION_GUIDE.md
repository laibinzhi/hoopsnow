# iOS 接入指南 — HoopsNow KMP Shared 模块

## 1. 概述

HoopsNow 的所有业务逻辑和 UI 代码都在 `shared` 模块中，通过 Kotlin Multiplatform 编译为 iOS Framework（`Shared.framework`）。iOS 端只需一个极简的 SwiftUI 壳来承载 Compose Multiplatform 渲染的界面。

### 架构示意

```
┌─────────────────────────────────┐
│         iOS App (SwiftUI)       │
│  ┌───────────────────────────┐  │
│  │   ComposeUIViewController │  │
│  │   ┌───────────────────┐   │  │
│  │   │   HoopsNowApp()   │   │  │  ← Compose Multiplatform UI
│  │   │   (shared module)  │   │  │
│  │   └───────────────────┘   │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

## 2. 前置要求

- macOS（必须，iOS 开发只能在 macOS 上进行）
- Xcode 15.0+
- JDK 17+
- Kotlin 2.0.21+
- CocoaPods（可选，本项目使用直接 Framework 集成）

## 3. 项目结构

```
hoopsnow/
├── shared/                          # KMP 共享模块
│   └── src/
│       ├── commonMain/              # 跨平台代码（UI + 业务逻辑）
│       ├── androidMain/             # Android 平台实现
│       └── iosMain/                 # iOS 平台实现
│           └── kotlin/.../
│               ├── MainViewController.kt       # CMP 入口
│               ├── KoinHelper.kt               # Koin 初始化桥接
│               ├── core/database/
│               │   └── DatabaseDriverFactory.kt # SQLite Native Driver
│               └── di/
│                   └── KoinModules.ios.kt       # iOS 平台 DI 模块
│
└── iosApp/                          # iOS 应用壳
    └── iosApp/
        ├── iOSApp.swift             # App 入口，初始化 Koin
        ├── ContentView.swift        # 嵌入 ComposeUIViewController
        └── Info.plist               # iOS 配置
```

## 4. 构建 Shared Framework

### 4.1 Gradle 配置（已完成）

`shared/build.gradle.kts` 中已配置 iOS targets：

```kotlin
kotlin {
    listOf(
        iosX64(),           // iOS 模拟器 (Intel)
        iosArm64(),         // iOS 真机
        iosSimulatorArm64() // iOS 模拟器 (Apple Silicon)
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
}
```

### 4.2 编译 Framework

```bash
# 编译 iOS 模拟器 (Apple Silicon Mac) 的 Debug Framework
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# 编译 iOS 模拟器 (Intel Mac) 的 Debug Framework
./gradlew :shared:linkDebugFrameworkIosX64

# 编译 iOS 真机的 Release Framework
./gradlew :shared:linkReleaseFrameworkIosArm64
```

编译产物位于：
```
shared/build/bin/iosSimulatorArm64/debugFramework/Shared.framework
shared/build/bin/iosArm64/releaseFramework/Shared.framework
```

## 5. Xcode 项目配置

### 5.1 创建 Xcode 项目

如果还没有 Xcode 项目：

1. 打开 Xcode → File → New → Project
2. 选择 iOS → App
3. Product Name: `iosApp`
4. Interface: SwiftUI
5. Language: Swift
6. 保存到 `hoopsnow/iosApp/` 目录

### 5.2 集成 Shared Framework

#### 方式一：直接引用（推荐开发阶段）

1. 在 Xcode 中选择项目 Target → Build Settings
2. 搜索 `Framework Search Paths`，添加：
   ```
   $(SRCROOT)/../shared/build/bin/iosSimulatorArm64/debugFramework
   ```
3. 搜索 `Other Linker Flags`，添加：
   ```
   -framework Shared
   ```

#### 方式二：Build Phase 脚本自动编译

在 Target → Build Phases → 添加 New Run Script Phase（放在 Compile Sources 之前）：

```bash
cd "$SRCROOT/.."
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

这会在每次 Xcode 构建时自动编译对应架构的 Framework。

#### 方式三：使用 CocoaPods

在 `shared/build.gradle.kts` 中添加：

```kotlin
kotlin {
    cocoapods {
        summary = "HoopsNow Shared Module"
        homepage = "https://github.com/example/hoopsnow"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "Shared"
            isStatic = true
        }
    }
}
```

然后在 `iosApp/` 目录创建 `Podfile`：

```ruby
platform :ios, '16.0'

target 'iosApp' do
  use_frameworks!
  pod 'Shared', :path => '../shared'
end
```

运行 `pod install`，之后使用 `.xcworkspace` 打开项目。

### 5.3 配置 Info.plist

确保 `Info.plist` 中包含网络权限（用于 API 请求）：

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

## 6. iOS 端代码

整个 iOS 端只需要两个 Swift 文件：

### 6.1 iOSApp.swift �� 应用入口

```swift
import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // 初始化 Koin 依赖注入
        KoinHelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### 6.2 ContentView.swift — 嵌入 Compose UI

```swift
import SwiftUI
import Shared

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

### 工作原理

1. `iOSApp.swift` 在启动时调用 `KoinHelperKt.doInitKoin()` 初始化依赖注入
2. `ContentView` 通过 `UIViewControllerRepresentable` 将 Compose Multiplatform 的 `ComposeUIViewController` 嵌入 SwiftUI
3. `MainViewController()` 是 shared 模块中定义的入口，渲染完整的 `HoopsNowApp()` Compose UI

## 7. Shared 模块中的 iOS 平台代码

### 7.1 MainViewController.kt

```kotlin
// iosMain
package com.hoopsnow.nba

import androidx.compose.ui.window.ComposeUIViewController
import com.hoopsnow.nba.ui.HoopsNowApp

fun MainViewController() = ComposeUIViewController { HoopsNowApp() }
```

### 7.2 KoinHelper.kt — Koin 初始化桥接

```kotlin
// iosMain
package com.hoopsnow.nba

import com.hoopsnow.nba.di.initKoin

fun doInitKoin() {
    initKoin()
}
```

Kotlin 的顶层函数在 Swift 中通过 `<文件名>Kt.函数名()` 调用。

### 7.3 KoinModules.ios.kt — iOS 平台模块

```kotlin
// iosMain
actual fun platformModule(): Module = module {
    single<HttpClientEngine> { Darwin.create() }
    single { DatabaseDriverFactory() }
}
```

提供 iOS 特定的：
- Ktor HTTP 引擎（Darwin）
- SQLDelight 数据库驱动（NativeSqliteDriver）

### 7.4 DatabaseDriverFactory.kt — iOS 数据库驱动

```kotlin
// iosMain
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(NbaDatabase.Schema, "nba.db")
    }
}
```

## 8. 运行和调试

### 8.1 通过命令行运行

```bash
# 在 iOS 模拟器上运行
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# 然后在 Xcode 中选择模拟器，点击 Run (⌘R)
```

### 8.2 通过 Android Studio / Fleet

安装 [Kotlin Multiplatform 插件](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform)，可以��接在 Android Studio 中选择 iOS 模拟器运行。

### 8.3 调试技巧

- Compose UI 的日志会输出到 Xcode Console
- 网络请求可通过 Ktor 的 `Logging` 插件查看
- SQLDelight 数据库文件位于 App 沙盒的 Documents 目录
- 如果遇到 Framework 找不到的问题，先执行 `./gradlew clean` 再重新编译

## 9. 常见问题

### Q: 编译报错 "Framework not found Shared"
A: 确保先执行了 `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`，并且 Xcode 的 Framework Search Paths 指向正确的路径。

### Q: 运行时崩溃 "Kotlin/Native runtime"
A: 检查是否在 `iOSApp.init()` 中调用了 `KoinHelperKt.doInitKoin()`。Koin 必须在使用任何 shared 模块功能之前初始化。

### Q: 如何添加 iOS 特有的功能？
A: 两种方式：
1. 在 `shared/src/iosMain/` 中通过 `expect/actual` 添加平台实现
2. 在 `iosApp/` 的 Swift 代码中直接调用 shared 模块暴露的 Kotlin API

### Q: 如何在 Swift 中调用 Kotlin 代码？
A: Kotlin/Native 会自动生成 Objective-C 头文件。在 Swift 中：
- 顶层函数：`<文件名>Kt.函数名()`
- 类：直接使用类名
- 枚举/sealed class：映射为 Objective-C 类层次

### Q: Compose Multiplatform 在 iOS 上的性能如何？
A: CMP 在 iOS 上使用 Skia 渲染引擎（通过 Skiko），性能接近原生。对于大多数应用场景完全够用。如果有特定的性能敏感场景，可以通过 `expect/actual` 使用原生 SwiftUI 组件。

### Q: 如何处理 iOS 特有的系统权限？
A: 系统权限（如推送通知、相机等）仍需在 iOS 端原生处理。可以通过 `expect/actual` 模式在 shared 模块中定义接口，在 iosMain 中调用 iOS 原生 API。

### Q: 如何发布到 App Store？
A: 与普通 iOS 应用发布流程完全一致：
1. 在 Xcode 中配置签名和 Provisioning Profile
2. Archive → Upload to App Store Connect
3. Framework 会被打包进 .ipa 文件中

## 10. 参考资源

- [Kotlin Multiplatform 官方文档](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform 官方文档](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Voyager 导航框架](https://voyager.adriel.cafe/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)
- [Coil 3 (KMP)](https://coil-kt.github.io/coil/)
