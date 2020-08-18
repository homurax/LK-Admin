package org.light_novel.lkadmin.ui.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.Message
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageAdapter(val context: Context, private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val idText: TextView = view.findViewById(R.id.idText)
        val fromText: TextView = view.findViewById(R.id.fromText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val toText: TextView = view.findViewById(R.id.toText)
        val msgText: TextView = view.findViewById(R.id.msgText)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        val holder = ViewHolder(view)
        holder.deleteButton.setOnClickListener {
            val position = holder.adapterPosition
            val message = messages[position]
            (parent.context as MessageActivity).deleteMessage(message.id)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        holder.titleText.text = message.title
        val showId = "No.${message.id}"
        holder.idText.text = showId
        holder.fromText.text = if (message.from == 0) "System" else message.from.toString()
        val showUid = "No.${message.to}"
        holder.toText.text = showUid
        holder.timeText.text = LocalDateTime.parse(message.time, DateTimeFormatter.ISO_DATE_TIME).format(formatter)
        holder.msgText.text = message.msg
    }

    override fun getItemCount() = messages.size

}