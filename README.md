# DPDP Compliance Architect

An educational workspace on India's **Digital Personal Data Protection Act, 2023** and **DPDP Rules, 2025**. For your assistance only, not legal advice. Built with Akash Sync Agent.

**Live:** https://dpdp-compliance-architect-beta-vers.vercel.app

> **Educational, not legal advice.** Verify against the official texts on MeitY.

## Web app (`web/` + `api/`, deployed)
- **Subscription model:** the landing page is public; the workspace needs Google sign-in. There is a 7-day free trial stored server-side, then a paywall (payments are off: "Subscription coming soon"). Every tool is gated server-side. Setup: see `SUBSCRIPTION_SETUP.md`.
- Legal pages: pricing, terms, privacy, refund and cancellation.
- **Self-assessment:** 12 checks, each cited to a section or Rule, giving a readiness percentage and a gap list. Answers are saved only in your browser (localStorage), and you can print the result.
- **Notice drafter:** a Section 5 / Rule 3 notice template.
- **Breach clock:** the Rule 7 72-hour deadline calculator.
- **Section guide and penalty Schedule (s. 33):** verified against the Gazette texts.
- Light and dark modes, mobile layout, WCAG AA and reduced motion. Landing page: GSAP ScrollTrigger pinned scenes, Lenis smooth scroll, layered parallax and canvas dust.

Run locally: `npm i && DEV_AUTH=1 npm run dev` (local test sign-in only). Test: `npm test`. Vercel uses `vercel.json` (output `web/`).
See `AUDIT.md`, `DESIGN.md` and `THIRD_PARTY_NOTICES.md`.

## Android app (original, `app/`, untested)
Several features in the Android app are simulated (URL scanner, live feed, OTP). See AUDIT.md.

## Features

* **Compliance Dashboard & Metrics**: Track compliance metrics, action items, and status with `ComplianceDashboard`, `ComplianceHealthChart`, `DashboardTab`, and `ComplianceScore`.
* **Agentic Audit & AI Integration**: Automated compliance auditing via `AgenticAuditorService` and remote processing with `GeminiService`.
* **Compliance Guide & Legal Documentation**: Access statutory guidance and generated legal documents through `ComplianceGuideService`, `ComplianceGuideScreen`, and `ComplianceDocs`.
* **Data Subject Rights & Erasure**: Manage data erasure workflows using `ErasureHandler` and capture consent notices with `ConsentDisclaimer`.
* **Intake & URL Processing**: Support for user and site data intake using `IntakeFormCard` and `UrlInputComponent`.
* **Legal Updates**: Manually entered legal updates via `LegalUpdateFeed` (not live tracking).
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

## Add your API key

AI features need your own key (free Gemini key: https://aistudio.google.com/apikey). No key is committed to this repo.

1. Copy `.env.example` to `.env` in the project root (`.env` is git-ignored; check `.gitignore` before committing).
2. Add: `GEMINI_API_KEY=your_key_here`
3. Rebuild in Android Studio. The Secrets Gradle plugin exposes it as `BuildConfig`. Without a key the app uses its built-in fallbacks.

**Privacy:** the key stays in your own `.env` / your build, is never committed or logged, and is sent only to the AI provider's API. Delete the line and rebuild to remove it. There is no in-app key screen yet because these Android apps are untested in this environment; a settings screen is a planned follow-up.
