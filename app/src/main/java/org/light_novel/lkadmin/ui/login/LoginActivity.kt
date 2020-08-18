package org.light_novel.lkadmin.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.light_novel.lkadmin.LKAdminApplication
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.extension.hideKeyboard
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.extension.startActivity
import org.light_novel.lkadmin.logic.model.LKPermissionItem
import org.light_novel.lkadmin.logic.model.LoginUser
import org.light_novel.lkadmin.logic.model.getPermissionName
import org.light_novel.lkadmin.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prefs = LKAdminApplication.context.getSharedPreferences("authorize", Context.MODE_PRIVATE)
        if (!prefs.getString("token", "").isNullOrEmpty()) {
            startActivity<MainActivity>(this) {}
            finish()
        }

        login.setOnClickListener {
            it.hideKeyboard()
            val username = accountEdit.text.toString()
            if (username.isEmpty()) {
                "未填写用户名".showToast()
                return@setOnClickListener
            }
            val password = passwordEdit.text.toString()
            if (password.isEmpty()) {
                "未填写密码".showToast()
                return@setOnClickListener
            }
            LKAdminApplication.context.getSharedPreferences("authorize", Context.MODE_PRIVATE).edit().run {
                putString("username", username)
                putString("password", password)
                apply()
            }
            viewModel.login(LoginUser(username, password))
        }

        viewModel.loginResult.observe(this, Observer { result ->
            val loginResponse = result.getOrNull()
            if (loginResponse != null) {
                LKAdminApplication.context.getSharedPreferences("authorize", Context.MODE_PRIVATE).edit().run {
                    putString("token", loginResponse.token)
                    putString("permissionItems", Gson().toJson(loginResponse.permissions.map { LKPermissionItem(it.id, it.routeName, getPermissionName(it.id)) }.toList()))
                    apply()
                }
                "${loginResponse.username}, 登录成功".showToast()
                startActivity<MainActivity>(this) {}
                finish()
            } else {
                "登录失败".showToast()
                Log.d(TAG, "LoginResponse is null")
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}