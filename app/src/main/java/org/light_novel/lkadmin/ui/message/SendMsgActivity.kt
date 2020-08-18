package org.light_novel.lkadmin.ui.message

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_send_msg.*
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.cutManage
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.logic.model.getPermissionName
import org.light_novel.lkadmin.extension.startActivity
import org.light_novel.lkadmin.logic.model.SendMessage

class SendMsgActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SendMsgActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(SendMsgViewModel::class.java) }

    private lateinit var adapter: SendReceiverAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_send_msg)

        val showName = intent.getStringExtra("show_name")?.cutManage()

        // toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.title = showName
            it.setDisplayHomeAsUpEnabled(true)
        }

        // RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = SendReceiverAdapter(this, viewModel.uidList)
        recyclerView.adapter = adapter

        // addReceiverButton
        addReceiverButton.setOnClickListener {
            viewModel.uidList.add(null)
            adapter.notifyDataSetChanged()
        }

        // sendButton
        sendButton.setOnClickListener {
            val title = titleText.editableText.toString()
            val msg = msgText.editableText.toString()
            val receivers = viewModel.uidList.filterNotNull().distinct().toList()
            if (title.isEmpty()) {
                "消息标题不得为空".showToast()
                return@setOnClickListener
            }
            if (msg.isEmpty()) {
                "消息内容不得为空".showToast()
                return@setOnClickListener
            }
            if (receivers.isEmpty()) {
                "接收者为空".showToast()
                return@setOnClickListener
            }
            viewModel.send(SendMessage(title, msg, receivers))
        }
        viewModel.sendResult.observe(this, Observer { result ->
            // 目前无法判断请求是否成功
            startActivity<MessageActivity>(this) {
                putExtra("id", 15)
                putExtra("show_name", getPermissionName(15))
            }
            finish()
        })

        if (viewModel.uidList.isEmpty()) {
            addReceiverButton.performClick()
        }
    }

}