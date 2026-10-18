# DPDP-Compliance-Architect-beta-version-

DPDP Compliance Architect beta version, developed by Adv. Akash Sharma.

## Features

* **Compliance Dashboard & Metrics**: Track compliance metrics, action items, and status with `ComplianceDashboard`, `ComplianceHealthChart`, `DashboardTab`, and `ComplianceScore`.
* **Agentic Audit & AI Integration**: Automated compliance auditing via `AgenticAuditorService` and remote processing with `GeminiService`.
* **Compliance Guide & Legal Documentation**: Access statutory guidance and generated legal documents through `ComplianceGuideService`, `ComplianceGuideScreen`, and `ComplianceDocs`.
* **Data Subject Rights & Erasure**: Manage data erasure workflows using `ErasureHandler` and capture consent notices with `ConsentDisclaimer`.
* **Intake & URL Processing**: Support for user and site data intake using `IntakeFormCard` and `UrlInputComponent`.
* **Legal Updates**: Real-time legal tracking through `LegalUpdateFeed`.
* **Authentication & Access Control**: Authentication support via `GoogleAuthService`, `PrivateLoginScreen`, `AccessDeniedScreen`, and role-based administration with `AdminUserManagementScreen`.
* **Local Data Persistence**: Structured data storage using `AppDatabase`, `Entities`, and `Daos`.

## Tech Stack

* **Language**: Kotlin
* **Platform**: Android
* **UI**: Android UI / Jetpack Compose components and themes
* **Database**: Local persistence via Room/SQLite (`AppDatabase`, `Daos`, `Entities`)
* **AI / Services**: Gemini API integration (`GeminiService`), Google Authentication (`GoogleAuthService`)
* **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`, `libs.versions.toml`)
* **Testing**: JUnit, Robolectric, AndroidX InstrumentedTest

## Setup

Detailed setup commands are not specified in the repository. The project is a Gradle-based Android application.

General configuration visible from the repository:
1. Review `.env.example` and configure the required environment variables.
2. Open and sync the project in an Android-compatible IDE (such as Android Studio) or build with Gradle.

## Usage

* Run the application on an Android device or emulator starting from `MainActivity`.
* Authenticate using private login or Google authentication.
* Use `MainComplianceScreen` and `ComplianceDashboard` to view compliance health, review action items, consult compliance guides, and access administrative settings.

## Project Structure

```text
DPDP-Compliance-Architect-beta-version-/
├── .env.example
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── metadata.json
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── assets/
│   └── .aistudio/
│       └── .gitignore
└── app/
    ├── .gitignore
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        ├── androidTest/
        │   └── java/com/example/ExampleInstrumentedTest.kt
        ├── test/
        │   └── java/com/example/
        │       ├── AuthAndAccessControlTest.kt
        │       ├── ExampleRobolectricTest.kt
        │       └── ExampleUnitTest.kt
        └── main/
            ├── AndroidManifest.xml
            ├── java/com/example/
            │   ├── MainActivity.kt
            │   ├── data/
            │   │   ├── AgenticAuditorService.kt
            │   │   ├── AppDatabase.kt
            │   │   ├── AuthModels.kt
            │   │   ├── ComplianceGuideData.kt
            │   │   ├── ComplianceGuideService.kt
            │   │   ├── ComplianceRepository.kt
            │   │   ├── ComplianceScore.kt
            │   │   ├── Daos.kt
            │   │   ├── Entities.kt
            │   │   ├── ErasureHandler.kt
            │   │   ├── GoogleAuthService.kt
            │   │   └── remote/
            │   │       └── GeminiService.kt
            │   └── ui/
            │       ├── ComplianceViewModel.kt
            │       ├── components/
            │       │   ├── ComplianceDashboard.kt
            │       │   └── ConsentDisclaimer.kt
            │       ├── documents/
            │       │   └── ComplianceDocs.kt
            │       ├── screens/
            │       │   ├── AccessDeniedScreen.kt
            │       │   ├── ActionItemCard.kt
            │       │   ├── AdminUserManagementScreen.kt
            │       │   ├── ComplianceGuideScreen.kt
            │       │   ├── ComplianceHealthChart.kt
            │       │   ├── DashboardTab.kt
            │       │   ├── ExtraComponents.kt
            │       │   ├── IndianTasteComponents.kt
            │       │   ├── IntakeFormCard.kt
            │       │   ├── LegalUpdateFeed.kt
            │       │   ├── MainComplianceScreen.kt
            │       │   ├── PrivateLoginScreen.kt
            │       │   └── UrlInputComponent.kt
            │       └── theme/
            │           ├── Color.kt
            │           ├── Theme.kt
            │           └── Type.kt
            └── res/
                ├── drawable/
                ├── mipmap-*/
                ├── values/
                │   ├── colors.xml
                │   ├── strings.xml
                │   └── themes.xml
                └── xml/
                    ├── backup_rules.xml
                    └── data_extraction_rules.xml

## Configuration

Set the bootstrap admin email in `app/src/main/java/com/example/data/AppConfig.kt` (`ADMIN_EMAIL`, placeholder `admin@example.com`) before building. Not built or tested here; build in Android Studio.
