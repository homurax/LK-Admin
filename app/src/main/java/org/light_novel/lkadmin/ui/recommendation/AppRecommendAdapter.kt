package org.light_novel.lkadmin.ui.recommendation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.Recommend

class AppRecommendAdapter(val context: Context, private val recommends: List<Recommend>) :
    RecyclerView.Adapter<AppRecommendAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupTitleText: TextView = view.findViewById(R.id.groupTitleText)
        val idText: TextView = view.findViewById(R.id.idText)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val actionParamsText: TextView = view.findViewById(R.id.actionParamsText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_recommend_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommend = recommends[position]

        holder.groupTitleText.text = recommend.group.title
        val showId = "No.${recommend.id}"
        holder.idText.text = showId
        holder.titleText.text = recommend.title
        holder.actionParamsText.text = recommend.actionParams
    }

    override fun getItemCount() = recommends.size
}