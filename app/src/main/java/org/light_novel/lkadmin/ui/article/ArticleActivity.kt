package org.light_novel.lkadmin.ui.article

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
import kotlinx.android.synthetic.main.activity_article.*
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.cutManage
import org.light_novel.lkadmin.extension.hideKeyboard
import org.light_novel.lkadmin.extension.showKeyboard
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.ui.comment.CommentActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.light_novel.lkadmin.extension.startActivity
import org.light_novel.lkadmin.logic.model.*
import org.light_novel.lkadmin.ui.user.UserActivity
import kotlin.math.abs

class ArticleActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ArticleActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(ArticleViewModel::class.java) }

    private lateinit var adapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_article)

        val showName = intent.getStringExtra("show_name")?.cutManage()

        // searchTypeSpinner
        ArrayAdapter.createFromResource(
            this,
            R.array.article_query_type_array,
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
        adapter = ArticleAdapter(this, viewModel.articles)
        recyclerView.adapter = adapter

        // searchArticleEdit
        searchArticleEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                searchArticleEdit.hideKeyboard()
                supportActionBar?.let {
                    it.title = showName
                    searchLayout.visibility = View.GONE
                }
                val searchType = if (searchTypeSpinner.selectedItemPosition == 0) null else searchTypeSpinner.selectedItemPosition
                search(1, searchArticleEdit.editableText.toString(), searchType)
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
            val articlePage = result.getOrNull()
            if (articlePage != null) {
                refresh(articlePage)
                recyclerView.scrollToPosition(0)
            } else {
                "请求失败".showToast()
                Log.d(TAG, "ArticlePage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        viewModel.refreshResult.observe(this, Observer { result ->
            val articlePage = result.getOrNull()
            if (articlePage != null) {
                refresh(articlePage)
            } else {
                "刷新失败".showToast()
                Log.d(TAG, "ArticlePage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.maskResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            Log.d(TAG, msg?.msg ?: "")
            msg?.msg?.showToast(Toast.LENGTH_LONG)
            refresh()
        })
        viewModel.topResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            Log.d(TAG, msg?.msg ?: "")
            msg?.msg?.showToast(Toast.LENGTH_LONG)
            refresh()
        })
        viewModel.linkCommentResult.observe(this, Observer { result ->
            val commentPage = result.getOrNull()
            if (commentPage != null) {
                if (commentPage.comments.isNotEmpty()) {
                    startActivity<CommentActivity>(this) {
                        putExtra("show_name", getPermissionName(13))
                        putExtra("comment_page", commentPage)
                        putExtra("q_type", "1")
                    }
                } else {
                    "尚无回帖".showToast()
                }
            } else {
                "请求失败".showToast()
                Log.d(TAG, "CommentPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
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

        val articlePage = intent.getParcelableExtra("article_page") as? ArticlePage
        if (articlePage != null) {
            viewModel.searchType = intent.getStringExtra("search_type")?.toInt()
            when (viewModel.searchType) {
                2 -> viewModel.query = articlePage.articles[0].uid.toString()
            }
            refresh(articlePage)
        } else {
            search()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_article, menu)
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
                    searchArticleEdit.showKeyboard()
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
                                search(targetPage, viewModel.query)
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
                    search(viewModel.currPage - 1, viewModel.query)
                }
            }
            R.id.nextPage -> {
                if (viewModel.currPage == viewModel.totalPage) {
                    "当前处于最后一页".showToast()
                } else {
                    search(viewModel.currPage + 1, viewModel.query)
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
                    search(viewModel.currPage - 1, viewModel.query)
                }
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (viewModel.currPage == viewModel.totalPage) {
                    "当前处于最后一页".showToast()
                } else {
                    search(viewModel.currPage + 1, viewModel.query)
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun refresh(articlePage: ArticlePage) {
        viewModel.totalCount = articlePage.count
        viewModel.totalPage = (articlePage.count + viewModel.pageSize - 1) / viewModel.pageSize
        viewModel.articles.clear()
        viewModel.articles.addAll(articlePage.articles)
        adapter.notifyDataSetChanged()
    }

    private fun search(targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query, targetSearchType: Int? = viewModel.searchType) {
        viewModel.search(targetPage, queryContent, targetSearchType)
        swipeRefresh.isRefreshing = true
    }

    private fun refresh(targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query, targetSearchType: Int? = viewModel.searchType) {
        viewModel.refresh(targetPage, queryContent, targetSearchType)
    }

    fun linkComment(aid: Int) {
        viewModel.linkComment(aid)
    }

    fun articleMask(aid: Int, mask: Int) {
        viewModel.articleMask(aid, mask)
    }

    fun articleTop(aid: Int, top: Boolean) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val lastTime = if (top) {
            LocalDateTime.now().plusYears(100).format(formatter)
        } else {
            LocalDateTime.now().format(formatter)
        }
        val sendMsg = if (top) "2" else null
        viewModel.articleTop(aid, lastTime, sendMsg)
    }

    fun findArticleAuthor(nickname: String) {
        viewModel.findArticleAuthor(nickname)
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