package org.light_novel.lkadmin.ui.series

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.logic.model.HideSeries
import org.light_novel.lkadmin.logic.model.Series
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SeriesAdapter(val context: Context, private val seriesList: List<Series>) :
    RecyclerView.Adapter<SeriesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupText: TextView = view.findViewById(R.id.groupText)
        val sidText: TextView = view.findViewById(R.id.sidText)
        val nameText: TextView = view.findViewById(R.id.nameText)
        val authorText: TextView = view.findViewById(R.id.authorText)
        val rateText: TextView = view.findViewById(R.id.rateText)
        val rateCountText: TextView = view.findViewById(R.id.rateCountText)
        val lastTimeText: TextView = view.findViewById(R.id.lastTimeText)
        val introText: TextView = view.findViewById(R.id.introText)
        val detailButton: Button = view.findViewById(R.id.detailButton)
        val unfoldButton: Button = view.findViewById(R.id.unfoldButton)
        val hideButton: Button = view.findViewById(R.id.hideButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.series_item, parent, false)
        val holder = ViewHolder(view)
        holder.rateCountText.setOnClickListener {
            val position = holder.adapterPosition
            val series = seriesList[position]
            if (series.rates == 0) {
                "暂无评分".showToast()
                return@setOnClickListener
            }
            (parent.context as SeriesActivity).linkScore(series.sid)
        }
        holder.detailButton.setOnClickListener {
            val position = holder.adapterPosition
            val series = seriesList[position]
            (parent.context as SeriesActivity).seriesDetail(series.sid)
        }
        holder.unfoldButton.setOnClickListener {
            val position = holder.adapterPosition
            val series = seriesList[position]
            (parent.context as SeriesActivity).unfold(series.sid)
        }
        holder.hideButton.setOnClickListener {
            val position = holder.adapterPosition
            val series = seriesList[position]
            if (series.status == 0) {
                (parent.context as SeriesActivity).seriesHide(HideSeries(1, series.name, 1), series.sid)
            } else {
                (parent.context as SeriesActivity).seriesHide(HideSeries(0), series.sid)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val series = seriesList[position]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val showGroup = "[${series.group.name}]"
        holder.groupText.text = showGroup
        val showSid = "#${series.sid}"
        holder.sidText.text = showSid
        holder.nameText.text = series.name
        holder.authorText.text = series.author
        holder.rateText.text = series.rate.toString()
        val rateCount = "${series.rates}人已评价"
        holder.rateCountText.text = rateCount
        holder.lastTimeText.text = LocalDateTime.parse(series.lastTime, DateTimeFormatter.ISO_DATE_TIME).format(formatter)
        holder.introText.text = series.intro
        holder.hideButton.text = if (series.status == 0) "隐藏" else "显示"
    }

    override fun getItemCount() = seriesList.size

}