package org.light_novel.lkadmin.ui.reply

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_reply.*
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.*
import org.light_novel.lkadmin.logic.model.*
import org.light_novel.lkadmin.ui.user.UserActivity
import kotlin.math.abs

class ReplyActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ReplyActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(ReplyViewModel::class.java) }

    private lateinit var adapter: ReplyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_reply)

        val showName = intent.getStringExtra("show_name")?.cutManage()
        val multipleQuery = intent.getParcelableExtra("multiple_query") ?: MultipleQuery("", "", 1, "")
        viewModel.qType = multipleQuery.qType
        viewModel.currPage = multipleQuery.page
        viewModel.query = multipleQuery.query

        // searchTypeSpinner
        ArrayAdapter.createFromResource(
            this,
            R.array.comment_query_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter.setDropDownViewResource(R.layout.spinner_item)
            searchTypeSpinner.adapter = adapter
        }

        // toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.title = showName
            it.setDisplayHomeAsUpEnabled(true)
        }

        // RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = ReplyAdapter(this, viewModel.replies)
        recyclerView.adapter = adapter

        // searchReplyEdit
        searchReplyEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                searchReplyEdit.hideKeyboard()
                supportActionBar?.let {
                    it.title = showName
                    searchLayout.visibility = View.GONE
                }
                val qType = if (searchTypeSpinner.selectedItemPosition == 0) "" else searchTypeSpinner.selectedItemPosition.toString()
                search(qType, 1, searchReplyEdit.editableText.toString())
                true
            } else {
                false
            }
        }

        // SwipeRefreshLayout
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            search()
        }
        viewModel.searchResult.observe(this, Observer { result ->
            invalidateOptionsMenu()
            val replyPage = result.getOrNull()
            if (replyPage != null) {
                refresh(replyPage)
                recyclerView.scrollToPosition(0)
            } else {
                "请求失败".showToast()
                Log.d(TAG, "ReplyPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        viewModel.refreshResult.observe(this, Observer { result ->
            val replyPage = result.getOrNull()
            if (replyPage != null) {
                refresh(replyPage)
            } else {
                "刷新失败".showToast()
                Log.d(TAG, "ReplyPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.hideResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            Log.d(TAG, msg?.msg ?: "")
            msg?.msg?.showToast(Toast.LENGTH_LONG)
            refresh()
        })
        viewModel.authorResult.observe(this, Observer { result ->
            val userPage = result.getOrNull()
            if (userPage != null) {
                startActivity<UserActivity>(this) {
                    putExtra("show_name", getPermissionName(10))
                    putExtra("user_page", userPage)
                }
            } else {
                "请求失败".showToast()
                Log.d(TAG, "UserPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.userResult.observe(this, Observer { result ->
            val user = result.getOrNull()
            if (user != null) {
                val contentView = LayoutInflater.from(this).inflate(R.layout.user_coin_modify, null)
                val coinInfoText: TextView = contentView.findViewById(R.id.coinInfoText)
                val userCoinInfo = "当前用户轻币数量: ${user.coin}"
                coinInfoText.text = userCoinInfo
                val coinModifyText: EditText = contentView.findViewById(R.id.coinModifyText)
                val reasonText: EditText = contentView.findViewById(R.id.reasonText)

                AlertDialog.Builder(this).apply {
                    setTitle("评分")
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
                        coinModify(user, coin, reason)
                        dialog.dismiss()
                    }
                }
            } else {
                "请求失败".showToast()
                Log.d(TAG, "User is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.coinModifyResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            Log.d(TAG, msg?.msg ?: "")
            msg?.msg?.showToast(Toast.LENGTH_LONG)
            refresh()
        })


        val replyPage = intent.getParcelableExtra("reply_page") as? ReplyPage
        if (replyPage != null) {
            viewModel.qType = "1"
            viewModel.query = replyPage.replies[0].tid.toString()

            viewModel.totalCount = replyPage.count
            viewModel.totalPage = (replyPage.count + viewModel.pageSize - 1) / viewModel.pageSize
            viewModel.replies.clear()
            viewModel.replies.addAll(replyPage.replies)
            adapter.notifyDataSetChanged()
        } else {
            search()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_reply, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when (searchLayout.visibility) {
            View.VISIBLE -> {
                menu?.forEach { it.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER) }
            }
            View.GONE -> {
                menu?.forEach { it.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM) }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.search -> {
                supportActionBar?.let {
                    it.title = null
                    invalidateOptionsMenu()
                    searchLayout.visibility = View.VISIBLE
                    searchReplyEdit.showKeyboard()
                }
            }
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
                                search(viewModel.qType, targetPage, viewModel.query)
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
                    search(viewModel.qType, viewModel.currPage - 1, viewModel.query)
                }
            }
            R.id.nextPage -> {
                if (viewModel.currPage == viewModel.totalPage) {
                    "当前处于最后一页".showToast()
                } else {
                    search(viewModel.qType, viewModel.currPage + 1, viewModel.query)
                }
            }
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (viewModel.currPage == 1) {
                    "当前处于第一页".showToast()
                } else {
                    search(viewModel.qType, viewModel.currPage - 1, viewModel.query)
                }
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (viewModel.currPage == viewModel.totalPage) {
                    "当前处于最后一页".showToast()
                } else {
                    search(viewModel.qType, viewModel.currPage + 1, viewModel.query)
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun refresh(replyPage: ReplyPage) {
        viewModel.totalCount = replyPage.count
        viewModel.totalPage = (replyPage.count + viewModel.pageSize - 1) / viewModel.pageSize
        viewModel.replies.clear()
        viewModel.replies.addAll(replyPage.replies)
        adapter.notifyDataSetChanged()
    }

    private fun search(targetQType: String = viewModel.qType, targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query) {
        viewModel.search(targetQType, targetPage, queryContent)
        swipeRefresh.isRefreshing = true
    }

    private fun refresh(targetQType: String = viewModel.qType, targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query) {
        viewModel.refresh(targetQType, targetPage, queryContent)
    }

    fun replyHide(status: Int, tid: Int, rid: Int, content: String) {
        viewModel.replyHide(status, tid, rid, content)
    }

    fun findReplyAuthor(nickname: String) {
        viewModel.findReplyAuthor(nickname)
    }

    fun findOneUser(nickname: String) {
        viewModel.findOneUser(nickname)
    }

    fun coinModify(user: LKUser, coin: Int, reason: String) {
        val sendMsg = if (coin > 0) 4 else 3
        viewModel.coinModify(
            ModifyCoin(
                user.uid,
                user.coin + coin,
                abs(coin),
                reason,
                sendMsg,
                user.nickname,
                user.avatar,
                user.passer,
                user.sign,
                user.gender,
                user.ip,
                user.createIp,
                user.createDate
            )
        )
    }

}