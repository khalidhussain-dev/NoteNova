# NoteNova

A modern Android notes-taking application built with **Kotlin**, **Jetpack Compose**, and **MVVM architecture**. NoteNova offers secure local authentication, rich note management, and a polished Material 3 interface with dark/light theme support.

**Latest Update:** Fully functional note-taking app with user authentication, secure password hashing, and comprehensive note management system (May 2026)

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square" alt="Platform" />
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat-square" alt="Compose" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-orange?style=flat-square" alt="Min SDK" />
  <img src="https://img.shields.io/badge/License-Educational-lightgrey?style=flat-square" alt="License" />
</p>

**Repository:** [github.com/khalidhussain-dev/NoteNova](https://github.com/khalidhussain-dev/NoteNova)

---

## Table of Contents

- [Features](#features)
  - [Authentication](#authentication-1)
  - [Notes Management](#notes-management)
  - [Limits & UX](#limits--ux)
  - [UI/UX](#uiux)
  - [Performance & Reliability](#performance--reliability)
  - [Accessibility](#accessibility)
- [Installation](#installation)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running the App](#running-the-app)
- [Build from Command Line](#build-from-command-line)
- [Authentication](#authentication)
- [Notes System](#notes-system)
- [Data & Security](#data--security)
- [Dependencies](#dependencies)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

## Features

### Authentication
- User registration with full name, email, and password
- Login with **Remember Me** session persistence
- Password hashing (SHA-256 + per-user salt)
- Inline form validation with Material 3 error styling
- Strong password rules on registration (8+ chars, uppercase, lowercase, number)
- Session managed via **DataStore Preferences**

### Notes Management
- Full **CRUD** operations (Create, Read, Update, Delete)
- Note fields: title, content, created/updated timestamps
- **Pin** and **favorite** notes
- **6 color categories** for visual organization
- **Search** across title, content, and keywords
- **Filter** by All / Pinned / Favorites
- **Sort** by date or title (ascending/descending)
- **Grid** and **list** view toggle
- **Auto-save** in the note editor (~1.2s debounce)
- Delete confirmation dialog + **Undo** snackbar

### Limits & UX
- **20 notes per user** maximum (Room-backed count)
- Live quota card: `X / 20 Notes Remaining` with progress bar
- Warning when near limit; FAB disabled at limit
- Animated splash screen
- Empty states for no notes / no search results
- Smooth navigation transitions

### UI/UX
- Material 3 design system
- Custom indigo/teal brand palette
- Dark and light theme (follows system)
- Edge-to-edge layout
- Reusable Compose components

### Performance & Reliability
- **Optimized Database Queries** - Indices on frequently queried columns for fast lookups
- **Efficient Coroutine-Based Async** - Non-blocking I/O with Kotlin coroutines for smooth UI
- **Memory-Efficient State Management** - StateFlow prevents unnecessary recompositions
- **Crash-Free UX** - Comprehensive error handling and graceful failure recovery
- **Database Integrity** - Foreign key constraints and CASCADE deletion for data consistency
- **Performance Monitoring** - Optimized Room queries with proper pagination support

### Accessibility
- **Touch-Friendly UI Components** - Large, easy-to-tap buttons and interactive elements
- **Screen Reader Support** - Proper content descriptions and semantic accessibility labels
- **High Contrast Color Schemes** - Material 3 colors ensure readability in light and dark modes
- **Keyboard Navigation** - Full keyboard support for accessibility-focused users
- **Proper Focus Management** - Clear focus indicators on interactive elements

---

## Installation

### Prerequisites
- **Android Studio** - Ladybug (2024.2.1) or later recommended
- **JDK 11 or higher** - Android Studio includes bundled JBR
- **Android SDK 24+** - API Level 24+ for runtime compatibility
- **Git** - For cloning the repository

### Step-by-Step Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/khalidhussain-dev/NoteNova.git
   cd NoteNova
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Click **File → Open** → Select the cloned `NoteNova` directory
   - Accept the project structure and allow Android Studio to recognize it

3. **Gradle Sync:**
   - Android Studio will automatically trigger a Gradle sync
   - Wait for the sync to complete (this may take several minutes on first run)
   - Accept any SDK license agreements if prompted
   - Let Android Studio download missing components automatically

4. **Verify Setup:**
   - Ensure `local.properties` is created automatically with your SDK path
   - No manual configuration is needed for local development

### Build and Run Instructions

**Debug APK Build:**
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

**Run on Emulator:**
- Open **Device Manager** (Tools → Device Manager)
- Create a virtual device if needed (e.g., Pixel 7 with API 34+)
- Select the emulator in the toolbar and click **Run ▶** or press `Shift + F10`

**Run on Physical Device:**
- Enable **Developer Options** and **USB Debugging** on your device
- Connect via USB and authorize the computer
- Select your device in Android Studio and click **Run ▶**

---

## Screenshots

> Add emulator screenshots here after running the app.

| Splash | Login | Home |
|--------|-------|------|
| *Coming soon* | *Coming soon* | *Coming soon* |

| Register | Note Editor | Quota Card |
|----------|-------------|------------|
| *Coming soon* | *Coming soon* | *Coming soon* |

---

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| Database | Room (SQLite) |
| Async | Kotlin Coroutines + StateFlow |
| Navigation | Navigation Compose |
| Session | DataStore Preferences |
| DI | Manual (`AppContainer`) |
| Build | Gradle 9.2.1 + AGP 9.0.1 |
| Annotation Processing | KSP |

---

## Architecture

NoteNova follows a layered **MVVM** pattern with unidirectional data flow:

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  Compose Screens · ViewModels · Navigation · Theme      │
└─────────────────────────┬───────────────────────────────┘
                          │ StateFlow / events
┌─────────────────────────▼───────────────────────────────┐
│                   Repository Layer                      │
│         AuthRepository · NoteRepository                 │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                    Data Layer                           │
│  Room (UserEntity, NoteEntity) · DataStore (Session)    │
└─────────────────────────────────────────────────────────┘
```

### Data flow example (Home screen)

1. `HomeViewModel` observes `NoteRepository.observeNotes()` and `observeNoteCount()`
2. Room emits updates via `Flow` when notes change
3. ViewModel applies filter, sort, and limit logic
4. UI collects `StateFlow` and recomposes automatically

### Navigation flow

```
Splash ──► Login ──► Register
   │         │
   │         └──► Home ──► Note Editor
   │
   └──► Home (if session exists)
```

---

## Project Structure

```
NoteNova/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/notestaking/
│       │   ├── MainActivity.kt              # App entry point
│       │   ├── NoteNovaApplication.kt       # Application + DI root
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── entity/              # UserEntity, NoteEntity
│       │   │   │   ├── dao/                 # UserDao, NoteDao
│       │   │   │   └── NoteNovaDatabase.kt
│       │   │   ├── preferences/
│       │   │   │   └── SessionManager.kt
│       │   │   └── repository/
│       │   │       ├── AuthRepository.kt
│       │   │       └── NoteRepository.kt
│       │   ├── di/
│       │   │   ├── AppContainer.kt
│       │   │   └── ViewModelFactory.kt
│       │   ├── util/
│       │   │   ├── AuthValidator.kt
│       │   │   ├── PasswordHasher.kt
│       │   │   ├── DateFormatter.kt
│       │   │   └── NoteLimits.kt
│       │   └── ui/
│       │       ├── theme/                   # Colors, Typography, Theme
│       │       ├── components/              # Reusable composables
│       │       ├── navigation/              # NavGraph, routes
│       │       ├── splash/
│       │       ├── auth/                    # Login, Register
│       │       ├── home/                    # Notes dashboard
│       │       └── note/                    # Create / Edit note
│       └── res/                             # Layouts, drawables, strings
├── gradle/
│   ├── libs.versions.toml                   # Version catalog
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## Getting Started

### Prerequisites

| Requirement | Version |
|-------------|---------|
| Android Studio | Ladybug (2024.2.1) or newer recommended |
| JDK | 11+ (Android Studio bundled JBR works) |
| Android SDK | API 36 (compile), API 24+ (run) |
| Git | Any recent version |

### Clone the repository

```bash
git clone https://github.com/khalidhussain-dev/NoteNova.git
cd NoteNova
```

### Android Studio setup

1. Open **Android Studio** → **File → Open** → select the project folder
2. Wait for **Gradle Sync** to finish (first sync may take several minutes)
3. If prompted, accept SDK licenses and install missing components
4. Android Studio creates `local.properties` automatically with your SDK path  
   (this file is gitignored and must not be committed)

---

## Running the App

### On an emulator

1. Open **Device Manager** (`Tools → Device Manager`)
2. Create a device if needed (e.g. **Pixel 7**, **API 34+**)
3. Select the emulator in the toolbar device dropdown
4. Click **Run ▶** (or press `Shift + F10`)

### On a physical device

1. Enable **Developer Options** and **USB Debugging** on your phone
2. Connect via USB and authorize the computer
3. Select your device in Android Studio and click **Run ▶**

### First-time usage

1. App opens on the **Splash** screen
2. Tap **Create one** to register a new account
3. After login, tap **+** to create your first note
4. Use the **delete icon** on a note card or in the editor to remove notes

---

## Build from Command Line

```bash
# macOS / Linux — use Android Studio's JBR if JAVA_HOME is unset
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Debug APK
./gradlew assembleDebug

# Output APK location
# app/build/outputs/apk/debug/app-debug.apk
```

---

## Authentication

### Registration validation

| Field | Rules |
|-------|-------|
| Full Name | Required, minimum 2 characters |
| Email | Required, valid email format |
| Password | Min 8 chars, 1 uppercase, 1 lowercase, 1 number |
| Confirm Password | Must match password |

### Login validation

| Field | Rules |
|-------|-------|
| Email | Required, valid format |
| Password | Required, minimum 8 characters |

Invalid credentials show an **inline error** on the login form (not a toast).

### Session

- Logged-in state stored in **DataStore Preferences**
- Survives app restarts when **Remember Me** is enabled
- Logout clears session and returns to Login

---

## Notes System

### Note model

| Property | Description |
|----------|-------------|
| `title` | Note heading |
| `content` | Note body |
| `createdAt` | Creation timestamp |
| `updatedAt` | Last edit timestamp |
| `isPinned` | Pin to top of list |
| `isFavorite` | Mark as favorite |
| `colorCategory` | Color index (0–5) |
| `searchKeywords` | Auto-generated search index |

### 20-note limit

- Each user can store up to **20 notes** in Room
- Home screen shows remaining quota with a progress indicator
- Creating a 21st note is blocked; delete a note to free a slot
- Undo restore respects the limit

### Delete behavior

1. Tap the **delete icon** on a note card or in the editor toolbar
2. Confirm in the Material 3 dialog
3. Note removed from Room; count updates immediately
4. **Undo** available via snackbar on the home screen

---

## Data & Security

| Data | Storage |
|------|---------|
| Users | Room `users` table |
| Notes | Room `notes` table (FK to user, CASCADE delete) |
| Session | DataStore `note_nova_session` |

**Password security:** Passwords are hashed with **SHA-256** and a random per-user salt before storage. Suitable for coursework/demo; production apps should use bcrypt, Argon2, or a backend auth service.

**Privacy:** All data stays on-device. No network calls or cloud sync.

---

## Dependencies

Managed via `gradle/libs.versions.toml`:

| Library | Purpose |
|---------|---------|
| Compose BOM + Material 3 | UI framework |
| Material Icons Extended | Icon set |
| Navigation Compose | Screen navigation |
| Lifecycle ViewModel Compose | MVVM integration |
| Room 2.7.1 + KSP | SQLite ORM |
| DataStore Preferences | Session storage |
| Activity Compose | `setContent` host |
| Core KTX | Android extensions |

---

## Configuration

### Change note limit

Edit `app/src/main/java/com/example/notestaking/util/NoteLimits.kt`:

```kotlin
const val MAX_NOTES_PER_USER = 20
const val NEAR_LIMIT_THRESHOLD = 17  // Warning when 17+ notes used
```

### SDK versions

In `app/build.gradle.kts`:

```kotlin
minSdk = 24        // Android 7.0+
targetSdk = 36
```

### Application ID

```kotlin
applicationId = "com.example.notestaking"
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Gradle sync fails | **File → Invalidate Caches → Restart**, then sync again |
| `local.properties` missing | Let Android Studio generate it, or create with `sdk.dir=/path/to/Android/sdk` |
| KSP / Kotlin errors | Ensure `android.disallowKotlinSourceSets=false` in `gradle.properties` |
| Emulator too slow | Use a **x86_64** or **ARM** image with hardware acceleration enabled |
| Java not found (CLI) | Set `JAVA_HOME` to Android Studio's JBR (see Build section) |
| Room schema changes | Bump `@Database(version = …)` and add a migration or clear app data |

---

## Contributing

We welcome contributions from the community! Here's how you can help:

### How to Contribute
1. **Fork the repository** on GitHub
2. **Create a new branch** for your feature or fix:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes** and commit with clear messages:
   ```bash
   git commit -m "Add: description of changes"
   ```
4. **Push to your fork** and create a **Pull Request** to the main repository
5. **Describe your changes** in the PR with details and motivation

### Contribution Guidelines
- Follow Kotlin coding conventions and naming standards
- Maintain the existing architecture pattern (MVVM)
- Add comments for complex logic
- Test your changes thoroughly on multiple Android versions
- Update documentation if adding new features
- Keep commits atomic and focused on single issues

### Code Style
- Use proper indentation (4 spaces)
- Name variables clearly and descriptively
- Comment non-obvious logic
- Follow Material Design principles for UI changes

### Report Issues
- Check existing issues before reporting duplicates
- Provide clear reproduction steps
- Include device info, Android version, and logs
- Be respectful and constructive

---

## Future Improvements

- [ ] Cloud sync / Firebase backend
- [ ] Rich text / markdown editor
- [ ] Note folders or tags
- [ ] Biometric app lock
- [ ] Export notes (PDF / TXT)
- [ ] Widget for quick capture
- [ ] Dependency Injection (Hilt/Koin)
- [ ] Unit & UI tests

---

## Author

**Khalid Hussain**  
GitHub: [@khalidhussain-dev](https://github.com/khalidhussain-dev)

---

<p align="center">
  Built with ❤️ using Kotlin & Jetpack Compose
</p>
