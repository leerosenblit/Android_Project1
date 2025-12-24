package com.example.ex1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var main_IMG_car: ImageView
    private lateinit var main_BTN_left: ImageButton
    private lateinit var main_BTN_right: ImageButton
    private lateinit var main_IMG_heart1: ImageView
    private lateinit var main_IMG_heart2: ImageView
    private lateinit var main_IMG_heart3: ImageView
    private lateinit var main_BTN_start: Button
    private lateinit var main_LBL_score: TextView

    private lateinit var obstacleViews: Array<ImageView>

    // --- Grid Logic ---
    private var colWidth = 0
    private var rowHeight = 0
    private val ROWS = 4
    private val COLS = 5 // 5 נתיבים

    // --- Game State ---
    private var carCol = 2 // מתחילים באמצע
    private val carRow = 3

    private var lives = 3
    private var isGameActive = false
    private var ticks = 0

    private val rockRows = IntArray(6) { -1 }
    private val rockCols = IntArray(6) { -1 }

    // --- SPEED CONTROL ---
    private val DELAY: Long = 600
    private val handler = Handler(Looper.getMainLooper())

    private var sensorManager: android.hardware.SensorManager? = null
    private var sensor: android.hardware.Sensor? = null
    private var useSensors: Boolean = false

    private lateinit var coinViews: Array<ImageView>
    private val coinRows = IntArray(4) { -1 }
    private val coinCols = IntArray(4) { -1 }
    private var score = 0

    private lateinit var main_BTN_back: Button
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient

    private var mediaPlayer: android.media.MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)



        useSensors = intent.getBooleanExtra("KEY_USE_SENSORS", false)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insets

        }



        findViews()

        initViews()

        initSensors()



        val displayMetrics = resources.displayMetrics

        val screenWidth = displayMetrics.widthPixels

        val screenHeight = displayMetrics.heightPixels



        colWidth = screenWidth / COLS



        rowHeight = screenHeight / 5



        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)



        checkAndRequestPermissions()

    }
    private fun checkAndRequestPermissions() {
        if (androidx.core.app.ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            androidx.core.app.ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (isGameActive) startTimer()

        if (useSensors) {
            sensorManager?.registerListener(sensorEventListener, sensor, android.hardware.SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer()

        if (useSensors) {
            sensorManager?.unregisterListener(sensorEventListener)
        }
    }

    private fun findViews() {
        main_IMG_car = findViewById(R.id.main_IMG_car)
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_IMG_heart1 = findViewById(R.id.main_IMG_heart1)
        main_IMG_heart2 = findViewById(R.id.main_IMG_heart2)
        main_IMG_heart3 = findViewById(R.id.main_IMG_heart3)
        main_BTN_start = findViewById(R.id.main_BTN_start)
        main_LBL_score = findViewById(R.id.main_LBL_score)

        obstacleViews = arrayOf(
            findViewById(R.id.main_IMG_rock1),
            findViewById(R.id.main_IMG_rock2),
            findViewById(R.id.main_IMG_rock3),
            findViewById(R.id.main_IMG_rock4),
            findViewById(R.id.main_IMG_rock5),
            findViewById(R.id.main_IMG_rock6)
        )

        coinViews = arrayOf(
            findViewById(R.id.main_IMG_coin1),
            findViewById(R.id.main_IMG_coin2),
            findViewById(R.id.main_IMG_coin3),
            findViewById(R.id.main_IMG_coin4)
        )
        main_BTN_back = findViewById(R.id.main_BTN_back)
    }

    private fun initViews() {
        if (useSensors) {
            main_BTN_left.visibility = View.INVISIBLE
            main_BTN_right.visibility = View.INVISIBLE
        } else {
            main_BTN_left.visibility = View.VISIBLE
            main_BTN_right.visibility = View.VISIBLE
            main_BTN_left.setOnClickListener { if (isGameActive) moveCar(-1) }
            main_BTN_right.setOnClickListener { if (isGameActive) moveCar(1) }
        }

        main_BTN_start.setOnClickListener { startGame() }
        main_IMG_car.visibility = View.INVISIBLE

        main_BTN_back.setOnClickListener {
            finish()
        }
    }

    private fun setViewPosition(view: View, col: Int, row: Int) {
        val params = view.layoutParams as RelativeLayout.LayoutParams

        val cellCenterX = (col * colWidth) + (colWidth / 2)
        val cellCenterY = (row * rowHeight) + (rowHeight / 2)

        val left = cellCenterX - (view.width / 2)
        val top = cellCenterY - (view.height / 2)

        params.leftMargin = left
        params.topMargin = top
        view.layoutParams = params
    }

    private fun startGame() {
        isGameActive = true
        main_BTN_start.visibility = View.INVISIBLE
        main_LBL_score.text = "0"
        lives = 3
        ticks = 0
        updateHearts()

        score = 0

        for (i in coinViews.indices) {
            coinViews[i].visibility = View.INVISIBLE
            coinRows[i] = -1
        }

        carCol = 2
        main_IMG_car.visibility = View.VISIBLE
        main_IMG_car.post {
            setViewPosition(main_IMG_car, carCol, carRow)
        }

        for (i in obstacleViews.indices) {
            obstacleViews[i].visibility = View.INVISIBLE
            rockRows[i] = -1
        }

        startTimer()
    }


    private fun gameOver() {
        isGameActive = false
        stopTimer()
        saveScoreWithLocation()
        main_LBL_score.text = "GAME OVER"
        main_BTN_start.text = "RESTART"
        main_BTN_start.visibility = View.VISIBLE
        Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show()
    }

    private fun saveScoreWithLocation() {
        if (androidx.core.app.ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val lat = location?.latitude ?: 0.0
                val lon = location?.longitude ?: 0.0

                val scoreObject = Score(score, lat, lon)
                ScoreManager(this).saveScore(scoreObject)
            }
        } else {
            val scoreObject = Score(score, 0.0, 0.0)
            ScoreManager(this).saveScore(scoreObject)
        }
    }

    private fun moveCar(direction: Int) {
        carCol += direction
        if (carCol < 0) carCol = 0
        if (carCol > 4) carCol = 4

        setViewPosition(main_IMG_car, carCol, carRow)
        checkCollisions()
    }

    private val runnable = object : Runnable {
        override fun run() {
            updateGame()
            if (isGameActive) {
                handler.postDelayed(this, DELAY)
            }
        }
    }

    private fun startTimer() {
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(runnable)
    }

    private fun updateGame() {
        ticks++
        score++

        for (i in coinViews.indices) {
            if (coinRows[i] != -1) {
                coinRows[i]++
                if (coinRows[i] > 4) {
                    coinRows[i] = -1
                    coinViews[i].visibility = View.INVISIBLE
                } else {
                    setViewPosition(coinViews[i], coinCols[i], coinRows[i])
                }
            }
        }

        for (i in obstacleViews.indices) {
            if (rockRows[i] != -1) {
                rockRows[i]++
                if (rockRows[i] > 3) {
                    rockRows[i] = -1
                    obstacleViews[i].visibility = View.INVISIBLE
                } else {
                    setViewPosition(obstacleViews[i], rockCols[i], rockRows[i])
                }
            }
        }

        if (Random.nextInt(0, 10) > 2) {
            spawnRock()
        }
        val random = Random.nextInt(0, 10)
        if (random > 3) spawnRock()
        if (random < 2) spawnCoin()

        checkCollisions()
        updateScoreUI()
    }

    private fun spawnRock() {
        val index = rockRows.indexOfFirst { it == -1 }
        if (index != -1) {
            rockRows[index] = 0
            // תיקון: שינוי מ-3 ל-COLS (5 נתיבים)
            rockCols[index] = Random.nextInt(0, COLS)

            var isOccupied = false
            for (i in obstacleViews.indices) {
                if (i != index && rockRows[i] == 0 && rockCols[i] == rockCols[index]) {
                    isOccupied = true
                }
            }

            if (!isOccupied) {
                obstacleViews[index].visibility = View.VISIBLE
                setViewPosition(obstacleViews[index], rockCols[index], rockRows[index])
            } else {
                rockRows[index] = -1
            }
        }
    }

    private fun checkCollisions() {
        for (i in obstacleViews.indices) {
            if (rockRows[i] != -1) {
                if (rockRows[i] == carRow && rockCols[i] == carCol) {
                    handleCrash()
                    rockRows[i] = -1
                    obstacleViews[i].visibility = View.INVISIBLE
                }
            }
        }

        for (i in coinViews.indices) {
            if (coinRows[i] != -1) {
                if (coinRows[i] == carRow && coinCols[i] == carCol) {
                    score += 100
                    coinRows[i] = -1
                    coinViews[i].visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun handleCrash() {
        lives--
        toastAndVibrate()

        try {
            if (mediaPlayer == null) {
                mediaPlayer = android.media.MediaPlayer.create(this, R.raw.crash_sound)
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        updateHearts()
        if (lives <= 0) gameOver()
    }

    private fun toastAndVibrate() {
        try {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (v.hasVibrator()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(500)
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
        Toast.makeText(this, "CRASH!", Toast.LENGTH_SHORT).show()
    }

    private fun updateHearts() {
        main_IMG_heart1.visibility = if (lives >= 1) View.VISIBLE else View.INVISIBLE
        main_IMG_heart2.visibility = if (lives >= 2) View.VISIBLE else View.INVISIBLE
        main_IMG_heart3.visibility = if (lives >= 3) View.VISIBLE else View.INVISIBLE
    }

    private fun initSensors() {
        if (!useSensors) return

        sensorManager = getSystemService(android.content.Context.SENSOR_SERVICE) as android.hardware.SensorManager
        sensor = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
    }

    private val sensorEventListener = object : android.hardware.SensorEventListener {
        override fun onSensorChanged(event: android.hardware.SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]

            if (abs(x) > 3.0) {
                if (x > 0) {
                    moveCar(-1)
                } else {
                    moveCar(1)
                }
            }
        }

        override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {
        }
    }

    private fun spawnCoin() {
        val index = coinRows.indexOfFirst { it == -1 }
        if (index != -1) {
            coinRows[index] = 0
            coinCols[index] = Random.nextInt(0, COLS)

            coinViews[index].visibility = View.VISIBLE
            setViewPosition(coinViews[index], coinCols[index], coinRows[index])
        }
    }

    private fun updateScoreUI() {
        main_LBL_score.text = score.toString()
    }
}