# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Android app
./gradlew :android-app:assembleDebug
./gradlew :android-app:assembleRelease

# Wear OS app
./gradlew :wear-app:assembleDebug

# Desktop app
./gradlew :desktop-app:run
./gradlew :desktop-app:hotRunDesktop --auto   # hot reload

# Tests
./gradlew test                                 # all unit tests
./gradlew :common:view:testDebugUnitTest       # single module

# Code quality
./gradlew lint
./gradlew detekt
```

## Architecture

This is a **Kotlin Multiplatform** Hacker News client targeting Android, Wear OS, Desktop (JVM), and iOS.

### Module Layout

```
android-app/        Android application shell
wear-app/           Wear OS application shell
desktop-app/        Compose Desktop application shell
ios-app/            Xcode iOS application shell
common/
  core/             Shared utilities (multiplatform)
  data/             Data layer: Ktor HTTP, Room DB, DataStore
  domain/           Business logic, navigation contracts
  view/             Shared Compose UI (multiplatform)
  android/          Android-specific bindings and implementations
  wear/             Wear OS UI components
  injection/        Pure Kotlin DI module (Metro)
ksp/
  injection-processor/   KSP processor for Metro DI codegen
  screenshot-processor/  KSP processor for Roborazzi screenshot tests
build-logic/        Custom Gradle convention plugins
```

### Key Patterns

- **DI:** [Metro](https://github.com/ZacSweers/metro) (compile-time, KSP-based). Components are in `common/injection/`.
- **Navigation:** Jetpack Navigation 3 (`navigation3` version `1.1.0`). Navigation contracts live in `common/domain/`.
- **Database:** Room with multiplatform support (Android, iOS, Desktop).
- **Networking:** Ktor Client with platform-specific engines (`CIO`/`Darwin`/`Apache5`).
- **UI State:** MVVM with Compose; ViewModels have platform-specific factory wrappers in each app shell.
- **Lint config:** `warningsAsErrors = true` — lint failures will break the build.
- **Detekt config:** `detekt.yml` at root; static analysis runs on all modules.

### Dependency Versions

All versions are managed in `gradle/libs.versions.toml`. Key versions:
- Kotlin: 2.3.21
- AGP: 9.2.0
- Compose Multiplatform: 1.10.3
- Ktor: 3.4.3
- Room: 2.8.4
- Metro: 1.0.0-RC4

### Targets

| Platform | Min SDK / Runtime |
|----------|-------------------|
| Android  | minSdk 29, targetSdk 37 |
| Wear OS  | Separate `wear-app` module |
| Desktop  | JVM 21 |
| iOS      | Arm64 + Simulator Arm64 (static framework) |

### Build Performance

Parallel builds, build cache, and configuration cache are all enabled. The Gradle daemon is configured with `-Xmx4g`. Don't disable these.

### Release Builds

Release APKs are minified (`isMinifyEnabled=true`, `isShrinkResources=true`). ProGuard/R8 consumer rules live in each module's `proguard-rules.pro`. The desktop app also runs ProGuard. When adding new reflective code or third-party libraries, add keep rules accordingly.