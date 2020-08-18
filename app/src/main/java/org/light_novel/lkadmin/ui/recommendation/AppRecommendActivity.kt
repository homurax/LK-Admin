package org.light_novel.lkadmin.ui.recommendation

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_app_recommend.*
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.cutManage
import org.light_novel.lkadmin.extension.showToast

class AppRecommendActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AppRecommendActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(AppRecommendViewModel::class.java) }

    private lateinit var adapter: AppRecommendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_app_recommend)

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
        adapter = AppRecommendAdapter(this, viewModel.recommends)
        recyclerView.adapter = adapter

        // SwipeRefreshLayout
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            refresh()
        }
        viewModel.refreshResult.observe(this, Observer { result ->
            val recommends = result.getOrNull()
            if (recommends != null) {
                viewModel.recommends.clear()
                viewModel.recommends.addAll(recommends)
                adapter.notifyDataSetChanged()
            } else {
                "请求失败".showToast()
                Log.d(TAG, "Recommend List is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        refresh()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }


    fun refresh() {
        viewModel.refresh()
    }
}