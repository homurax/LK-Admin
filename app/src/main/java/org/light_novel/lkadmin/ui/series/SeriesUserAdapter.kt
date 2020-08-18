package org.light_novel.lkadmin.ui.series

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.SeriesDetailUser

class SeriesUserAdapter(val context: Context, private val users: List<SeriesDetailUser>) :
    RecyclerView.Adapter<SeriesUserAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val uidText: TextView = view.findViewById(R.id.uidText)
        val nicknameText: TextView = view.findViewById(R.id.nicknameText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.series_user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.uidText.text = user.uid.toString()
        holder.nicknameText.text = user.nickname
    }

    override fun getItemCount() = users.size

}