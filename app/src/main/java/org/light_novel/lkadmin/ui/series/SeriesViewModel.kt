package org.light_novel.lkadmin.ui.series

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.*

class SeriesViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<SimpleTypeQuery>()

    private val refreshLiveData = MutableLiveData<SimpleTypeQuery>()

    private val hideLiveData = MutableLiveData<HideSeriesAO>()

    private val linkScoreLiveData = MutableLiveData<Int>()

    private val detailLiveData = MutableLiveData<Int>()

    private val unfoldLiveData = MutableLiveData<Int>()

    private val removeLiveData = MutableLiveData<SeriesArticle>()

    private val headers = mapOf("token" to getToken(), "signature" to "{}".sign())

    val seriesList = ArrayList<Series>()
    var currPage = 1
    var totalPage = 1
    var totalCount = 1
    var pageSize = 10
    var query = ""
    var searchType: Int? = null

    val searchResult = Transformations.switchMap(searchLiveData) {
        val searchMap = mutableMapOf("page" to it.page.toString(), "query" to it.query)
        searchType?.let { type -> searchMap["search_type"] = type.toString() }
        Repository.seriesPage(searchMap, headers)
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
        Repository.seriesPage(searchMap, headers)
    }

    fun refresh(targetPage: Int, queryContent: String, targetSearchType: Int?) {
        currPage = targetPage
        query = queryContent
        searchType = targetSearchType
        refreshLiveData.value = SimpleTypeQuery(queryContent, targetSearchType, targetPage)
    }

    val hideResult = Transformations.switchMap(hideLiveData) {
        val currHeaders = mapOf("token" to getToken(), "signature" to it.hideSeries.sign())
        Repository.seriesHide(it.hideSeries, it.sid, currHeaders)
    }

    fun seriesHide(hideSeriesAO: HideSeriesAO) {
        hideLiveData.value = hideSeriesAO
    }

    val linkScoreResult = Transformations.switchMap(linkScoreLiveData) {
        val searchMap = mutableMapOf("page" to "1", "query" to it.toString(), "search_type" to "2")
        Repository.scorePage(searchMap, headers)
    }

    fun linkScore(sid: Int) {
        linkScoreLiveData.value = sid
    }

    val detailResult = Transformations.switchMap(detailLiveData) {
        Repository.seriesDetail(it, headers)
    }

    fun seriesDetail(sid: Int) {
        detailLiveData.value = sid
    }

    val unfoldResult = Transformations.switchMap(unfoldLiveData) {
        Repository.seriesArticle(it, headers)
    }

    fun unfold(sid: Int) {
        unfoldLiveData.value = sid
    }

    val removeResult = Transformations.switchMap(removeLiveData) {
        val currHeaders = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.seriesRemove(it, currHeaders)
    }

    fun removeArticle(article: SeriesArticle) {
        removeLiveData.value = article
    }

}