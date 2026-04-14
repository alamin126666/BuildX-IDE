# BuildX IDE - Professional Android IDE with GitHub Actions Integration

<p align="center">
  <img src="assets/logo.png" alt="BuildX IDE Logo" width="200"/>
</p>

<p align="center">
  <strong>A professional-grade Android IDE that builds itself and empowers developers to build their apps using GitHub Actions</strong>
</p>

<p align="center">
  <a href="https://github.com/your-org/buildx-ide/actions">
    <img src="https://github.com/your-org/buildx-ide/workflows/Build%20BuildX%20IDE/badge.svg" alt="Build Status"/>
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"/>
  </a>
  <img src="https://img.shields.io/badge/Platform-Android%2026%2B-brightgreen" alt="Platform"/>
  <img src="https://img.shields.io/badge/Kotlin-1.9.22-purple" alt="Kotlin"/>
</p>

---

## 🚀 Features

### Core IDE Features

- **📝 Advanced Code Editor** - Syntax highlighting, auto-completion, and code folding
- **📁 Project Management** - Create, open, and manage multiple projects
- **🔧 Build Automation** - Trigger GitHub Actions builds directly from the IDE
- **📊 Build Monitoring** - Real-time build status and logs
- **🗂️ File Explorer** - Navigate and edit project files
- **🔐 Git Integration** - Clone, commit, push, and pull using JGit
- **🎨 Customizable Themes** - Dark, Light, Monokai, Solarized themes
- **⚡ Performance Optimized** - Multi-dex support and optimized builds

### GitHub Actions Integration

- **🔄 Self-Building IDE** - BuildX builds itself using GitHub Actions
- **🏗️ User Project Builds** - Build user projects via GitHub Actions
- **📝 Workflow Templates** - Pre-built workflows for Android, Kotlin, Java
- **🔍 Build History** - Track all build attempts and results
- **📦 Artifact Downloads** - Download built APKs directly to device
- **🔑 Secure Authentication** - GitHub Personal Access Token management

### AI-Powered Features (Coming Soon)

- **🤖 AI Code Completion** - Intelligent code suggestions
- **🐛 Smart Error Detection** - AI-powered error highlighting
- **💡 Code Refactoring** - Automated refactoring suggestions
- **📖 Code Documentation** - Auto-generate documentation

---

## 📋 Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────┐
│                   BuildX IDE App                    │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │   UI Layer   │  │  ViewModel   │  │Repository │ │
│  │              │  │   Layer      │  │  Layer    │ │
│  │ - MainActivity│◄►│ - MainVM    │◄►│ -GitHubRepo│ │
│  │ - EditorActivity│ │ - EditorVM │  │ - FileRepo │ │
│  │ - SettingsActivity││ - BuildVM │  │ - GitRepo  │ │
│  └──────────────┘  └──────────────┘  └───────────┘ │
│                                                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │           Service Layer                      │  │
│  │                                              │  │
│  │  - BuildService (Foreground)                 │  │
│  │  - WorkManager (Background Jobs)             │  │
│  │  - Notification Manager                      │  │
│  └──────────────────────────────────────────────┘  │
│                                                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │           Data Layer                         │  │
│  │                                              │  │
│  │  - Room Database (Projects, Settings)        │  │
│  │  - DataStore (Preferences)                   │  │
│  │  - Retrofit (GitHub API)                     │  │
│  │  - File System (Project Files)               │  │
│  └──────────────────────────────────────────────┘  │
│                                                     │
└─────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│              GitHub Actions API                     │
│                                                     │
│  - Trigger Workflows                                │
│  - Monitor Build Status                             │
│  - Retrieve Logs                                    │
│  - Download Artifacts                               │
└─────────────────────────────────────────────────────┘
```

### Project Structure

```
BuildX-IDE/
├── .github/
│   └── workflows/
│       ├── build.yml              # Self-build workflow
│       └── user-build.yml         # User project build template
│
├── app/
│   ├── src/main/
│   │   ├── java/com/buildx/ide/
│   │   │   ├── api/
│   │   │   │   ├── APIClient.kt
│   │   │   │   └── GitHubAPI.kt
│   │   │   ├── model/
│   │   │   │   └── Models.kt
│   │   │   ├── repository/
│   │   │   │   └── GitHubRepository.kt
│   │   │   ├── service/
│   │   │   │   └── BuildService.kt
│   │   │   ├── ui/
│   │   │   │   ├── main/
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   ├── editor/
│   │   │   │   │   └── CodeEditorActivity.kt
│   │   │   │   ├── settings/
│   │   │   │   │   └── SettingsActivity.kt
│   │   │   │   └── build/
│   │   │   │       └── BuildOutputActivity.kt
│   │   │   └── BuildXApplication.kt
│   │   │
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── activity_code_editor.xml
│   │   │   │   └── ...
│   │   │   ├── menu/
│   │   │   │   ├── main_menu.xml
│   │   │   │   ├── editor_menu.xml
│   │   │   │   └── drawer_menu.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── ...
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   └── build.gradle
│
├── templates/
│   └── workflows/
│       ├── android-build.yml
│       ├── kotlin-build.yml
│       └── java-build.yml
│
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🛠️ Setup & Installation

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- GitHub Personal Access Token (for GitHub Actions API)

### Building from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/buildx-ide.git
   cd buildx-ide
   ```

2. **Open in Android Studio**
   ```bash
   android-studio .
   ```

3. **Sync Gradle and Build**
   - Let Gradle sync automatically
   - Run `./gradlew assembleDebug` to build debug APK

4. **Install on Device**
   ```bash
   ./gradlew installDebug
   ```

### Generating GitHub Token

1. Go to GitHub Settings → Developer Settings → Personal Access Tokens
2. Generate a new token with these scopes:
   - `repo` (Full control of private repositories)
   - `workflow` (Update GitHub Action workflows)
   - `read:org` (Read organization data)

---

## 📖 Usage Guide

### 1. Initial Setup

- Launch BuildX IDE
- Enter your GitHub Personal Access Token in Settings
- Configure default build preferences

### 2. Creating a New Project

- Tap "New Project" on home screen
- Choose project template (Android App, Library, etc.)
- Configure project settings (package name, min SDK, etc.)
- Project is created with standard Android structure

### 3. Building Projects

**Option 1: Quick Build**
- Tap "Build Project" on home screen
- Select build type (Debug/Release)
- Build triggers via GitHub Actions

**Option 2: Workflow Editor**
- Navigate to Workflows section
- Edit workflow YAML files
- Save and trigger build

**Option 3: Custom Workflows**
- Create custom `.github/workflows/*.yml` files
- Use templates provided by BuildX
- Configure build inputs and parameters

### 4. Monitoring Builds

- Real-time status updates in Build Output screen
- Notifications for build completion
- View detailed logs
- Download build artifacts (APKs)

---

## 🔧 Configuration

### Build Configuration (build.gradle)

```gradle
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.buildx.ide"
        minSdk 26
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
    }
    
    buildFeatures {
        viewBinding true
        dataBinding true
        compose true
    }
}
```

### GitHub Actions Workflow

```yaml
name: Build Android App

on:
  workflow_dispatch:
    inputs:
      build_type:
        description: 'Build Type'
        required: true
        type: choice
        options:
        - debug
        - release

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - run: ./gradlew assemble${{ github.event.inputs.build_type }}
```

---

## 🎨 Customization

### Editor Themes

BuildX supports multiple editor themes:

- **Dark** - Default dark theme
- **Light** - Light theme for bright environments  
- **Monokai** - Popular dark theme for developers
- **Solarized Dark/Light** - Scientifically optimized themes

### Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| Ctrl+S | Save file |
| Ctrl+Z | Undo |
| Ctrl+Y | Redo |
| Ctrl+F | Find |
| Ctrl+H | Replace |
| Ctrl+G | Go to line |
| Ctrl+/ | Toggle comment |
| Ctrl+D | Duplicate line |

---

## 🧪 Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Lint checks
./gradlew lint
```

### Test Coverage

```bash
./gradlew jacocoTestReport
```

---

## 🚀 CI/CD

BuildX IDE uses GitHub Actions for continuous integration:

- **Build on Push** - Every push to main/develop branches
- **Pull Request Checks** - Lint and test on PRs
- **Nightly Builds** - Scheduled builds to catch regressions
- **Release Builds** - Tagged releases produce signed APKs

---

## 📊 Performance

### Optimization Techniques

- **Multi-dex Support** - Handle large codebases
- **Lazy Loading** - Load files on demand
- **Caching** - Cache API responses and build results
- **Background Processing** - Use WorkManager for builds
- **Memory Management** - Efficient file handling

---

## 🔐 Security

- **Token Storage** - GitHub tokens encrypted in Android Keystore
- **Secure API Calls** - All API calls over HTTPS
- **No Code Execution** - Code builds on GitHub, not device
- **Scoped Storage** - Use Android scoped storage APIs

---

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2026 BuildX IDE Contributors

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

---

## 🙏 Acknowledgments

- **GitHub Actions** - For build automation infrastructure
- **Retrofit** - For type-safe HTTP client
- **Jetpack Compose** - For modern UI components
- **JGit** - For Git operations
- **Android ACE Editor** - For code editing capabilities

---

## 📞 Support

- **Documentation**: [Wiki](https://github.com/your-org/buildx-ide/wiki)
- **Issues**: [GitHub Issues](https://github.com/your-org/buildx-ide/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/buildx-ide/discussions)

---

<p align="center">
  Made with ❤️ by the BuildX IDE Team
</p>
