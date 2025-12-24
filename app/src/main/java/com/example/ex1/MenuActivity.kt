package com.example.ex1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnButtons = findViewById<Button>(R.id.menu_BTN_buttons)
        val btnSensors = findViewById<Button>(R.id.menu_BTN_sensors)
        val btnScores = findViewById<Button>(R.id.menu_BTN_scores)

        btnButtons.setOnClickListener {
            startGame(false)
        }

        btnSensors.setOnClickListener {
            startGame(true)
        }

        btnScores.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGame(useSensors: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("KEY_USE_SENSORS", useSensors)
        startActivity(intent)
    }
}