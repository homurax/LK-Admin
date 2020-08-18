package org.light_novel.lkadmin.ui.score

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.*

class ScoreViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<SimpleTypeQuery>()

    private val refreshLiveData = MutableLiveData<SimpleTypeQuery>()

    private val hideLiveData = MutableLiveData<HideScore>()


    private val headers = mapOf("token" to getToken(), "signature" to "{}".sign())

    val scores = ArrayList<Score>()
    var currPage = 1
    var totalPage = 1
    var totalCount = 1
    var pageSize = 10
    var query = ""
    var searchType: Int? = null


    val searchResult = Transformations.switchMap(searchLiveData) {
        val searchMap = mutableMapOf("page" to it.page.toString(), "query" to it.query)
        searchType?.let { type -> searchMap["search_type"] = type.toString() }
        Repository.scorePage(searchMap, headers)
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
        Repository.scorePage(searchMap, headers)
    }

    fun refresh(targetPage: Int, queryContent: String, targetSearchType: Int?) {
        currPage = targetPage
        query = queryContent
        searchType = targetSearchType
        refreshLiveData.value = SimpleTypeQuery(queryContent, targetSearchType, targetPage)
    }

    val hideResult = Transformations.switchMap(hideLiveData) {
        val currHeaders = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.scoreHide(it, currHeaders)
    }

    fun scoreHide(sid: Int, uid: Int, status: Int) {
        hideLiveData.value = HideScore(sid, uid, status)
    }

}