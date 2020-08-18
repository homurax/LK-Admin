package org.light_novel.lkadmin.ui.series

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.SeriesDetailArticle
import org.light_novel.lkadmin.logic.model.SeriesDetailUser

class SeriesDetailAdapter(
    val context: Context,
    private val users: List<SeriesDetailUser>,
    private val articles: List<SeriesDetailArticle>
) : RecyclerView.Adapter<SeriesDetailAdapter.SimpleViewHolder>() {

    inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        when (viewType) {
            USER_LIST -> return createUserHolder(parent)
            ARTICLE_LIST -> return createArticleHolder(parent)
        }
        throw IllegalArgumentException()
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
    }

    override fun getItemCount() = 2

    override fun getItemViewType(position: Int) = when (position) {
        0 -> USER_LIST
        1 -> ARTICLE_LIST
        else -> super.getItemViewType(position)
    }

    private fun createUserHolder(parent: ViewGroup): SimpleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.series_user, parent, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SeriesUserAdapter(context, users)
        return SimpleViewHolder(view)
    }

    private fun createArticleHolder(parent: ViewGroup): SimpleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.series_article, parent, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SeriesArticleAdapter(context, articles)
        return SimpleViewHolder(view)
    }

    companion object {

        private const val USER_LIST = 0

        private const val ARTICLE_LIST = 1
    }

}