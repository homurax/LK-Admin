package org.light_novel.lkadmin.ui.message

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R

class SendReceiverAdapter(val context: Context, private val uidList: ArrayList<Int?>) :
    RecyclerView.Adapter<SendReceiverAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val uidText: EditText = view.findViewById(R.id.uidText)
        val removeReceiverButton: Button = view.findViewById(R.id.removeReceiverButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.send_receiver_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (holder.uidText.tag is TextWatcher) {
            holder.uidText.removeTextChangedListener((holder.uidText.tag as TextWatcher))
        }
        val uid = uidList[position]
        holder.uidText.setText(uid?.toString())
        holder.removeReceiverButton.setOnClickListener {
            uidList.removeAt(position)
            notifyItemRemoved(position)
            if (position != uidList.size) {
                notifyItemRangeChanged(position, uidList.size - position)
            }
        }
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                uidList[position] = s?.toString()?.toInt()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        holder.uidText.addTextChangedListener(watcher)
        holder.uidText.tag = watcher
    }

    override fun getItemCount() = uidList.size

}