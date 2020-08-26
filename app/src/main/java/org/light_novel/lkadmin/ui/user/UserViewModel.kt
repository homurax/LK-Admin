package org.light_novel.lkadmin.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.*

class UserViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<SimpleQuery>()

    private val refreshLiveData = MutableLiveData<SimpleQuery>()

    private val hideAllLiveData = MutableLiveData<Int>()

    private val banLiveData = MutableLiveData<LKUser>()

    private val modifyAdventurerLiveData = MutableLiveData<ModifyAdventurer>()

    private val coinModifyLiveData = MutableLiveData<ModifyCoin>()

    private val linkArticleLiveData = MutableLiveData<Int>()

    private val linkCommentLiveData = MutableLiveData<Int>()

    private val linkReplyLiveData = MutableLiveData<Int>()

    private val headers = mapOf("token" to getToken(), "signature" to "{}".sign())


    val users = ArrayList<LKUser>()
    var currPage = 1
    var totalPage = 1
    var totalCount = 1
    var pageSize = 10
    var query = ""


    val searchResult = Transformations.switchMap(searchLiveData) {
        val searchMap = mutableMapOf("page" to it.page.toString(), "query" to it.query)
        Repository.userPage(searchMap, headers)
    }

    fun search(targetPage: Int, queryContent: String) {
        currPage = targetPage
        query = queryContent
        searchLiveData.value = SimpleQuery(targetPage, queryContent)
    }

    val refreshResult = Transformations.switchMap(refreshLiveData) {
        val searchMap = mutableMapOf("page" to it.page.toString(), "query" to it.query)
        Repository.userPage(searchMap, headers)
    }

    fun refresh(targetPage: Int, queryContent: String) {
        currPage = targetPage
        query = queryContent
        refreshLiveData.value = SimpleQuery(targetPage, queryContent)
    }

    val hideAllResult = Transformations.switchMap(hideAllLiveData) {
        val hideUser = HideUser(it)
        Repository.hideAll(hideUser, mapOf("token" to getToken(), "signature" to hideUser.sign()))
    }

    fun hideAll(uid: Int) {
        hideAllLiveData.value = uid
    }

    val banResult = Transformations.switchMap(banLiveData) {
        val banHeaders = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.userModify(it, it.uid.toString(), banHeaders)
    }

    fun ban(user: LKUser) {
        banLiveData.value = user
    }

    val modifyAdventurerResult = Transformations.switchMap(modifyAdventurerLiveData) {
        val currHeaders = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.modifyAdventurer(it, it.uid, currHeaders)
    }

    fun adventurerModify(uid: Int, modifiedTo: Int) {
        val sendMsg = if (modifiedTo == 1) 2 else null
        modifyAdventurerLiveData.value = ModifyAdventurer(uid, modifiedTo, sendMsg)
    }

    val coinModifyResult = Transformations.switchMap(coinModifyLiveData) {
        val currHeaders = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.coinModify(it, it.uid, currHeaders)
    }

    fun coinModify(modifyCoin: ModifyCoin) {
        coinModifyLiveData.value = modifyCoin
    }

    val linkArticleResult = Transformations.switchMap(linkArticleLiveData) {
        val searchMap = mutableMapOf("search_type" to "2", "page" to "1", "query" to it.toString())
        Repository.articlePage(searchMap, headers)
    }

    fun linkArticle(uid: Int) {
        linkArticleLiveData.value = uid
    }

    val linkCommentResult = Transformations.switchMap(linkCommentLiveData) {
        val searchMap = mutableMapOf("datatype" to "comments", "qtype" to "3", "page" to "1", "query" to it.toString())
        Repository.commentPage(searchMap, headers)
    }

    fun linkComment(uid: Int) {
        linkCommentLiveData.value = uid
    }

    val linkReplyResult = Transformations.switchMap(linkReplyLiveData) {
        val searchMap = mutableMapOf("datatype" to "replies", "qtype" to "3", "page" to "1", "query" to it.toString())
        Repository.replyPage(searchMap, headers)
    }

    fun linkReply(uid: Int) {
        linkReplyLiveData.value = uid
    }
}