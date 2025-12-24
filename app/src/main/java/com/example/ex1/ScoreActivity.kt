package com.example.ex1

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ScoreActivity : AppCompatActivity() {

    private lateinit var listFragment: ListFragment
    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        listFragment = ListFragment()
        mapFragment = MapFragment()

        listFragment.scoreCallback = object : ScoreAdapter.ScoreCallback {
            override fun onScoreClicked(score: Score) {
                mapFragment.zoom(score.lat, score.lon)
            }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.score_FRAME_list, listFragment)
            .add(R.id.score_FRAME_map, mapFragment)
            .commit()

        val btnBack = findViewById<Button>(R.id.score_BTN_back)
        btnBack.setOnClickListener {
            finish()
        }
    }
}