package org.light_novel.lkadmin.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.Message
import org.light_novel.lkadmin.logic.model.getToken

class MessageViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<Any?>()

    private val refreshLiveData = MutableLiveData<Any?>()

    private val deleteLiveData = MutableLiveData<Int>()

    private val headers = mapOf("token" to getToken(), "signature" to "{}".sign())

    val messages = ArrayList<Message>()
    var currPage = 1
    var totalPage = 1
    var totalCount = 1
    var pageSize = 10


    val searchResult = Transformations.switchMap(searchLiveData) {
        val searchMap = mutableMapOf("page" to currPage.toString())
        Repository.messagePage(searchMap, headers)
    }

    fun search(targetPage: Int) {
        currPage = targetPage
        searchLiveData.value = searchLiveData.value
    }

    val refreshResult = Transformations.switchMap(refreshLiveData) {
        val searchMap = mutableMapOf("page" to currPage.toString())
        Repository.messagePage(searchMap, headers)
    }

    fun refresh(targetPage: Int) {
        currPage = targetPage
        searchLiveData.value = searchLiveData.value
    }

    val deleteResult = Transformations.switchMap(deleteLiveData) {
        Repository.deleteMessage(it, headers)
    }

    fun deleteMessage(id: Int) {
        deleteLiveData.value = id
    }
}