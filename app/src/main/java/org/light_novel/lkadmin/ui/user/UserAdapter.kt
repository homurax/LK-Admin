package org.light_novel.lkadmin.ui.user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.logic.model.LKUser
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserAdapter(val context: Context, private val users: List<LKUser>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImage: ImageView = view.findViewById(R.id.avatarImage)
        val nameText: TextView = view.findViewById(R.id.nameText)
        val uidText: TextView = view.findViewById(R.id.uidText)
        val signText: TextView = view.findViewById(R.id.signText)
        val passerText: TextView = view.findViewById(R.id.passerText)
        val coinText: TextView = view.findViewById(R.id.coinText)
        val ipText: TextView = view.findViewById(R.id.ipText)
        val createDateText: TextView = view.findViewById(R.id.createDateText)
        val banButton: Button = view.findViewById(R.id.banButton)
        val hideButton: Button = view.findViewById(R.id.hideButton)
        val adventurerButton: Button = view.findViewById(R.id.adventurerButton)
        val coinButton: Button = view.findViewById(R.id.coinButton)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        val holder = ViewHolder(view)
        holder.banButton.setOnClickListener {
            val position = holder.adapterPosition
            val user = users[position]
            if (holder.banButton.text == "BAN") {
                AlertDialog.Builder(context).apply {
                    setTitle("选择封号时间")
                    setCancelable(false)
                    val banDayArr = arrayOf(1, 3, 7, 30, -1)
                    var banDayIndex = 0
                    setSingleChoiceItems(arrayOf("1天", "3天", "7天", "30天", "永久"), 0) { dialog, which ->
                        banDayIndex = which
                    }
                    setPositiveButton("确认") { dialog, which ->
                        (parent.context as UserActivity).ban(user, banDayArr[banDayIndex])
                    }
                    setNegativeButton("取消") { dialog, which ->
                    }
                    show()
                }
            } else {
                AlertDialog.Builder(context).apply {
                    setTitle("解封")
                    setMessage("确定要解封吗?")
                    setCancelable(false)
                    setPositiveButton("确认") { dialog, which ->
                        (parent.context as UserActivity).ban(user, 0)
                    }
                    setNegativeButton("取消") { dialog, which ->
                    }
                    show()
                }
            }
        }
        holder.hideButton.setOnClickListener {
            val position = holder.adapterPosition
            val user = users[position]
            AlertDialog.Builder(context).apply {
                setTitle("隐藏")
                setMessage("隐藏该用户全部帖子?")
                setCancelable(false)
                setPositiveButton("确认") { dialog, which ->
                    (parent.context as UserActivity).hideAll(user.uid)
                }
                setNegativeButton("取消") { dialog, which ->
                }
                show()
            }
        }
        holder.adventurerButton.setOnClickListener {
            val position = holder.adapterPosition
            val user = users[position]
            AlertDialog.Builder(context).apply {
                setTitle("身份修改")
                val message = if (user.passer == 0) "授予该用户勇者身份?" else "恢复用户平民身份?"
                setMessage(message)
                setCancelable(false)
                setPositiveButton("确认") { dialog, which ->
                    (parent.context as UserActivity).adventurerModify(user.uid, user.passer.xor(1))
                }
                setNegativeButton("取消") { dialog, which ->
                }
                show()
            }
        }
        holder.coinButton.setOnClickListener {
            val position = holder.adapterPosition
            val user = users[position]

            val contentView = LayoutInflater.from(context).inflate(R.layout.user_coin_modify, parent, false)
            val coinInfoText: TextView = contentView.findViewById(R.id.coinInfoText)
            val userCoinInfo = "当前用户轻币数量: ${user.coin}"
            coinInfoText.text = userCoinInfo
            val coinModifyText: EditText = contentView.findViewById(R.id.coinModifyText)
            val reasonText: EditText = contentView.findViewById(R.id.reasonText)

            AlertDialog.Builder(context).apply {
                setTitle("轻币修改")
                setCancelable(false)
                setView(contentView)
                setPositiveButton("确认", null)
                setNegativeButton("取消") { dialog, which ->
                }
                val dialog = create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener label@{
                    val coinModify = coinModifyText.editableText.toString()
                    val reason = reasonText.editableText.toString()
                    if (coinModify.isEmpty()) {
                        "修改数量不得为空".showToast()
                        return@label
                    }
                    val coin = coinModify.toInt()
                    if (coin == 0) {
                        "修改数量不可为零".showToast()
                        return@label
                    }
                    if (coin < 0 && (user.coin + coin < 0)) {
                        "不得扣为负数".showToast()
                        return@label
                    }
                    if (reason.trim().isEmpty()) {
                        "修改原因不得为空".showToast()
                        return@label
                    }
                    (parent.context as UserActivity).coinModify(user, coin, reason)
                    dialog.dismiss()
                }
                // show()
            }
        }
        return holder
    }


    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = users[position]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        Glide.with(context).load(user.avatar).error(R.drawable.akkariin).into(holder.avatarImage)
        val showUid = "No.${user.uid}"
        holder.uidText.text = showUid
        holder.nameText.text = user.nickname
        holder.signText.text = user.sign
        holder.passerText.text = if (user.passer == 1) {
            holder.passerText.setTextColor(ContextCompat.getColor(context, R.color.passer1))
            holder.passerText.background = ContextCompat.getDrawable(context, R.drawable.text_passer1)
            "勇者"
        } else {
            holder.passerText.setTextColor(ContextCompat.getColor(context, R.color.passer0))
            holder.passerText.background = ContextCompat.getDrawable(context, R.drawable.text_passer0)
            "普通会员"
        }
        // holder.passerText.setBackgroundColor(android.graphics.Color.GRAY)
        holder.coinText.text = user.coin.toString()
        holder.ipText.text = user.ip
        holder.createDateText.text = LocalDateTime.parse(user.createDate, DateTimeFormatter.ISO_DATE_TIME).format(formatter)
        holder.banButton.text = if (user.banEndDate == "false") "BAN" else "UNBAN"
        holder.adventurerButton.text = if (user.passer == 0) "授予勇者" else "恢复平民"
    }

    override fun getItemCount() = users.size

}