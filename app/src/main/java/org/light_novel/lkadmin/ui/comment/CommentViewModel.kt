package org.light_novel.lkadmin.ui.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.Comment
import org.light_novel.lkadmin.logic.model.HideComment
import org.light_novel.lkadmin.logic.model.ModifyCoin
import org.light_novel.lkadmin.logic.model.getToken

class CommentViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<Any?>()

    private val refreshLiveData = MutableLiveData<Any?>()

    private val hideLiveData = MutableLiveData<HideComment>()

    private val linkReplyLiveData = MutableLiveData<Int>()

    private val authorLiveData = MutableLiveData<Int>()

    private val userLiveData = MutableLiveData<Int>()

    private val coinModifyLiveData = MutableLiveData<ModifyCoin>()

    private val headers = mapOf("token" to getToken(), "signature" to "{}".sign())

    val comments = ArrayList<Comment>()
    var currPage = 1
    var totalPage = 1
    var totalCount = 1
    var pageSize = 10
    var qType = ""
    var query = ""

    val searchResult = Transformations.switchMap(searchLiveData) {
        val searchMap = mutableMapOf("datatype" to "comments", "qtype" to qType, "page" to currPage.toString(), "query" to query)
        Repository.commentPage(searchMap, headers)
    }

    fun search(targetQType: String, targetPage: Int, queryContent: String) {
        qType = targetQType
        currPage = targetPage
        query = queryContent
        searchLiveData.value = searchLiveData.value
    }

    val refreshResult = Transformations.switchMap(refreshLiveData) {
        val searchMap = mutableMapOf("datatype" to "comments", "qtype" to qType, "page" to currPage.toString(), "query" to query)
        Repository.commentPage(searchMap, headers)
    }

    fun refresh(targetQType: String, targetPage: Int, queryContent: String) {
        qType = targetQType
        currPage = targetPage
        query = queryContent
        refreshLiveData.value = refreshLiveData.value
    }

    val hideResult = Transformations.switchMap(hideLiveData) {
        val hideHeaders = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.commentHide(it, it.tid.toString(), hideHeaders)
    }

    fun commentHide(tid: Int, status: Int, content: String) {
        hideLiveData.value = HideComment(status, tid, content)
    }

    val linkReplyResult = Transformations.switchMap(linkReplyLiveData) {
        val searchMap = mutableMapOf("datatype" to "replies", "qtype" to "2", "page" to "1", "query" to it.toString())
        Repository.replyPage(searchMap, headers)
    }

    fun linkReply(tid: Int) {
        linkReplyLiveData.value = tid
    }

    val authorResult = Transformations.switchMap(authorLiveData) {
        val searchMap = mutableMapOf("page" to "1", "query" to it.toString())
        Repository.userPage(searchMap, headers)
    }

    fun findCommentAuthor(uid: Int) {
        authorLiveData.value = uid
    }

    val userResult = Transformations.switchMap(userLiveData) {
        val searchMap = mutableMapOf("page" to "1", "query" to it.toString())
        Repository.findOneUser(searchMap, headers)
    }

    fun findOneUser(uid: Int) {
        userLiveData.value = uid
    }

    val coinModifyResult = Transformations.switchMap(coinModifyLiveData) {
        val currHeaders = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.coinModify(it, it.uid, currHeaders)
    }

    fun coinModify(modifyCoin: ModifyCoin) {
        coinModifyLiveData.value = modifyCoin
    }
}