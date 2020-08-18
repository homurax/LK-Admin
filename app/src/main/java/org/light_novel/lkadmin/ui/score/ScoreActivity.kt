package org.light_novel.lkadmin.ui.score

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_score.*
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.cutManage
import org.light_novel.lkadmin.extension.hideKeyboard
import org.light_novel.lkadmin.extension.showKeyboard
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.logic.model.ScorePage

class ScoreActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ScoreActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(ScoreViewModel::class.java) }

    private lateinit var adapter: ScoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_score)

        val showName = intent.getStringExtra("show_name")?.cutManage()

        // searchTypeSpinner
        ArrayAdapter.createFromResource(
            this,
            R.array.score_query_type_array,
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
        adapter = ScoreAdapter(this, viewModel.scores)
        recyclerView.adapter = adapter

        // searchScoreEdit
        searchScoreEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                searchScoreEdit.hideKeyboard()
                supportActionBar?.let {
                    it.title = showName
                    searchLayout.visibility = View.GONE
                }
                val searchType = if (searchTypeSpinner.selectedItemPosition == 0) null else searchTypeSpinner.selectedItemPosition
                search(1, searchScoreEdit.editableText.toString(), searchType)
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
            val scorePage = result.getOrNull()
            if (scorePage != null) {
                refresh(scorePage)
                recyclerView.scrollToPosition(0)
            } else {
                "请求失败".showToast()
                Log.d(TAG, "ScorePage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        viewModel.refreshResult.observe(this, Observer { result ->
            val scorePage = result.getOrNull()
            if (scorePage != null) {
                refresh(scorePage)
            } else {
                "刷新失败".showToast()
                Log.d(TAG, "ScorePage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.hideResult.observe(this, Observer { result ->
            // 目前返回为 [1] 无法判断请求是否成功
            val responseBody = result.getOrNull()
            responseBody.let { Log.d(TAG, it.toString()) }
            refresh()
        })

        val scorePage = intent.getParcelableExtra("score_page") as? ScorePage
        if (scorePage != null) {
            viewModel.query = scorePage.rows[0].sid.toString()
            viewModel.searchType = 2
            refresh(scorePage)
        } else {
            search()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_score, menu)
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
                    searchScoreEdit.showKeyboard()
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

    private fun refresh(scorePage: ScorePage) {
        viewModel.totalCount = scorePage.count
        viewModel.totalPage = (scorePage.count + viewModel.pageSize - 1) / viewModel.pageSize
        viewModel.scores.clear()
        viewModel.scores.addAll(scorePage.rows)
        adapter.notifyDataSetChanged()
    }

    fun search(targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query, targetSearchType: Int? = viewModel.searchType) {
        viewModel.search(targetPage, queryContent, targetSearchType)
        swipeRefresh.isRefreshing = true
    }

    private fun refresh(targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query, targetSearchType: Int? = viewModel.searchType) {
        viewModel.refresh(targetPage, queryContent, targetSearchType)
    }

    fun scoreHide(sid: Int, uid: Int, status: Int) {
        viewModel.scoreHide(sid, uid, status)
    }

}