package org.light_novel.lkadmin.ui.series

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_series.*
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.cutManage
import org.light_novel.lkadmin.extension.hideKeyboard
import org.light_novel.lkadmin.extension.showKeyboard
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.extension.startActivity
import org.light_novel.lkadmin.logic.model.*
import org.light_novel.lkadmin.ui.score.ScoreActivity

class SeriesActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SeriesActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(SeriesViewModel::class.java) }

    private lateinit var adapter: SeriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_series)

        val showName = intent.getStringExtra("show_name")?.cutManage()

        // searchTypeSpinner
        ArrayAdapter.createFromResource(
            this,
            R.array.series_query_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
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
        adapter = SeriesAdapter(this, viewModel.seriesList)
        recyclerView.adapter = adapter

        // searchSeriesEdit
        searchSeriesEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                searchSeriesEdit.hideKeyboard()
                supportActionBar?.let {
                    it.title = showName
                    searchLayout.visibility = View.GONE
                }
                val searchType = if (searchTypeSpinner.selectedItemPosition == 0) null else searchTypeSpinner.selectedItemPosition
                search(1, searchSeriesEdit.editableText.toString(), searchType)
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
            val seriesPage = result.getOrNull()
            if (seriesPage != null) {
                refresh(seriesPage)
                recyclerView.scrollToPosition(0)
            } else {
                "请求失败".showToast()
                Log.d(TAG, "SeriesPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        viewModel.refreshResult.observe(this, Observer { result ->
            val seriesPage = result.getOrNull()
            if (seriesPage != null) {
                refresh(seriesPage)
            } else {
                "刷新失败".showToast()
                Log.d(TAG, "SeriesPage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.hideResult.observe(this, Observer { result ->
            val msgCode = result.getOrNull()
            refresh()
        })
        viewModel.linkScoreResult.observe(this, Observer { result ->
            val scorePage = result.getOrNull()
            if (scorePage != null) {
                if (scorePage.rows.isNotEmpty()) {
                    startActivity<ScoreActivity>(this) {
                        putExtra("show_name", getPermissionName(18))
                        putExtra("score_page", scorePage)
                    }
                } else {
                    "暂无评分".showToast()
                }
            } else {
                "请求失败".showToast()
                Log.d(TAG, "ScorePage is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.detailResult.observe(this, Observer { result ->
            val seriesDetail = result.getOrNull()
            if (seriesDetail != null) {
                val users = seriesDetail.users
                val articles = seriesDetail.articles

                val contentView = LayoutInflater.from(this).inflate(R.layout.series_detail, null)
                val recyclerView: RecyclerView = contentView.findViewById(R.id.recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = SeriesDetailAdapter(this, users, articles)

                AlertDialog.Builder(this).apply {
                    setTitle("集合详情")
                    setCancelable(false)
                    setView(contentView)
                    setNegativeButton("关闭") { dialog, which ->
                    }
                    show()
                }
            } else {
                "请求失败".showToast()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.unfoldResult.observe(this, Observer { result ->
            val articles = result.getOrNull()
            if (articles != null) {
                val contentView = LayoutInflater.from(this).inflate(R.layout.series_unfold, null)
                val recyclerView: RecyclerView = contentView.findViewById(R.id.recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = SeriesUnfoldAdapter(this, ArrayList(articles))

                AlertDialog.Builder(this).apply {
                    setTitle("展开合集")
                    setCancelable(false)
                    setView(contentView)
                    setNegativeButton("关闭") { dialog, which ->
                    }
                    show()
                }
            } else {
                "合集内暂无文章".showToast()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.removeResult.observe(this, Observer { result ->
            val msg = result.getOrNull()
            if (msg != null) {
                msg.msg.showToast()
            } else {
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        search()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_series, menu)
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
                    searchSeriesEdit.showKeyboard()
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


    private fun refresh(seriesPage: SeriesPage) {
        viewModel.totalCount = seriesPage.count
        viewModel.totalPage = (seriesPage.count + viewModel.pageSize - 1) / viewModel.pageSize
        viewModel.seriesList.clear()
        viewModel.seriesList.addAll(seriesPage.rows)
        adapter.notifyDataSetChanged()
    }

    fun search(targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query, targetSearchType: Int? = viewModel.searchType) {
        viewModel.search(targetPage, queryContent, targetSearchType)
        swipeRefresh.isRefreshing = true
    }

    private fun refresh(targetPage: Int = viewModel.currPage, queryContent: String = viewModel.query, targetSearchType: Int? = viewModel.searchType) {
        viewModel.refresh(targetPage, queryContent, targetSearchType)
    }

    fun seriesHide(hideSeries: HideSeries, sid: Int) {
        viewModel.seriesHide(HideSeriesAO(hideSeries, sid))
    }

    fun linkScore(sid: Int) {
        viewModel.linkScore(sid)
    }

    fun seriesDetail(sid: Int) {
        viewModel.seriesDetail(sid)
    }

    fun unfold(sid: Int) {
        viewModel.unfold(sid)
    }

    fun removeArticle(article: SeriesArticle) {
        viewModel.removeArticle(article)
    }
}