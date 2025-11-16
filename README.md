# Adaptive Study Planner

Aplikasi desktop untuk membantu mahasiswa mengelola jadwal belajar dengan teknik Spaced Repetition (SM-2) dan Interleaving.

## Quick Start

### Prerequisites
- Java Development Kit (JDK) 25+
- Maven 3.6+

### Installation & Run

```bash
# Clone repository
git clone https://github.com/zarzet/project-langithijau.git
cd project-langithijau

# Build project
mvn clean install

# Run application
mvn javafx:run
```

### Alternative Run Scripts
- Windows: `run.bat`
- Linux/Mac: `./run.sh`

## Key Features

- Manajemen mata kuliah dan topik
- Auto-generate jadwal belajar dengan algoritma SM-2
- Tracking progress dan review scheduling
- Dashboard tugas harian
- Dark mode support with custom window decoration
- Real-time analog clock widget
- Weather widget with live weather data

## Technology Stack

- Java 25
- JavaFX 25
- SQLite 3.47
- Maven

## Documentation

Dokumentasi lengkap tersedia di folder `docs/`:
- `docs/DOKUMENTASI_PROYEK.md` - Dokumentasi lengkap proyek
- `docs/USE_CASE_DIAGRAM.md` - Use case dan diagram
- `docs/USE_CASE_DETAIL_DOSEN_ADMIN.md` - Detail use case dosen & admin
- `docs/CHANGELOG.md` - Riwayat perubahan
- `docs/WEATHER_API_SETUP.md` - Weather API configuration guide

## Project Structure

```
project-langithijau/
├── src/main/
│   ├── java/com/studyplanner/
│   │   ├── algorithm/         # SM-2 & Schedule Generator
│   │   ├── controller/        # JavaFX Controllers
│   │   ├── database/          # SQLite Manager
│   │   ├── model/             # Data Models
│   │   └── MainApp.java       # Entry Point
│   └── resources/
│       ├── css/               # Stylesheets (dark mode support)
│       └── fxml/              # UI Layouts
├── docs/                      # Documentation
└── pom.xml                    # Maven Config
```

## Weather Widget Setup

The application includes a weather widget that requires a free API key from OpenWeatherMap:

1. Get your free API key at [OpenWeatherMap](https://openweathermap.org/api)
2. Edit `src/main/java/com/studyplanner/component/WeatherWidget.java`
3. Replace `YOUR_API_KEY` with your actual API key
4. Rebuild: `mvn clean install`

For detailed setup instructions, see `docs/WEATHER_API_SETUP.md`

## License

Educational project.