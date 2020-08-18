package org.light_novel.lkadmin.ui.reply

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.Reply
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReplyAdapter(val context: Context, private val replies: List<Reply>) :
    RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val pidText: TextView = view.findViewById(R.id.pidText)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val tidText: TextView = view.findViewById(R.id.tidText)
        val authorText: TextView = view.findViewById(R.id.authorText)
        val ridText: TextView = view.findViewById(R.id.ridText)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val contentText: TextView = view.findViewById(R.id.contentText)
        val hideButton: Button = view.findViewById(R.id.hideButton)
        val scoreButton: Button = view.findViewById(R.id.scoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reply_item, parent, false)
        val holder = ViewHolder(view)
        holder.authorText.setOnClickListener {
            val position = holder.adapterPosition
            val reply = replies[position]
            val nickname = reply.author.nickname
            (parent.context as ReplyActivity).findReplyAuthor(nickname)
        }
        holder.hideButton.setOnClickListener {
            val position = holder.adapterPosition
            val reply = replies[position]
            if (reply.status == 0) {
                (parent.context as ReplyActivity).replyHide(1, reply.tid, reply.rid, reply.content)
            } else {
                (parent.context as ReplyActivity).replyHide(0, reply.tid, reply.rid, reply.content)
            }
        }
        holder.scoreButton.setOnClickListener {
            val position = holder.adapterPosition
            val reply = replies[position]
            val nickname = reply.author.nickname
            (parent.context as ReplyActivity).findOneUser(nickname)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reply = replies[position]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        // comment.article.aid
        // val showPid = "No.${reply.pid}"
        // holder.pidText.text = showPid
        holder.titleText.text = reply.article.title
        val showTid = "No.${reply.tid}"
        holder.tidText.text = showTid
        val showRid = "#${reply.rid}L"
        holder.ridText.text = showRid
        // val showName = "${reply.author.nickname} [No.${reply.author.uid}]"
        holder.authorText.text = reply.author.nickname
        holder.timeText.text = LocalDateTime.parse(reply.time, DateTimeFormatter.ISO_DATE_TIME).format(formatter)
        holder.statusText.text = if (reply.status == 0) {
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.statusNormal))
            holder.statusText.background = ContextCompat.getDrawable(context, R.drawable.status_normal)
            "正常"
        } else {
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.statusHide))
            holder.statusText.background = ContextCompat.getDrawable(context, R.drawable.status_hide)
            "隐藏"
        }
        holder.contentText.text = reply.content

        holder.hideButton.text = if (reply.status == 0) "隐藏" else "显示"
    }

    override fun getItemCount() = replies.size
}