# BuildX IDE - Architecture Documentation

## 1. System Overview

BuildX IDE is a professional Android IDE application that provides:
- On-device code editing with syntax highlighting
- GitHub Actions integration for building projects
- Project management and file explorer
- Build monitoring and artifact downloads
- Git operations using JGit

## 2. Architecture Layers

### 2.1 Presentation Layer (UI)

**Components:**
- `MainActivity` - Home screen with quick actions and recent projects
- `CodeEditorActivity` - Code editing interface
- `SettingsActivity` - App and project settings
- `BuildOutputActivity` - Build status and logs viewer

**Design Pattern:** MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────┐
│         Activity/Fragment           │
│         (View Layer)                │
│                                     │
│  - Observes ViewModel state         │
│  - Handles user interactions        │
│  - Updates UI based on state        │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│          ViewModel                  │
│      (Presentation Logic)           │
│                                     │
│  - Holds UI state                   │
│  - Processes user actions           │
│  - Communicates with Repository     │
│  - Exposes StateFlow/LiveData       │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│         Repository                  │
│       (Data Access)                 │
│                                     │
│  - Abstracts data sources           │
│  - Caches data                      │
│  - Provides clean API to ViewModel  │
└─────────────────────────────────────┘
```

### 2.2 Domain Layer (Business Logic)

**Use Cases:**
- `TriggerBuildUseCase` - Initiates a GitHub Actions build
- `GetBuildStatusUseCase` - Retrieves current build status
- `SaveProjectUseCase` - Saves project configuration
- `CloneRepositoryUseCase` - Clones a Git repository

### 2.3 Data Layer

**Repositories:**
- `GitHubRepository` - GitHub Actions API interactions
- `ProjectRepository` - Local project storage (Room)
- `SettingsRepository` - App preferences (DataStore)
- `GitRepository` - Git operations (JGit)

**Data Sources:**
- Remote: GitHub REST API via Retrofit
- Local: Room Database, DataStore, File System

## 3. Key Components

### 3.1 GitHub Actions Integration

**API Client:**
```kotlin
APIClient
├── Base URL: https://api.github.com/
├── Authentication: Bearer token
├── Interceptors: Logging, Auth
└── Retrofit instance with Gson converter
```

**API Endpoints:**
```
POST /repos/{owner}/{repo}/actions/workflows/{workflow_id}/dispatches
GET  /repos/{owner}/{repo}/actions/runs
GET  /repos/{owner}/{repo}/actions/runs/{run_id}
GET  /repos/{owner}/{repo}/actions/runs/{run_id}/logs
PUT  /repos/{owner}/{repo}/contents/{path}
GET  /repos/{owner}/{repo}/contents/{path}
```

### 3.2 Build Service

**Foreground Service** for long-running builds:
- Shows persistent notification with build status
- Runs independently of app lifecycle
- Updates build status in real-time
- Handles build cancellation

**WorkManager** for background tasks:
- Schedules periodic build status checks
- Handles retry logic for failed builds
- Persists through app restarts

### 3.3 Code Editor

**Features:**
- Syntax highlighting for multiple languages
- Auto-completion
- Line numbers
- Code folding
- Search and replace
- Undo/Redo

**Integration:**
- Android ACE Editor library
- Custom theme support
- Keyboard shortcuts

## 4. Data Flow

### 4.1 Build Trigger Flow

```
User taps "Build" 
    ↓
MainActivity
    ↓
ViewModel.triggerBuild()
    ↓
GitHubRepository.triggerBuild()
    ↓
APIClient.triggerWorkflow()
    ↓
GitHub Actions API
    ↓
Response returned
    ↓
ViewModel updates state
    ↓
UI shows build status
```

### 4.2 Build Monitoring Flow

```
BuildService starts
    ↓
Periodically polls GitHub
    ↓
Gets workflow run status
    ↓
Updates BuildOutput in Repository
    ↓
ViewModel observes changes
    ↓
UI updates in real-time
    ↓
Notification updated
    ↓
Build completes
    ↓
Service stops
```

## 5. Database Schema

### 5.1 Room Database

**Tables:**

`projects`
- id (TEXT, PK)
- name (TEXT)
- path (TEXT)
- type (TEXT)
- createdAt (INTEGER)
- lastOpenedAt (INTEGER)
- githubOwner (TEXT)
- githubRepo (TEXT)

`builds`
- id (TEXT, PK)
- projectId (TEXT, FK)
- status (TEXT)
- startTime (INTEGER)
- endTime (INTEGER)
- output (TEXT)
- errors (TEXT)
- artifacts (TEXT)

`settings`
- key (TEXT, PK)
- value (TEXT)

### 5.2 DataStore Preferences

**Keys:**
- `github_token` - Encrypted GitHub PAT
- `default_build_type` - debug/release
- `editor_theme` - dark/light/monokai
- `editor_font_size` - integer
- `auto_save_enabled` - boolean
- `last_project_id` - string

## 6. Security Architecture

### 6.1 Token Storage

```
GitHub Token
    ↓
Encrypted with Android Keystore
    ↓
Stored in EncryptedSharedPreferences
    ↓
Only accessible by BuildX IDE
```

### 6.2 Network Security

- All API calls over HTTPS
- Certificate pinning (optional)
- Token sent in Authorization header
- No token logging

## 7. Performance Optimizations

### 7.1 Memory Management

- Lazy loading of files
- File size limits for editor
- Image compression for previews
- Bitmap recycling

### 7.2 Build Performance

- Gradle configuration caching
- Parallel build execution
- Build result caching
- Incremental compilation support

### 7.3 UI Performance

- RecyclerView with ViewPool
- DiffUtil for list updates
- Coroutines for async operations
- Debounced search inputs

## 8. Testing Strategy

### 8.1 Unit Tests

- ViewModel logic
- Repository methods
- Use cases
- Data transformations

### 8.2 Integration Tests

- API client interactions
- Database operations
- File system operations

### 8.3 UI Tests

- Activity flows
- User interactions
- Navigation

## 9. CI/CD Pipeline

```
Push to GitHub
    ↓
GitHub Actions triggered
    ↓
Checkout code
    ↓
Setup JDK 17
    ↓
Gradle cache restore
    ↓
Run lint checks
    ↓
Run unit tests
    ↓
Build debug APK
    ↓
Build release APK
    ↓
Upload artifacts
    ↓
Notify on success/failure
```

## 10. Future Enhancements

### 10.1 AI Features

- Code completion using LLM
- Error explanation
- Code refactoring suggestions
- Auto-documentation generation

### 10.2 Collaboration

- Real-time code editing (CRDT)
- Code review comments
- Shared workspaces

### 10.3 Advanced Features

- Integrated terminal emulator
- Visual layout editor
- Database browser
- API testing tools
- Profiler integration

## 11. Dependencies Graph

```
BuildX IDE
├── AndroidX Core
│   ├── AppCompat
│   ├── Material Design
│   ├── ConstraintLayout
│   └── Lifecycle
│
├── Jetpack
│   ├── Compose
│   ├── Navigation
│   ├── Room
│   ├── DataStore
│   └── WorkManager
│
├── Networking
│   ├── Retrofit
│   ├── OkHttp
│   └── Gson
│
├── Coroutines
│   ├── kotlinx-coroutines-core
│   └── kotlinx-coroutines-android
│
├── Git
│   └── JGit
│
├── Editor
│   └── Android ACE Editor
│
└── UI
    ├── Material Dialogs
    └── Coil
```

## 12. Build Configuration Matrix

| Configuration | Debug | Release |
|--------------|-------|---------|
| minifyEnabled | false | true |
| debuggable | true | false |
| applicationIdSuffix | .debug | - |
| versionNameSuffix | -DEBUG | - |
| ProGuard | No | Yes |
| Signing | Debug keystore | Release keystore |

---

*Document Version: 1.0*
*Last Updated: 2026-04-14*
