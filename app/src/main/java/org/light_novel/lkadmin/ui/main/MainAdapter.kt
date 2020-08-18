package org.light_novel.lkadmin.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.extension.startActivity
import org.light_novel.lkadmin.logic.model.LKPermissionItem
import org.light_novel.lkadmin.ui.article.ArticleActivity
import org.light_novel.lkadmin.ui.comment.CommentActivity
import org.light_novel.lkadmin.ui.message.MessageActivity
import org.light_novel.lkadmin.ui.message.SendMsgActivity
import org.light_novel.lkadmin.ui.recommendation.AppRecommendActivity
import org.light_novel.lkadmin.ui.recommendation.WebRecommendActivity
import org.light_novel.lkadmin.ui.reply.ReplyActivity
import org.light_novel.lkadmin.ui.score.ScoreActivity
import org.light_novel.lkadmin.ui.series.SeriesActivity
import org.light_novel.lkadmin.ui.user.UserActivity

class MainAdapter(val context: Context, private val permissionItems: List<LKPermissionItem>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val permissionText: TextView = view.findViewById(R.id.permissionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val permissionItem = permissionItems[position]
            when (permissionItem.id) {
                8 -> startActivity<AppRecommendActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                9 -> startActivity<WebRecommendActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                10 -> startActivity<UserActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                12 -> startActivity<ArticleActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                13 -> startActivity<CommentActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                14 -> startActivity<ReplyActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                15 -> startActivity<MessageActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                16 -> startActivity<SendMsgActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                17 -> startActivity<SeriesActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                18 -> startActivity<ScoreActivity>(context) {
                    putExtra("id", permissionItem.id)
                    putExtra("route_name", permissionItem.routeName)
                    putExtra("show_name", permissionItem.showName)
                }
                else -> "暂未启用".showToast()
            }

        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val permissionItem = permissionItems[position]
        holder.permissionText.text = permissionItem.showName
    }

    override fun getItemCount() = permissionItems.size

}