package org.light_novel.lkadmin.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.LoginResponse
import org.light_novel.lkadmin.logic.model.LoginUser

class LoginViewModel : ViewModel() {

    private val loginRequestLiveData = MutableLiveData<LoginUser>()


    val loginResult = Transformations.switchMap(loginRequestLiveData) {
        Repository.login(it.sign(), it)
    }


    fun login(loginUser: LoginUser) {
        loginRequestLiveData.value = loginUser
    }
}