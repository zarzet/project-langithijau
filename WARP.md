# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Common commands

All commands are intended to be run from the project root (`project-langithijau`). This is a Maven-based Java 25 / JavaFX 25 desktop application using SQLite.

### Prerequisites
- JDK 25+
- Maven 3.6+ with `mvn` on PATH

### Build
- Full build (compile + tests + package):
  - `mvn clean install`
- Fast compile-only build (also runs compiler lint warnings via `-Xlint:all`):
  - `mvn clean compile`

### Run the desktop application
- Run via JavaFX Maven plugin (recommended during development):
  - `mvn javafx:run`
- Platform helper scripts (wrap `mvn javafx:run`):
  - Windows: `./run.bat`
  - Linux/macOS: `./run.sh`

### Tests
The project is configured with `maven-surefire-plugin` and JVM `--add-opens` flags so tests can access necessary JDK internals.

- Run the full test suite:
  - `mvn test`
- Run a single test class:
  - `mvn -Dtest=MyTestClass test`
- Run a single test method:
  - `mvn -Dtest=MyTestClass#myTestMethod test`

If new tests are added, place them under `src/test/java` using standard Maven conventions.

## High-level architecture

### Overview
The application is an "Adaptive Study Planner" that generates and tracks study schedules for students using:
- **Spaced Repetition (SM-2)** for review intervals
- **Interleaving** to mix topics from different courses within a day

It follows an **MVC-style architecture** with a **Repository pattern** for persistence, as documented in `docs/DOKUMENTASI_PROYEK.md`.

Core layers:
- **View (JavaFX FXML + CSS)** — UI layouts and styling
- **Controller (JavaFX controllers)** — orchestrates user actions, calls domain logic and repositories
- **Model (domain entities)** — courses, topics, exams, study sessions, progress
- **Algorithm (scheduling logic)** — SM-2 and interleaving implementation
- **Persistence (SQLite via repositories)** — CRUD and queries over the domain model

### Source layout
Key locations (Java packages under `src/main/java/com/studyplanner/`):

- `MainApp` (root package)
  - JavaFX `Application` entry point.
  - Boots the app by loading `/fxml/MainView.fxml`, attaching `/css/style.css`, and configuring the primary `Stage`.
  - Other parts of the UI are composed from additional FXML views.

- `algorithm/`
  - Contains the **SM-2 spaced repetition** implementation and **interleaving** scheduler.
  - Uses domain models (courses, topics, exams, sessions) and configuration parameters to:
    - Calculate review intervals and easiness factors (SM-2) based on performance ratings.
    - Generate a **7-day rolling study schedule** that:
      - Prioritises topics by difficulty, user-defined priority, upcoming exams, and review needs.
      - Mixes topics from multiple courses in a single day.
  - Controllers call into this layer to (re)generate daily schedules and update next-review timestamps after a session is completed.

- `model/`
  - Holds the **domain entities** reflecting the concepts described in `docs/DOKUMENTASI_PROYEK.md`:
    - Courses / subjects
    - Topics with difficulty, priority, and mastery status
    - Exams / assessments with dates
    - Study sessions / review events with performance ratings and intervals
  - Encapsulates business-related attributes needed by the SM-2 algorithm and the interleaving scheduler.
  - Designed to be used both by the `algorithm` layer and the `database` repositories.

- `database/`
  - Acts as the **SQLite manager and repository layer**.
  - Responsibilities typically include:
    - Managing the SQLite connection and schema.
    - Mapping between domain models (`model/`) and database tables.
    - Providing repository-style APIs (e.g., load/save courses, topics, sessions, exams, progress metrics).
  - All persistence should go through this layer rather than being accessed directly from controllers.

- `controller/`
  - JavaFX controllers that implement the **Controller** part of MVC.
  - Responsibilities typically include:
    - Handling UI events from FXML views (button clicks, form submissions, selections).
    - Coordinating between the `algorithm` and `database` layers:
      - Persisting new/updated domain objects (courses, topics, exams).
      - Triggering schedule generation and updates.
    - Updating observable UI state and view components with data from the domain model.
  - Controllers should avoid embedding business rules that belong in `algorithm/` or `model/` to keep responsibilities separated.

### UI resources
Located under `src/main/resources/`:

- `fxml/`
  - `MainView.fxml` — main application window / dashboard.
  - `CourseManagement.fxml` — interfaces for managing courses and topics.
  - `ScheduleView.fxml` — views for the generated study schedule and daily tasks.
- `css/`
  - `style.css` — main stylesheet, includes dark mode support and visual styling for the JavaFX UI.

When adding or modifying UI:
- Define layout in FXML, bind it to a controller under `controller/`.
- Keep styling in CSS rather than inline in FXML.

### Documentation-driven design
The `docs/` directory contains the conceptual and functional design of the system and should be consulted for non-trivial changes:

- `docs/DOKUMENTASI_PROYEK.md`
  - Describes the problem domain, goals, and **key features**, including SM-2 and interleaving behaviour, mastery rules, priority matrix, and planned analytics.
  - Also documents roles (Mahasiswa, Dosen, Administrator) and planned features beyond the current UI.
- `docs/USE_CASE_DIAGRAM.md` and `docs/USE_CASE_DETAIL_DOSEN_ADMIN.md`
  - Detail use cases and flows for different roles.
- `docs/CHANGELOG.md`
  - Tracks evolution of the project; check here when you need historical context for behaviour changes.

For any major modifications to scheduling behaviour, role support, or progress tracking, align the implementation with these documents and update them if the behaviour changes.
