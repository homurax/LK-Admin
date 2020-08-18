package org.light_novel.lkadmin.ui.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.*

class ArticleViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<SimpleTypeQuery>()

    private val refreshLiveData = MutableLiveData<SimpleTypeQuery>()

    private val maskLiveData = MutableLiveData<Map<String, Int>>()

    private val topLiveData = MutableLiveData<Map<String, String?>>()

    private val linkCommentLiveData = MutableLiveData<Int>()

    private val authorLiveData = MutableLiveData<String>()

    private val userLiveData = MutableLiveData<String>()

    private val coinModifyLiveData = MutableLiveData<ModifyCoin>()

    private val headers = mapOf("token" to getToken(), "signature" to "{}".sign())

    val articles = ArrayList<Article>()
    var currPage = 1
    var totalPage = 1
    var totalCount = 1
    var pageSize = 10
    var query = ""
    var searchType: Int? = null

    val searchResult = Transformations.switchMap(searchLiveData) {
        val searchMap = mutableMapOf("page" to it.page.toString(), "query" to it.query)
        searchType?.let { type -> searchMap["search_type"] = type.toString() }
        Repository.articlePage(searchMap, headers)
    }

    fun search(targetPage: Int, queryContent: String, targetSearchType: Int?) {
        currPage = targetPage
        query = queryContent
        searchType = targetSearchType
        searchLiveData.value = SimpleTypeQuery(queryContent, targetSearchType, targetPage)
    }

    val refreshResult = Transformations.switchMap(refreshLiveData) {
        val searchMap = mutableMapOf("page" to it.page.toString(), "query" to it.query)
        searchType?.let { type -> searchMap["search_type"] = type.toString() }
        Repository.articlePage(searchMap, headers)
    }

    fun refresh(targetPage: Int, queryContent: String, targetSearchType: Int?) {
        currPage = targetPage
        query = queryContent
        searchType = targetSearchType
        refreshLiveData.value = SimpleTypeQuery(queryContent, targetSearchType, targetPage)
    }

    val maskResult = Transformations.switchMap(maskLiveData) {
        val mask = it["mask"] ?: error("")
        val sendMsg = if (mask == 1) 1 else null
        val maskArticle = MaskArticle(mask, sendMsg)
        val maskHeaders = mapOf("token" to getToken(), "signature" to maskArticle.sign())
        Repository.articleMask(maskArticle, it["aid"].toString(), maskHeaders)
    }

    fun articleMask(aid: Int, mask: Int) {
        maskLiveData.value = mapOf("aid" to aid, "mask" to mask)
    }

    val topResult = Transformations.switchMap(topLiveData) {
        val lastTime = it["lastTime"] ?: error("")
        val sendMsg = it["sendMsg"]?.toInt()
        val topArticle = TopArticle(lastTime, sendMsg)
        val topHeaders = mapOf("token" to getToken(), "signature" to topArticle.sign())
        Repository.articleTop(topArticle, it["aid"] ?: error(""), topHeaders)
    }

    fun articleTop(aid: Int, lastTime: String, sendMsg: String? = null) {
        topLiveData.value = mapOf("aid" to aid.toString(), "lastTime" to lastTime, "sendMsg" to sendMsg)
    }

    val linkCommentResult = Transformations.switchMap(linkCommentLiveData) {
        val searchMap = mutableMapOf("datatype" to "comments", "qtype" to "1", "page" to "1", "query" to it.toString())
        Repository.commentPage(searchMap, headers)
    }

    fun linkComment(aid: Int) {
        linkCommentLiveData.value = aid
    }

    val authorResult = Transformations.switchMap(authorLiveData) {
        val searchMap = mutableMapOf("page" to "1", "query" to it)
        Repository.findOneInUserPage(searchMap, headers)
    }

    fun findArticleAuthor(nickname: String) {
        authorLiveData.value = nickname
    }

    val userResult = Transformations.switchMap(userLiveData) {
        val searchMap = mutableMapOf("page" to "1", "query" to it)
        Repository.findOneUser(searchMap, headers)
    }

    fun findOneUser(nickname: String) {
        userLiveData.value = nickname
    }

    val coinModifyResult = Transformations.switchMap(coinModifyLiveData) {
        val currHeaders = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.coinModify(it, it.uid, currHeaders)
    }

    fun coinModify(modifyCoin: ModifyCoin) {
        coinModifyLiveData.value = modifyCoin
    }

}