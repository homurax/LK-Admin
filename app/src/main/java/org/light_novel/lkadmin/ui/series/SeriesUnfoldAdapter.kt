package org.light_novel.lkadmin.ui.series

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.SeriesArticle

class SeriesUnfoldAdapter(val context: Context, private val articles: ArrayList<SeriesArticle>) :
    RecyclerView.Adapter<SeriesUnfoldAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serialText: TextView = view.findViewById(R.id.serialText)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val removeButton: Button = view.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.series_unfold_item, parent, false)
        val holder = ViewHolder(view)
        holder.removeButton.setOnClickListener {
            val position = holder.adapterPosition
            val article = articles[position]
            (parent.context as SeriesActivity).removeArticle(article)
            articles.removeAt(position)
            notifyItemRemoved(position)
            if (position != articles.size) {
                notifyItemRangeChanged(position, articles.size - position)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        val partIndex = "P${position + 1}"
        holder.serialText.text = partIndex
        holder.titleText.text = article.title
    }

    override fun getItemCount() = articles.size

}