package org.light_novel.lkadmin.ui.message

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message.*
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.cutManage
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.logic.model.MessagePage

class MessageActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MessageActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(MessageViewModel::class.java) }

    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_message)

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
        adapter = MessageAdapter(this, viewModel.messages)
        recyclerView.adapter = adapter

        // SwipeRefreshLayout
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            search()
        }
        viewModel.searchResult.observe(this, Observer { result ->
            val messagePage = result.getOrNull()
            if (messagePage != null) {
                refresh(messagePage)
                recyclerView.scrollToPosition(0)
            } else {
                "请求失败".showToast()
                Log.d(TAG, "MessagePage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        viewModel.refreshResult.observe(this, Observer { result ->
            val messagePage = result.getOrNull()
            if (messagePage != null) {
                refresh(messagePage)
            } else {
                "刷新失败".showToast()
                Log.d(TAG, "MessagePage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.deleteResult.observe(this, Observer { result ->
            // val msgCode = result.getOrNull()
            // msgCode?.code?.toString()?.showToast()
            refresh()
        })

        search()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.jumpPage -> {
                AlertDialog.Builder(this).apply {
                    setTitle("跳至特定页数")
                    setMessage("当前搜索条件下共 ${viewModel.totalPage} 页")
                    setCancelable(false)
                    val editText = EditText(context).apply {
                        inputType = InputType.TYPE_CLASS_NUMBER
                        setText(viewModel.currPage.toString())
                        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                            leftMargin = 50
                            rightMargin = 500
                        }
                    }
                    val container = FrameLayout(context).apply { addView(editText) }
                    setView(container)
                    setPositiveButton("跳转") { dialog, which ->
                        val inputPage = editText.editableText.toString()
                        if (inputPage.isNotEmpty()) {
                            val targetPage = editText.editableText.toString().toInt()
                            if (targetPage in IntRange(1, viewModel.totalPage)) {
                                search(targetPage)
                            } else {
                                "无效页数".showToast(Toast.LENGTH_LONG)
                            }
                        }
                    }
                    setNegativeButton("取消") { dialog, which ->
                    }
                    show()
                }
            }
            R.id.previousPage -> {
                if (viewModel.currPage == 1) {
                    "当前处于第一页".showToast()
                } else {
                    search(viewModel.currPage - 1)
                }
            }
            R.id.nextPage -> {
                if (viewModel.currPage == viewModel.totalPage) {
                    "当前处于最后一页".showToast()
                } else {
                    search(viewModel.currPage + 1)
                }
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_message, menu)
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (viewModel.currPage == 1) {
                    "当前处于第一页".showToast()
                } else {
                    search(viewModel.currPage - 1)
                }
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (viewModel.currPage == viewModel.totalPage) {
                    "当前处于最后一页".showToast()
                } else {
                    search(viewModel.currPage + 1)
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun refresh(messagePage: MessagePage) {
        viewModel.totalCount = messagePage.count
        viewModel.totalPage = (messagePage.count + viewModel.pageSize - 1) / viewModel.pageSize
        viewModel.messages.clear()
        viewModel.messages.addAll(messagePage.rows)
        adapter.notifyDataSetChanged()
    }

    private fun search(targetPage: Int = viewModel.currPage) {
        viewModel.search(targetPage)
        swipeRefresh.isRefreshing = true
    }

    private fun refresh(targetPage: Int = viewModel.currPage) {
        viewModel.refresh(targetPage)
    }

    fun deleteMessage(id: Int) {
        viewModel.deleteMessage(id)
    }
}