# METRO ASSIST
### Delhi Metro Divyangjan Assistance & Station Coordination System

[![Android](https://img.shields.io/badge/Platform-Android%207.0%2B-brightgreen.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-purple.svg)](https://developer.android.com/jetpack/compose)
[![Pink Line](https://img.shields.io/badge/Delhi%20Metro-Pink%20Line%20(38%20Stations)-e91e63.svg)](https://www.delhimetrorail.com)

---

## 🚇 Overview

**METRO ASSIST** is a specialized Android application designed for inter-station communication and passenger coordination across the **Delhi Metro Pink Line (Line 7)**. Built as a controlled prototype, the system coordinates accessibility assistance for passengers who are **Visually Impaired** or require **Wheelchair Assistance**, from passenger boarding to destination arrival.

---

## 📱 Pre-Built APK Download

The compiled Android application package is ready for direct installation:
- **Download APK:** [`releases/MetroAssist-PinkLine-v1.0.apk`](releases/MetroAssist-PinkLine-v1.0.apk)
- **Application ID:** `com.aistudio.metroassist.pkstn`
- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 15 (API 36)

---

## ✨ Key Features

1. **Station-Specific Console Login**
   - Access consoles for all **38 Pink Line Stations** (Majlis Park ↔ Shiv Vihar).
   - Rapid test credentials: Password `12345`.
   - Dedicated Central Administrator Portal for line controllers.

2. **Divyangjan Assistance Lifecycle**
   - **Visually Impaired** and **Wheelchair** request creation.
   - Auto-populated boarding station with searchable destination station dropdown.
   - Train Number, Train Set Number, and Circular Direction (`+ Circular` or `− Circular`).
   - Generates structured Request IDs (`REQ-XXXXXX`).

3. **Destination Station Acknowledgement**
   - Destination station receives immediate audible alert and high-priority modal.
   - Single-click **ACKNOWLEDGE** and **SILENCE ALARM** controls with duplicate suppression.

4. **Live Train Movement & Route Tracking**
   - After departure, station staff updates train position along the route.
   - Validates forward progression, prevents backward movement, and records movement audit logs.

5. **Critical 3-Station Second Alarm**
   - Automatically triggers when the train reaches **exactly 3 stations before destination** (e.g., at *Sarai Kale Khan - Nizamuddin* for trains destined to *Lajpat Nagar*).
   - Displays flashing warning:
     > **“TRAIN APPROACHING – PLEASE PREPARE FOR DIVYANGJAN ASSISTANCE.”**
   - Calculates remaining travel time at **3 minutes per station** (ETA: ~9 minutes).
   - Generates dual-tone siren audio via `AudioTrack` PCM streaming plus hardware vibration.

6. **Assistance Provided & Completion**
   - Destination station staff marks **ASSISTANCE PROVIDED**, logging staff ID and completion timestamp.

7. **Other Information Module (Broadcast Notice Board)**
   - Categories: `LOST & FOUND`, `SEPARATE PASSENGER`, and `OTHER`.
   - Broadcasts network-wide alerts to all 38 active stations.

8. **Light & Dark Theme Switcher**
   - In-app toggle between high-contrast Delhi Metro Light Theme and Dark Slate Theme.

---

## 📋 Delhi Metro Pink Line Master Stations (38 Stations)

| # | Station Name | Code | + Circular Seq | − Circular Seq |
|---|--------------|------|----------------|----------------|
| 1 | Majlis Park | `MJPR` | 1 | 38 |
| 2 | Azadpur | `AZDP` | 2 | 37 |
| 3 | Shalimar Bagh | `SLMB` | 3 | 36 |
| 4 | Netaji Subhash Place | `NSP` | 4 | 35 |
| 5 | Shakurpur | `SKPR` | 5 | 34 |
| 6 | Punjabi Bagh West | `PBGW` | 6 | 33 |
| 7 | ESI - Basaidarapur | `ESIB` | 7 | 32 |
| 8 | Rajouri Garden | `RJGD` | 8 | 31 |
| 9 | Maya Puri | `MYPR` | 9 | 30 |
| 10 | Naraina Vihar | `NRVH` | 10 | 29 |
| 11 | Delhi Cantt | `DLCT` | 11 | 28 |
| 12 | Durgabai Deshmukh South Campus | `DSKC` | 12 | 27 |
| 13 | Sir M. Vishweshwaraiah Moti Bagh | `SMVM` | 13 | 26 |
| 14 | Bhikaji Cama Place | `BCMP` | 14 | 25 |
| 15 | Sarojini Nagar | `SJNR` | 15 | 24 |
| 16 | Dilli Haat - INA | `DHIN` | 16 | 23 |
| 17 | South Extension | `SOEX` | 17 | 22 |
| 18 | Lajpat Nagar | `LJPN` | 18 | 21 |
| 19 | Vinobapuri | `VNBP` | 19 | 20 |
| 20 | Ashram | `ASRM` | 20 | 19 |
| 21 | Sarai Kale Khan - Nizamuddin | `SKKN` | 21 | 18 |
| 22 | Mayur Vihar - I | `MVH1` | 22 | 17 |
| 23 | Mayur Vihar Pocket 1 | `MVP1` | 23 | 16 |
| 24 | Trilokpuri Sanjay Lake | `TPSL` | 24 | 15 |
| 25 | East Vinod Nagar - Mayur Vihar-II | `EVNM` | 25 | 14 |
| 26 | Mandawali - West Vinod Nagar | `MWVN` | 26 | 13 |
| 27 | I.P. Extension | `IPEX` | 27 | 12 |
| 28 | Anand Vihar ISBT | `AVIS` | 28 | 11 |
| 29 | Karkarduma | `KKRD` | 29 | 10 |
| 30 | Karkarduma Court | `KKRC` | 30 | 9 |
| 31 | Krishna Nagar | `KRNG` | 31 | 8 |
| 32 | East Azad Nagar | `EAZN` | 32 | 7 |
| 33 | Welcome | `WLCM` | 33 | 6 |
| 34 | Jafrabad | `JFBD` | 34 | 5 |
| 35 | Maujpur - Babarpur | `MJPR2` | 35 | 4 |
| 36 | Gokulpuri | `GKLP` | 36 | 3 |
| 37 | Johri Enclave | `JHEN` | 37 | 2 |
| 38 | Shiv Vihar | `SHVR` | 38 | 1 |

---

## 🚀 How to Publish to GitHub from Google AI Studio

### Method 1: Using AI Studio GitHub Export (Recommended)
1. In the Google AI Studio project interface, look at the top-right toolbar.
2. Click the **GitHub icon / "Push to GitHub"** button (or open the **Project Settings** menu).
3. Connect your GitHub account and authorize access if prompted.
4. Choose **Create a new repository** (e.g., `metro-assist-pinkline`) or select an existing repository.
5. Click **Commit & Push**. AI Studio will automatically push all project files, the Gradle build configuration, and the pre-built APK in `releases/`.

### Method 2: Export as ZIP & Push via Git CLI
If you prefer pushing via your local command line:
1. In Google AI Studio, click **Settings > Export Project as ZIP**.
2. Extract the ZIP file on your computer.
3. Open your terminal in the extracted folder and run:
   ```bash
   git init
   git add .
   git commit -m "Initial commit: Metro Assist Pink Line Android app and APK"
   git branch -M main
   git remote add origin https://github.com/<YOUR_USERNAME>/metro-assist-pinkline.git
   git push -u origin main
   ```

### Method 3: Publishing a GitHub Release with the APK
1. Once pushed to GitHub, go to your repository on `github.com`.
2. On the right-hand sidebar, click **Releases > Draft a new release**.
3. Choose a tag version (e.g. `v1.0.0`) and title (e.g. `Metro Assist v1.0.0`).
4. Drag and drop `releases/MetroAssist-PinkLine-v1.0.apk` into the release binaries box.
5. Click **Publish release**. Visitors can now download the `.apk` directly from your GitHub Releases page!

---

## 🛠️ Tech Stack & Architecture

- **Architecture:** MVVM (Model - View - ViewModel) + Clean Architecture
- **Language:** Kotlin 2.0
- **UI Framework:** Jetpack Compose (Material 3)
- **Local Persistence:** Android Room (SQLite) with KSP compiler
- **Asynchronous Flow:** Kotlin Coroutines & `StateFlow`
- **Sound Engine:** Low-latency synthesized dual-tone siren via Android `AudioTrack` & `Vibrator`
- **Testing:** JUnit 4 for route calculations and station distance verification

---

## ⚠️ Disclaimer

*This application is an independent prototype developed for research and demonstration of Divyangjan assistance workflows on the Delhi Metro Pink Line. It does not connect to internal DMRC operational signaling or dispatch systems.*
