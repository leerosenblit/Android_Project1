package com.example.ex1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScoreAdapter(
    private val scores: List<Score>,
    private val scoreCallback: ScoreCallback
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    interface ScoreCallback {
        fun onScoreClicked(score: Score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun getItemCount(): Int = scores.size

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.lblPlace.text = "#${position + 1}"
        holder.lblScore.text = "Score: ${score.score}"
    }

    inner class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblPlace: TextView = itemView.findViewById(R.id.score_LBL_place)
        val lblScore: TextView = itemView.findViewById(R.id.score_LBL_score)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    scoreCallback.onScoreClicked(scores[adapterPosition])
                }
            }
        }
    }
}