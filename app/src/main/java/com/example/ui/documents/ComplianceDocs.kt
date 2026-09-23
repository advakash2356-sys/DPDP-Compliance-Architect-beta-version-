package com.example.ui.documents

object ComplianceDocs {
    val prd = """
        # Product Requirements Document (PRD)
        
        ## 1. Problem Statement
        With the enforcement of the Digital Personal Data Protection (DPDP) Act 2023, Indian digital platforms can no longer collect, store, or process personal identifiers (names, emails, cell numbers) without explicit, modular, revocable, and audited consent. Engineering groups lack reference architectures showing how to build consent ledgers, automated purging mechanisms, user rights workflows (erasure, correction, nomination), and rapid 72-hour breach alert modules into local database schemas.
        
        ## 2. Target Users
        - **Legal Consultants & Advocates** (e.g., Adv. Akash) verifying standard compliance of user interfaces.
        - **Systems Architects & Engineers** seeking explicit reference implementations of DPDP protocols.
        - **Data Protection Officers (DPOs)** executing simulated risk containment drills.
        
        ## 3. Core Features (Must-Haves)
        - **Granular Consent Sandboxing:** Consent forms must be separate for account signup versus secondary marketing, with strict pre-consent check prior to OTP collection.
        - **Minimization Ledger:** Interactive inventory of all stored variables with configurable automated purge actions.
        - **User Rights Portals:** Immediate one-click account scrubbing, self-serve detail correction, and legal nominee selection fields.
        - **Breach Alert Simulator:** 72-hour countdown simulator triggering regulatory board notices and affected user advisory packages.
        
        ## 4. Success Metrics
        - **Consent Audit Rate:** 100% of state change transactions recorded in an immutable ledger.
        - **Erasure Speed:** Local database scrubbed within 5 seconds of request trigger.
        - **Breach Mitigation window:** Documented alert dispatch under 72 hours.
    """.trimIndent()

    val technicalArch = """
        # Technical Architecture Document
        
        ## 1. Technology Stack Selection
        - **Language:** Kotlin 2.2 (Modern idiomatic types)
        - **UI Layer:** Jetpack Compose (Edge-to-edge Material 3 layouts)
        - **Persistence:** SQLite managed via Room Persistence Library with KSP symbol processor
        - **Architecture:** Clean MVVM (Model-View-ViewModel) with unidirectional flow
        
        ## 2. Database Schema
        - **`user_profiles` table:** Contains active registration indicators. Uses explicit boolean flags (`hasAccountConsent`, `hasMarketingConsent`) to enforce programmatic segregation.
        - **`consent_logs` table:** Immutable state mutation ledger. Keys: ID, ConsentType string, Action, Details transcript, and a Millisecond Epoch timestamp tracker.
        - **`nominees` table:** Legal delegates designated under Section 13. Stores delegate name, link, and contact detail string markers.
        - **`grievance_reports` table:** Local compliance grievance registry.
        
        ## 3. Folder & Package Structure
        - `com.example.data.*` — Persistent Entity objects, Room DAOs, and the Repository connector.
        - `com.example.ui.*` — Dynamic Compose screens, local themes, and Android ComplianceViewModel states.
        - `com.example.ui.documents.*` — Structured specification databases.
        
        ## 4. Programmatic Data Minimization Timer
        Outdated state variables are evaluated by a background Purge Daemon. Outdated telemetry rows are minimized to zero bytes on trigger.
    """.trimIndent()

    val securityAccess = """
        # Security & Access Document
        
        ## 1. Zero-Trust Access Protocol
        - User authentication must reject all hardcoded passcodes. Simulated OTP generation ("2356") is linked to verified phone nodes.
        - Server-side validations reject OTP verification attempts that exceed a strict 10-minute validity threshold.
        
        ## 2. Cryptographic Hashing at Rest
        - Cell phone numbers, being direct Personal Identifiers (PII), must be obscured.
        - On receipt, phone fields are converted in-memory via a SHA-256 secure hash generator, ensuring we only record cryptographically unique fingerprints for transaction logging.
        
        ## 3. Row-Level Security & Role Enforcement
        - The local profile is isolated to a single hardcoded active record `local_user`. No profile can browse parallel profile keys, enforcing logical Sandbox confinement.
        
        ## 4. Error Logging Safeguards
        - Exception handlers are configured across all database writes. SQLite constraint mismatches trigger generic error boundaries in the ViewModel, avoiding any raw query or stack-trace leaks into user screens.
    """.trimIndent()

    val frontendSpec = """
        # Frontend Specification Document
        
        ## 1. Color System (Material 3)
        Utilising the premium dynamic Slate Blue accent system:
        - **Primary Color:** `#3F51B5` (Indicates secure trust status)
        - **Secondary / Accent:** `#009688` (Emphasises compliance actions)
        - **Dark Canvas Background:** `#121212` (Eye-safe modern slate motif)
        - **Alert Container:** `#D32F2F` (Indicates active incident simulation)
        
        ## 2. Accessibility Guidelines
        - **Touch Targets:** Minimum dimension of 48.dp is mandated on all buttons, sliders, list toggles, and form inputs.
        - **Contrast:** AA compliant contrast ratios across dark text overlays to avoid reading strain.
        - **Labels:** Meticulous content description overlays provided on security status badges.
        
        ## 3. Microservice Integrations & SDK Auditing
        - Standard default SDK analytical packages must be explicitly list-disclosed on the dashboard.
        - Optional trackers are shut down on the frontend if the user revokes the default telemetry switch.
    """.trimIndent()

    val ticketList = listOf(
        Ticket("DPDP-101", "Zero-Trust Consent Validation Screen", "Implement a phone signup card that enforces explicit pre-OTP consent before allowing numerical phone characters or triggering simulated SMS relays.", "High", "None"),
        Ticket("DPDP-102", "Granular Consent Log Logger", "Integrate Room database triggers that automatically record an audit entry inside the consent ledger every time any user accepts or revokes optional marketing or telemetry policies.", "High", "DPDP-101"),
        Ticket("DPDP-103", "One-Click Data Erasure (Sec 12)", "Design a danger-zone wipe button. Triggering this button must physically run deletion actions on all Room profiles, nominations, and logs, returning the database to empty states.", "Critical", "DPDP-102"),
        Ticket("DPDP-104", "Right to Nominate Designations", "Create an India-compliant Section 13 Nominee registry form, recording delegate contact identifiers securely, with complete local database validation.", "Medium", "None"),
        Ticket("DPDP-105", "72-Hour Rapid Breach Simulator", "Build an emergency response dry-run utility containing a ticking regulatory countdown clock, alert banners, and simulated regulatory reporting callbacks.", "High", "DPDP-101")
    )
}

data class Ticket(
    val id: String,
    val title: String,
    val description: String,
    val priority: String,
    val dependencies: String
)
