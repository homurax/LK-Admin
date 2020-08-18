package org.light_novel.lkadmin.ui.series

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.SeriesDetailArticle

class SeriesArticleAdapter(val context: Context, private val articles: List<SeriesDetailArticle>) :
    RecyclerView.Adapter<SeriesArticleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val aidText: TextView = view.findViewById(R.id.aidText)
        val titleText: TextView = view.findViewById(R.id.titleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.series_article_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.aidText.text = article.aid.toString()
        holder.titleText.text = article.title
    }

    override fun getItemCount() = articles.size

}