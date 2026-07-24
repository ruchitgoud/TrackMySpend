# Implementation Plan - TrackMySpend

Recreate the TrackMySpend app (Neo-Brutalist Expense Tracker) in Jetpack Compose, following the design and features from `index.html`.

## Proposed Changes

### Project Configuration
#### [MODIFY] [libs.versions.toml](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/gradle/libs.versions.toml)
- Add Room (entity, dao, database).
- Add Navigation Compose.
- Add ViewModel Compose.
- Add KSP plugin.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/build.gradle.kts)
- Apply KSP plugin.
- Add Room, Navigation, and ViewModel dependencies.

### Data Layer
#### [NEW] [Transaction.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/data/Transaction.kt)
- Data class for transactions (id, description, amount, type, date).

#### [NEW] [TransactionDao.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/data/TransactionDao.kt)
- Room DAO for CRUD operations and StateFlow streams.

#### [NEW] [AppDatabase.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/data/AppDatabase.kt)
- Room database configuration.

#### [NEW] [TransactionRepository.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/data/TransactionRepository.kt)
- Repository to abstract data source.

### UI Layer - Theme & Components
#### [MODIFY] [Color.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/ui/theme/Color.kt)
- Define Neo-Brutalist colors: Soft Yellow (#FEF3C7), Peach (#FFC98B), Mint (#D1EAE5), Light Pink (#FEE2E2), and pure Black for outlines.

#### [NEW] [BrutalistComponents.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/ui/components/BrutalistComponents.kt)
- `BrutalistCard`, `BrutalistButton`, `BrutalistTextField`, `BrutalistDialog`.

### UI Layer - Features
#### [NEW] [TransactionViewModel.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/ui/viewmodel/TransactionViewModel.kt)
- Manage transaction list, summary calculation, and CSV logic.

#### [NEW] [LandingScreen.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/ui/screens/LandingScreen.kt)
- Hero entrance animation, "Get Started" button.

#### [NEW] [TrackerScreen.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/ui/screens/TrackerScreen.kt)
- Summary dashboard, input form, transaction list, and delete dialog.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/MainActivity.kt)
- Setup Navigation (NavHost) and Edge-to-Edge.

### Utilities
#### [NEW] [CsvUtils.kt](file:///C:/Users/AKASH/OneDrive/Desktop/Projects/TrackMySpend2/app/src/main/java/com/ruchitgoud/trackmyspend/util/CsvUtils.kt)
- Logic for parsing and generating CSV files.

## Verification Plan

### Automated Tests
- Build the project to verify dependencies.
- Unit tests for `CsvUtils` parsing logic (optional but recommended).

### Manual Verification
- Deploy to emulator/device.
- Verify "Landing Screen" animations.
- Add/Delete transactions and check summary updates.
- Test CSV Export and Import functionality.
- Check Neo-Brutalist styling (borders, shadows, colors) against `index.html`.
