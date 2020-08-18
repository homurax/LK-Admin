package org.light_novel.lkadmin.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.LKAdminApplication
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.LKPermissionItem
import org.light_novel.lkadmin.logic.model.LoginUser
import org.light_novel.lkadmin.logic.model.getPassword
import org.light_novel.lkadmin.logic.model.getUsername

class MainViewModel : ViewModel() {

    private val refreshLiveData = MutableLiveData<Any?>()

    val permissionItems = ArrayList<LKPermissionItem>()

    val refreshResult = Transformations.switchMap(refreshLiveData) {
        val loginUser = LoginUser(getUsername(), getPassword())
        Repository.login(loginUser.sign(), loginUser)
    }

    fun refresh() {
        refreshLiveData.value = refreshLiveData.value
    }
}