package com.example.ex1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment() {

    private lateinit var list_RCV_scores: RecyclerView

    var scoreCallback: ScoreAdapter.ScoreCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        findViews(view)
        initViews()

        return view
    }

    private fun findViews(view: View) {
        list_RCV_scores = view.findViewById(R.id.list_RCV_scores)
    }

    private fun initViews() {
        val scoreManager = ScoreManager(requireContext())
        val scores = scoreManager.getAllScores()

        val adapter = ScoreAdapter(scores, object : ScoreAdapter.ScoreCallback {
            override fun onScoreClicked(score: Score) {
                scoreCallback?.onScoreClicked(score)
            }
        })

        list_RCV_scores.layoutManager = LinearLayoutManager(requireContext())
        list_RCV_scores.adapter = adapter
    }
}