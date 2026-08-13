# NoteNova App – Modern Local-First Notes Taking Application

NoteNova is a secure, native Android mobile application designed to manage personal notes on-device. The application features user authentication with hashed credentials, reactive data flows, and a strict per-user storage quota system. Notes are stored locally, providing users with absolute privacy, robust offline access, and a clean Material 3 design.

**GitHub Repository:** [https://github.com/khalidhussain-dev/NoteNova](https://github.com/khalidhussain-dev/NoteNova)

### Key Features
* Secure local user registration and login
* Dynamic session management (Remember Me / Auto-login)
* Password hashing using SHA-256 and unique per-user salt keys
* Full note CRUD (Create, Read, Update, Delete) operations
* Interactive sorting (by date/title) and filtering (All, Pinned, Favorites)
* Organization via pins, favorites, and 6 custom color categories
* Auto-save editor with 1200ms debouncing logic
* User note quota system (enforced 20 notes limit per user) with live progress indicators
* Interactive UX patterns including delete confirmation prompts and undo snackbar actions
* Dynamic Material 3 design supporting both light and dark themes

### Technologies Used
* Kotlin
* Jetpack Compose (Declarative UI)
* Room Database (SQLite ORM)
* Kotlin Symbol Processing (KSP)
* Kotlin Coroutines & Flow (Reactive Data Streams)
* DataStore Preferences (Session persistence)
* Jetpack Navigation (Compose Navigation Graph)
* Material Design 3

### Responsibilities
* Engineered the local user registration, login authentication, and security workflows using SHA-256 password hashing.
* Implemented the database persistence layer using Room Database with custom entities, DAOs, and relationship structures.
* Built view model logic (MVVM architecture) orchestrating StateFlow to manage UI states reactively.
* Developed search, filter (All, Pinned, Favorites), and sorting mechanisms for real-time note retrieval.
* Implemented the auto-save functionality for notes with debounced coroutine delays (1200ms).
* Designed user quota restrictions (maximum 20 notes) and integrated visual feedback cards with warning thresholds.
* Formulated clean user interfaces using Jetpack Compose and Material 3 design system components.
