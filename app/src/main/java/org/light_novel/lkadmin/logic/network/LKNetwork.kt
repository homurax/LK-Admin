package org.light_novel.lkadmin.logic.network

import org.light_novel.lkadmin.logic.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object LKNetwork {

    // service
    private val service = ServiceCreator.create<LKAdminService>()

    // suspend function
    suspend fun login(signature: String, loginUser: LoginUser) = service.login(signature, loginUser).await()

    suspend fun articlePage(searchMap: Map<String, String>, headers: Map<String, String>) = service.articlePage(searchMap, headers).await()
    
    suspend fun articleMask(mask: MaskArticle, aid: String, headers: Map<String, String>) = service.articleMask(mask, aid, headers).await()

    suspend fun articleTop(top: TopArticle, aid: String, headers: Map<String, String>) = service.articleTop(top, aid, headers).await()

    suspend fun commentPage(searchMap: Map<String, String>, headers: Map<String, String>) = service.commentPage(searchMap, headers).await()

    suspend fun commentHide(hideComment: HideComment, tid: String, headers: Map<String, String>) = service.commentHide(hideComment, tid, headers).await()

    suspend fun replyPage(searchMap: Map<String, String>, headers: Map<String, String>) = service.replyPage(searchMap, headers).await()

    suspend fun replyHide(hideReply: HideReply, tid: Int, headers: Map<String, String>) = service.replyHide(hideReply, tid, headers).await()

    suspend fun userPage(searchMap: Map<String, String>, headers: Map<String, String>) = service.userPage(searchMap, headers).await()

    suspend fun hideAll(hideUser: HideUser, headers: Map<String, String>) = service.hideAll(hideUser, headers).await()

    suspend fun userModify(user: LKUser, uid: String, headers: Map<String, String>) = service.userModify(user, uid, headers).await()

    suspend fun userInfo(uid: String, headers: Map<String, String>) = service.userInfo(uid, headers).await()

    suspend fun modifyAdventurer(adventurer: ModifyAdventurer, uid: Int, headers: Map<String, String>) = service.modifyAdventurer(adventurer, uid, headers).await()

    suspend fun coinModifyLog(modifyCoinLog: ModifyCoinLog, headers: Map<String, String>) = service.coinModifyLog(modifyCoinLog, headers).await()

    suspend fun coinModify(modifyCoin: ModifyCoin, uid: Int, headers: Map<String, String>) = service.coinModify(modifyCoin, uid, headers).await()

    suspend fun messagePage(searchMap: Map<String, String>, headers: Map<String, String>) = service.messagePage(searchMap, headers).await()

    suspend fun deleteMessage(id: Int, headers: Map<String, String>) = service.deleteMessage(id, headers).await()

    suspend fun sendMessage(sendMessage: SendMessage, headers: Map<String, String>) = service.sendMessage(sendMessage, headers).await()

    suspend fun scorePage(searchMap: Map<String, String>, headers: Map<String, String>) = service.scorePage(searchMap, headers).await()

    suspend fun scoreHide(hideScore: HideScore, headers: Map<String, String>) = service.scoreHide(hideScore, headers).await()

    suspend fun seriesPage(searchMap: Map<String, String>, headers: Map<String, String>) = service.seriesPage(searchMap, headers).await()

    suspend fun seriesDetail(sid: Int, headers: Map<String, String>) = service.seriesDetail(sid, headers).await()

    suspend fun seriesArticle(sid: Int, headers: Map<String, String>) = service.seriesArticle(sid, headers).await()

    suspend fun seriesHide(hideSeries: HideSeries, sid: Int, headers: Map<String, String>) = service.seriesHide(hideSeries, sid, headers).await()

    suspend fun seriesRemove(article: SeriesArticle, headers: Map<String, String>) = service.seriesRemove(article, headers).await()

    suspend fun recommendList(searchMap: Map<String, String>, headers: Map<String, String>) = service.recommendList(searchMap, headers).await()


    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("Response body is null."))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}