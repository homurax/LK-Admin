package org.light_novel.lkadmin.ui.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.Comment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommentAdapter(val context: Context, private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val pidText: TextView = view.findViewById(R.id.pidText)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val tidText: TextView = view.findViewById(R.id.tidText)
        val authorText: TextView = view.findViewById(R.id.authorText)
        val authorUidText: TextView = view.findViewById(R.id.authorUidText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val contentText: TextView = view.findViewById(R.id.contentText)
        val hideButton: Button = view.findViewById(R.id.hideButton)
        val linkReplyButton: Button = view.findViewById(R.id.linkReplyButton)
        val scoreButton: Button = view.findViewById(R.id.scoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        val holder = ViewHolder(view)
        holder.authorText.setOnClickListener {
            val position = holder.adapterPosition
            val comment = comments[position]
            (parent.context as CommentActivity).findCommentAuthor(comment.uid)
        }
        holder.hideButton.setOnClickListener {
            val position = holder.adapterPosition
            val comment = comments[position]
            if (comment.status == 0) {
                (parent.context as CommentActivity).commentHide(comment.tid, 1, comment.content)
            } else {
                (parent.context as CommentActivity).commentHide(comment.tid, 0, comment.content)
            }
        }
        holder.linkReplyButton.setOnClickListener {
            val position = holder.adapterPosition
            val comment = comments[position]
            (parent.context as CommentActivity).linkReply(comment.tid)
        }
        holder.scoreButton.setOnClickListener {
            val position = holder.adapterPosition
            val comment = comments[position]
            (parent.context as CommentActivity).findOneUser(comment.uid)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // comment.article.aid
        // val showPid = "No.${comment.pid}"
        // holder.pidText.text = showPid
        holder.titleText.text = comment.article.title
        val showTid = "No.${comment.tid}"
        holder.tidText.text = showTid
        holder.authorText.text = comment.author.nickname
        val showName = "[No.${comment.uid}]"
        holder.authorUidText.text = showName
        holder.timeText.text = LocalDateTime.parse(comment.time, DateTimeFormatter.ISO_DATE_TIME).format(formatter)
        holder.contentText.text = comment.content
        holder.statusText.text = if (comment.status == 0) {
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.statusNormal))
            holder.statusText.background = ContextCompat.getDrawable(context, R.drawable.status_normal)
            "正常"
        } else {
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.statusHide))
            holder.statusText.background = ContextCompat.getDrawable(context, R.drawable.status_hide)
            "隐藏"
        }
        holder.hideButton.text = if (comment.status == 0) "隐藏" else "显示"
    }

    override fun getItemCount() = comments.size
}