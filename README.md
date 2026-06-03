# HeartHealth

A personal, local-only Android app for managing heart health — built with Kotlin, Jetpack Compose, Room, and the Google Generative AI (Gemini) SDK.

## Features (Phase 1)

- **Medication Entry** — Add medications with name and dosage
- **Dosage Scheduler** — Set multiple reminder times per medication, powered by AlarmManager for local push notifications
- **Status Toggle** — Mark medications Active/Inactive; inactive silences reminders without deleting data
- **Daily Journal** — Quick text entries for logging feelings and symptoms post-dosage
- **Lifestyle Mock-Injector** — Simulate daily exercise/heart-rate/step data with one tap
- **Medication Deep-Dive** — Tap any medication to get an AI-powered breakdown via Gemini (purpose, mechanism, side effects, tips)

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Local DB | Room (SQLite) |
| Notifications | AlarmManager + NotificationCompat |
| AI | Google Generative AI SDK (Gemini 2.0 Flash) |
| Build | Gradle 8.9 / AGP 8.7.3 |
| Min SDK | 26 (Android 8.0) |

## Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2+) or JDK 17+ with Android SDK 35
- Android device or emulator running API 26+

### Build
```bash
# Debug APK
./gradlew assembleDebug

# APK location
app/build/outputs/apk/debug/app-debug.apk
```

### Gemini API Key
To enable the Medication Deep-Dive feature, add your Gemini API key:

```properties
# local.properties (project root – not committed)
GEMINI_API_KEY=your_api_key_here
```

Get a key at https://aistudio.google.com/apikey

## Project Structure

```
app/src/main/java/com/hearthealth/app/
├── HeartHealthApp.kt          # Application class (notification channel)
├── MainActivity.kt            # Single-activity entry point
├── data/
│   ├── entity/                # Room entities (Medication, Reminder, JournalEntry, LifestyleData)
│   ├── dao/                   # DAOs for each entity
│   └── db/AppDatabase.kt     # Room database singleton
├── notification/
│   ├── ReminderAlarmReceiver.kt  # BroadcastReceiver for alarms
│   ├── ReminderScheduler.kt     # AlarmManager scheduling logic
│   └── BootReceiver.kt          # Re-schedules alarms after reboot
└── ui/
    ├── Navigation.kt          # NavHost with all routes
    ├── theme/Theme.kt         # Material 3 color scheme
    ├── viewmodel/             # ViewModels for each feature
    └── screens/               # Compose UI screens
```

## Phase 2 (Planned)
- Firebase Firestore/Auth integration for cloud syncing
- Real wearable data via Health Connect API
- Medication interaction checker
- Export/share reports
