# 🏎️ Highway Racer – Android Game

A classic 2D lane racing game built with **Kotlin** for Android.  
The game features multiple control modes, dynamic obstacles, coin collection, and a location based high score system integrated with **Google Maps**.

---

## 📱 Features

### 🎮 Game Modes
- **Buttons Mode**
  - Control the car using on-screen Left and Right buttons.

- **Sensor Mode**
  - Control the car using the phone’s accelerometer:
    - Tilt left or right to steer.
    - Tilt forward or backward to control game speed.

---

### 🕹️ Gameplay
- **5-Lane System**  
  Navigate through five distinct lanes.

- **Dynamic Obstacles**  
  Rocks fall randomly from the top of the screen.

- **Collectibles**  
  Coins appear randomly and give a bonus of +100 points.

- **Crash Mechanics**
  - Heart-based life system  
  - Vibration feedback  
  - Crash sound effects  

- **Scoring System**
  - Score increases based on distance traveled  
  - Bonus points from collected coins  

---

## 🏆 High Scores & Location

- **Top 10 Leaderboard**  
  Stores and displays the best runs.

- **Persistent Storage**  
  High scores are saved locally using `SharedPreferences` and `Gson`.

- **GPS Integration**  
  The device location is captured when a high score is achieved.

- **Google Maps Integration**  
  Clicking on a score opens a map and zooms to the exact location where the score was recorded.

---

## 🛠️ Tech Stack & Concepts Used

### Language
- Kotlin

### Architecture
- MVC (Model View Controller)

### Activities & Fragments
- `MainActivity` – Game loop and main logic  
- `MenuActivity` – Game mode selection and navigation  
- `ScoreActivity` – Hosts score list and map fragments  
- `ListFragment` – RecyclerView for displaying scores  
- `MapFragment` – Google Maps integration  

### Sensors & Location
- `SensorManager` with `TYPE_ACCELEROMETER`
- `FusedLocationProviderClient` for GPS tracking

### Storage & Data Handling
- `SharedPreferences` for persistent data
- JSON serialization using **Gson**

### UI Components
- RecyclerView  
- CardView  
- FrameLayout  
- Custom XML layouts  

### Game Logic
- `Handler` and `Runnable` for the game loop and timing

### Permissions
- Runtime permissions for location access

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK API Level 31 or higher
- Google Maps API Key

---

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/lee-rosenblit/Android_Project1.git
   ```

2. **Open the project in Android Studio**

3. **Add your Google Maps API Key**
   - Open `AndroidManifest.xml`
   - Find the following meta-data tag:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_KEY_HERE" />
     ```
   - Replace `YOUR_KEY_HERE` with your actual API key.

4. **Build and Run**
   - Sync Gradle files
   - Run the app on an emulator or physical device


---

## 👨‍💻 Author

**Lee Rosenblit**  
Computer Science Student  

Built as part of an Android Development course assignment.
