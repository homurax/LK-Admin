package org.light_novel.lkadmin.logic

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import org.light_novel.lkadmin.extension.showToast
import org.light_novel.lkadmin.logic.model.*
import org.light_novel.lkadmin.logic.network.LKNetwork
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

object Repository {

    fun login(signature: String, loginUser: LoginUser) = fire(Dispatchers.IO) {
        val loginResponse = LKNetwork.login(signature, loginUser)
        if (loginResponse.token.isNotEmpty()) {
            Result.success(loginResponse)
        } else {
            loginResponse.msg.showToast()
            Result.failure(RuntimeException("Response message: ${loginResponse.msg}"))
        }
    }

    fun articlePage(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            if ((searchMap["search_type"] ?: "" == "2") and isWhitelistUser(searchMap["query"] ?: "")) {
                val emptyArticlePage = ArticlePage(0, 10, null, emptyList())
                Result.success(emptyArticlePage)
            } else {
                val articlePage = LKNetwork.articlePage(searchMap, headers)
                if (articlePage.msg == null) {
                    articlePage.articles = articlePage.articles.filterNot { isWhitelistUser(it.uid) }.toList()
                    Result.success(articlePage)
                } else {
                    articlePage.msg.showToast()
                    Result.failure(RuntimeException("Response message: ${articlePage.msg}"))
                }
            }
        }

    fun articleMask(mask: MaskArticle, aid: String, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msg = LKNetwork.articleMask(mask, aid, headers)
            Result.success(msg)
        }

    fun articleTop(top: TopArticle, aid: String, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msg = LKNetwork.articleTop(top, aid, headers)
            Result.success(msg)
        }

    fun commentPage(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            if ((searchMap["qtype"] ?: "" == "3") and isWhitelistUser(searchMap["query"] ?: "")) {
                val emptyCommentPage = CommentPage(0, 10, null, emptyList())
                Result.success(emptyCommentPage)
            } else {
                val commentPage = LKNetwork.commentPage(searchMap, headers)
                if (commentPage.msg == null) {
                    commentPage.comments = commentPage.comments.filterNot { isWhitelistUser(it.uid) }.toList()
                    Result.success(commentPage)
                } else {
                    commentPage.msg.showToast()
                    Result.failure(RuntimeException("Response message: ${commentPage.msg}"))
                }
            }
        }

    fun commentHide(hideComment: HideComment, tid: String, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msg = LKNetwork.commentHide(hideComment, tid, headers)
            Result.success(msg)
        }

    fun replyPage(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            if ((searchMap["qtype"] ?: "" == "3") and isWhitelistUser(searchMap["query"] ?: "")) {
                val emptyReplyPage = ReplyPage(0, 10, null, emptyList())
                Result.success(emptyReplyPage)
            } else {
                val replyPage = LKNetwork.replyPage(searchMap, headers)
                if (replyPage.msg == null) {
                    replyPage.replies = replyPage.replies.filterNot { isWhitelistUser(it.uid) }.toList()
                    Result.success(replyPage)
                } else {
                    replyPage.msg.showToast()
                    Result.failure(RuntimeException("Response message: ${replyPage.msg}"))
                }
            }
        }

    fun replyHide(hideReply: HideReply, tid: Int, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msg = LKNetwork.replyHide(hideReply, tid, headers)
            Result.success(msg)
        }

    fun userPage(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            if (isWhitelistUser(searchMap["query"] ?: "")) {
                val emptyUserPage = UserPage(0, 10, null, emptyList())
                Result.success(emptyUserPage)
            } else {
                val userPage = LKNetwork.userPage(searchMap, headers)
                if (userPage.msg == null) {
                    userPage.users = userPage.users.filterNot { isWhitelistUser(it.uid) }.toList()
                    Result.success(userPage)
                } else {
                    userPage.msg.showToast()
                    Result.failure(RuntimeException("Response message: ${userPage.msg}"))
                }
            }
        }

    fun findOneUser(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val uid = searchMap["query"]?.toInt()
            val userPage = LKNetwork.userPage(searchMap, headers)
            if (userPage.msg == null) {
                val user = userPage.users.find { it.uid == uid }
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(RuntimeException("Response message: Can not find $uid in UserPage"))
                }
            } else {
                userPage.msg.showToast()
                Result.failure(RuntimeException("Response message: ${userPage.msg}"))
            }
        }

    fun hideAll(hideUser: HideUser, headers: Map<String, String>) = fire(Dispatchers.IO) {
        val msg = LKNetwork.hideAll(hideUser, headers)
        Result.success(msg)
    }

    fun userModify(user: LKUser, uid: String, headers: Map<String, String>) = fire(Dispatchers.IO) {
        val msg = LKNetwork.userModify(user, uid, headers)
        Result.success(msg)
    }

    fun userInfo(uid: String, headers: Map<String, String>) = fire(Dispatchers.IO) {
        val user = LKNetwork.userInfo(uid, headers)
        if (user.msg == null) {
            Result.success(user)
        } else {
            user.msg.showToast()
            Result.failure(RuntimeException("Response message: ${user.msg}"))
        }
    }

    fun modifyAdventurer(adventurer: ModifyAdventurer, uid: Int, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msg = LKNetwork.modifyAdventurer(adventurer, uid, headers)
            Result.success(msg)
        }

    fun coinModifyLog(modifyCoinLog: ModifyCoinLog, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val modifyCoinInfo = LKNetwork.coinModifyLog(modifyCoinLog, headers)
            Result.success(modifyCoinInfo)
        }

    fun coinModify(modifyCoin: ModifyCoin, uid: Int, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msg = LKNetwork.coinModify(modifyCoin, uid, headers)
            Result.success(msg)
        }

    fun messagePage(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val messagePage = LKNetwork.messagePage(searchMap, headers)
            if (messagePage.msg == null) {
                Result.success(messagePage)
            } else {
                messagePage.msg.showToast()
                Result.failure(RuntimeException("Response message: ${messagePage.msg}"))
            }
        }

    fun deleteMessage(id: Int, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msgCode = LKNetwork.deleteMessage(id, headers)
            Result.success(msgCode)
        }

    fun sendMessage(sendMessage: SendMessage, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val responseBody = LKNetwork.sendMessage(sendMessage, headers)
            Result.success(responseBody)
        }

    fun scorePage(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            if ((searchMap["search_type"] ?: "" == "1") and isWhitelistUser(searchMap["query"] ?: "")) {
                val emptyScorePage = ScorePage(0, 10, emptyList(), null)
                Result.success(emptyScorePage)
            } else {
                val scorePage = LKNetwork.scorePage(searchMap, headers)
                if (scorePage.msg == null) {
                    scorePage.rows = scorePage.rows.filterNot { isWhitelistUser(it.uid) }.toList()
                    Result.success(scorePage)
                } else {
                    scorePage.msg.showToast()
                    Result.failure(RuntimeException("Response message: ${scorePage.msg}"))
                }
            }
        }

    fun scoreHide(hideScore: HideScore, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val responseBody = LKNetwork.scoreHide(hideScore, headers)
            Result.success(responseBody)
        }

    fun seriesPage(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val seriesPage = LKNetwork.seriesPage(searchMap, headers)
            if (seriesPage.msg == null) {
                Result.success(seriesPage)
            } else {
                seriesPage.msg.showToast()
                Result.failure(RuntimeException("Response message: ${seriesPage.msg}"))
            }
        }

    fun seriesDetail(sid: Int, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val seriesDetail = LKNetwork.seriesDetail(sid, headers)
            Result.success(seriesDetail)
        }

    fun seriesArticle(sid: Int, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val seriesArticles = LKNetwork.seriesArticle(sid, headers)
            Result.success(seriesArticles)
        }

    fun seriesHide(hideSeries: HideSeries, sid: Int, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msgCode = LKNetwork.seriesHide(hideSeries, sid, headers)
            Result.success(msgCode)
        }

    fun seriesRemove(article: SeriesArticle, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val msg = LKNetwork.seriesRemove(article, headers)
            Result.success(msg)
        }

    fun recommendList(searchMap: Map<String, String>, headers: Map<String, String>) =
        fire(Dispatchers.IO) {
            val recommends = LKNetwork.recommendList(searchMap, headers)
            Result.success(recommends)
        }


    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }


    const val TAG = "Repository"
}