package org.light_novel.lkadmin.ui.score

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.Score
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScoreAdapter(val context: Context, private val scores: List<Score>) :
    RecyclerView.Adapter<ScoreAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sidText: TextView = view.findViewById(R.id.sidText)
        val seriesText: TextView = view.findViewById(R.id.seriesText)
        val rateText: TextView = view.findViewById(R.id.rateText)
        val authorText: TextView = view.findViewById(R.id.authorText)
        val userRateText: TextView = view.findViewById(R.id.userRateText)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val userSidText: TextView = view.findViewById(R.id.userSidText)
        val contentText: TextView = view.findViewById(R.id.contentText)
        val hideButton: Button = view.findViewById(R.id.hideButton)
        val seriesScoreButton: Button = view.findViewById(R.id.seriesScoreButton)
        val userScoreButton: Button = view.findViewById(R.id.userScoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.score_item, parent, false)
        val holder = ViewHolder(view)
        holder.hideButton.setOnClickListener {
            val position = holder.adapterPosition
            val score = scores[position]
            if (score.status == 0) {
                (parent.context as ScoreActivity).scoreHide(score.sid, score.uid, 1)
            } else {
                (parent.context as ScoreActivity).scoreHide(score.sid, score.uid, 0)
            }
        }
        holder.seriesScoreButton.setOnClickListener {
            val position = holder.adapterPosition
            val score = scores[position]
            (parent.context as ScoreActivity).search(1, score.seriesInfo.sid.toString(), 2)
        }
        holder.userScoreButton.setOnClickListener {
            val position = holder.adapterPosition
            val score = scores[position]
            (parent.context as ScoreActivity).search(1, score.uid.toString(), 1)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val score = scores[position]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val showSid = "#${score.seriesInfo.sid}"
        holder.sidText.text = showSid
        holder.seriesText.text = score.seriesInfo.name
        holder.rateText.setTextColor(ContextCompat.getColor(context, R.color.statusNormal))
        holder.rateText.background = ContextCompat.getDrawable(context, R.drawable.status_normal)
        holder.rateText.text = score.seriesInfo.rate.toString()
        val author = "${score.author.nickname} [No.${score.author.uid}]"
        holder.authorText.text = author
        holder.userRateText.text = score.rate.toString()
        holder.statusText.text = if (score.status == 0) {
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.statusNormal))
            holder.statusText.background = ContextCompat.getDrawable(context, R.drawable.status_normal)
            "正常"
        } else {
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.statusHide))
            holder.statusText.background = ContextCompat.getDrawable(context, R.drawable.status_hide)
            "隐藏"
        }
        holder.timeText.text = LocalDateTime.parse(score.time, DateTimeFormatter.ISO_DATE_TIME).format(formatter)
        val showUserSid = "No.${score.sid}"
        holder.userSidText.text = showUserSid
        holder.contentText.text = score.text
        holder.hideButton.text = if (score.status == 0) "隐藏" else "显示"
    }

    override fun getItemCount() = scores.size
}