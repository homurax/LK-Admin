package org.light_novel.lkadmin.ui.user

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_user.*
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.*
import org.light_novel.lkadmin.logic.model.LKUser
import org.light_novel.lkadmin.logic.model.ModifyCoin
import org.light_novel.lkadmin.logic.model.UserPage
import org.light_novel.lkadmin.logic.model.getPermissionName
import org.light_novel.lkadmin.ui.article.ArticleActivity
import org.light_novel.lkadmin.ui.comment.CommentActivity
import org.light_novel.lkadmin.ui.reply.ReplyActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class UserActivity : AppCompatActivity() {

    companion object {
        const val TAG = "UserActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }

    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_user)

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
        adapter = UserAdapter(this, viewModel.users)
        recyclerView.adapter = adapter

        // searchUserEdit
        searchUserEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                searchUserEdit.hideKeyboard()
                supportActionBar?.let {
                    it.title = showName
                    searchUserEdit.visibility = View.GONE
                }
                search(1, searchUserEdit.editableText.toString())
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
            val userPage = result.getOrNull()
            if (userPage != null) {
                refresh(userPage)
                recyclerView.scrollToPosition(0)
            } else {
                "请求失败".showToast()
                Log.d(TAG, "UserPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        viewModel.refreshResult.observe(this, Observer { result ->
            val userPage = result.getOrNull()
            if (userPage != null) {
                refresh(userPage)
            } else {
                "刷新失败".showToast()
                Log.d(TAG, "UserPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.hideAllResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            Log.d(TAG, msg?.msg ?: "")
            msg?.msg?.showToast(Toast.LENGTH_LONG)
            refresh()
        })
        viewModel.banResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            Log.d(TAG, msg?.msg ?: "")
            msg?.msg?.showToast(Toast.LENGTH_LONG)
            refresh()
        })
        viewModel.modifyAdventurerResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            Log.d(TAG, msg?.msg ?: "")
            msg?.msg?.showToast(Toast.LENGTH_LONG)
            refresh()
        })
        viewModel.coinModifyResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            Log.d(TAG, msg?.msg ?: "")
            msg?.msg?.showToast(Toast.LENGTH_LONG)
            refresh()
        })
        viewModel.linkArticleResult.observe(this, Observer { result ->
            val articlePage = result.getOrNull()
            if (articlePage != null) {
                if (articlePage.articles.isNotEmpty()) {
                    startActivity<ArticleActivity>(this) {
                        putExtra("show_name", getPermissionName(12))
                        putExtra("article_page", articlePage)
                        putExtra("search_type", "2")
                    }

                } else {
                    "尚无主题".showToast()
                }
            } else {
                "请求失败".showToast()
                Log.d(TAG, "ArticlePage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.linkCommentResult.observe(this, Observer { result ->
            val commentPage = result.getOrNull()
            if (commentPage != null) {
                if (commentPage.comments.isNotEmpty()) {
                    startActivity<CommentActivity>(this) {
                        putExtra("show_name", getPermissionName(13))
                        putExtra("comment_page", commentPage)
                        putExtra("q_type", "3")
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
        viewModel.linkReplyResult.observe(this, Observer { result ->
            val replyPage = result.getOrNull()
            if (replyPage != null) {
                if (replyPage.replies.isNotEmpty()) {
                    startActivity<ReplyActivity>(this) {
                        putExtra("show_name", getPermissionName(14))
                        putExtra("reply_page", replyPage)
                        putExtra("q_type", "3")
                    }
                } else {
                    "尚无回帖".showToast()
                }
            } else {
                "请求失败".showToast()
                Log.d(TAG, "ReplyPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })



        val userPage = intent.getParcelableExtra("user_page") as? UserPage
        if (userPage != null) {
            viewModel.query = userPage.users[0].nickname
            refresh(userPage)
        } else {
            search()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.search -> {
                supportActionBar?.let {
                    it.title = null
                    searchUserEdit.visibility = View.VISIBLE
                    searchUserEdit.showKeyboard()
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

    private fun refresh(userPage: UserPage) {
        viewModel.totalCount = userPage.count
        viewModel.totalPage = (userPage.count + viewModel.pageSize - 1) / viewModel.pageSize
        viewModel.users.clear()
        viewModel.users.addAll(userPage.users)
        adapter.notifyDataSetChanged()
    }

    private fun search(targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query) {
        viewModel.search(targetPage, queryContent)
        swipeRefresh.isRefreshing = true
    }

    private fun refresh(targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query) {
        viewModel.refresh(targetPage, queryContent)
    }

    fun hideAll(uid: Int) {
        viewModel.hideAll(uid)
    }

    fun ban(user: LKUser, banDay: Int) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        when(banDay) {
            0 -> {
                user.banEndDate = "1970-01-01T00:00:01.970Z"
            }
            -1 -> {
                user.banEndDate = LocalDateTime.now().plusYears(274).format(formatter)
            }
            else -> {
                user.banEndDate = LocalDateTime.now().plusDays(banDay.toLong()).format(formatter)
            }
        }
        viewModel.ban(user)
    }

    fun adventurerModify(uid: Int, modifiedTo: Int) {
        viewModel.adventurerModify(uid, modifiedTo)
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

    fun linkArticle(uid: Int) {
        viewModel.linkArticle(uid)
    }

    fun linkComment(uid: Int) {
        viewModel.linkComment(uid)
    }

    fun linkReply(uid: Int) {
        viewModel.linkReply(uid)
    }

}