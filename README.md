# Step Counter Health App

A comprehensive Android fitness tracking application that monitors your steps, distance, speed, and walking routes using Google Maps integration.

## Features

- Real-time step counting
- GPS route tracking with Google Maps
- Session history and statistics
- Calorie tracking
- Duration and speed monitoring
- Local database storage with Room

## Setup Instructions

### Prerequisites

- Android Studio (latest version)
- Android SDK 26 or higher
- Google Maps API Key

### Getting Your Google Maps API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable "Maps SDK for Android"
4. Go to **Credentials** → **Create Credentials** → **API Key**
5. Copy your API key

### Local Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/KennethRofuli/StepCounterApp.git
   cd StepCounterApp
   ```

2. Create `local.properties` file in the root directory:
   ```properties
   sdk.dir=YOUR_SDK_PATH
   MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY_HERE
   ```

3. Open project in Android Studio

4. Sync Gradle files

5. Run the app on your device or emulator

### CI/CD Setup (GitHub Actions)

To enable the CI/CD pipeline to build successfully:

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add:
   - Name: `MAPS_API_KEY`
   - Value: Your Google Maps API key

## Running Tests

### Unit Tests
```bash
./gradlew test
```

### Lint Check
```bash
./gradlew lintDebug
```

### Build APK
```bash
./gradlew assembleDebug
```

## Test Coverage

- **StepCalculationsTest**: Distance, speed, and time calculations
- **StepSessionTest**: Database entity validation
- **ConvertersTest**: JSON serialization for GPS paths

All 46+ unit tests using JUnit and Mockito.

## CI/CD Pipeline

The GitHub Actions workflow automatically:
- Builds the app
- Runs all unit tests
- Performs lint checks
- Generates debug APK
- Validates project structure

## Permissions Required

- `ACTIVITY_RECOGNITION` - Step detection
- `ACCESS_FINE_LOCATION` - GPS tracking
- `ACCESS_BACKGROUND_LOCATION` - Background tracking
- `FOREGROUND_SERVICE` - Background service
- `INTERNET` - Map data

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/stepcounterapp/
│   │   │   ├── MainActivity.java
│   │   │   ├── HomeActivity.java
│   │   │   ├── PastWalkActivity.java
│   │   │   ├── SessionDetailActivity.java
│   │   │   ├── StepTrackingService.java
│   │   │   ├── AppDatabase.java
│   │   │   └── ...
│   │   └── AndroidManifest.xml
│   └── test/
│       └── java/com/example/stepcounterapp/
│           ├── StepCalculationsTest.java
│           ├── StepSessionTest.java
│           └── ConvertersTest.java
└── build.gradle.kts
```

## Technologies Used

- **Language**: Java
- **Architecture**: Room Database, Service-based architecture
- **Testing**: JUnit, Mockito
- **Maps**: Google Maps SDK
- **Location**: FusedLocationProviderClient
- **Sensors**: Android Step Detector
- **UI**: Material Design components

## Security Note

**Never commit API keys to version control!**

- API keys are stored in `local.properties` (gitignored)
- CI/CD uses GitHub Secrets
- Placeholder key used when neither is available

## License

This project is for educational purposes.

## Author

Kenneth Rofuli
