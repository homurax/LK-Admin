package org.light_novel.lkadmin.ui.main

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.light_novel.lkadmin.LKAdminApplication
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.extension.startActivity
import org.light_novel.lkadmin.logic.model.LKPermissionItem
import org.light_novel.lkadmin.logic.model.getPermissionItems
import org.light_novel.lkadmin.logic.model.getPermissionName
import org.light_novel.lkadmin.logic.model.getUsername
import org.light_novel.lkadmin.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_main)

        refreshViewModelPermission()

        // toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.title = "LK-Admin"
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        // NavigationView
        val inflateHeaderView = navView.inflateHeaderView(R.layout.nav_header)
        navView.setCheckedItem(R.id.navHome)
        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            when (it.itemId) {
                R.id.navHome -> refresh()
            }
            true
        }
        val usernameText = inflateHeaderView.findViewById<TextView>(R.id.username)
        usernameText.visibility = View.VISIBLE
        usernameText.text = getUsername()
        usernameText.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("登出")
                setMessage("确定退出?")
                setCancelable(false)
                setPositiveButton("是") { dialog, which ->
                    LKAdminApplication.context.getSharedPreferences("authorize", Context.MODE_PRIVATE).edit().clear().apply()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    "已登出".showToast()
                    startActivity<LoginActivity>(this@MainActivity) {}
                    finish()
                }
                setNegativeButton("否") { dialog, which ->
                }
                show()
            }
        }


        // RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = MainAdapter(this, viewModel.permissionItems)
        recyclerView.adapter = adapter

        // SwipeRefreshLayout
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            refresh()
        }
        viewModel.refreshResult.observe(this, Observer { result ->
            val loginResponse = result.getOrNull()
            if (loginResponse != null) {
                LKAdminApplication.context.getSharedPreferences("authorize", Context.MODE_PRIVATE).edit().run {
                    putString("permissionItems", Gson().toJson(loginResponse.permissions.map { LKPermissionItem(it.id, it.routeName, getPermissionName(it.id)) }.toList()))
                    apply()
                }
                refreshViewModelPermission()
                adapter.notifyDataSetChanged()
            } else {
                "刷新失败".showToast()
                Log.d(TAG, "LoginResponse is null")
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
    }

    private fun refresh() {
        viewModel.refresh()
        swipeRefresh.isRefreshing = true
    }

    private fun refreshViewModelPermission() {
        viewModel.permissionItems.clear()
        val permissions = getPermissionItems().filter { it.showName.isNotEmpty() }.toList()
        viewModel.permissionItems.addAll(permissions)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

}