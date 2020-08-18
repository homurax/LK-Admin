package org.light_novel.lkadmin.ui.recommendation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.Recommend
import org.light_novel.lkadmin.logic.model.getToken

class WebRecommendViewModel : ViewModel() {

    private val refreshLiveData = MutableLiveData<Any?>()


    private val searchMap = mutableMapOf("type" to "web")

    private val headers = mapOf("token" to getToken(), "signature" to "{}".sign())


    val recommends = ArrayList<Recommend>()


    val refreshResult = Transformations.switchMap(refreshLiveData) {
        Repository.recommendList(searchMap, headers)
    }

    fun refresh() {
        refreshLiveData.value = refreshLiveData.value
    }
}