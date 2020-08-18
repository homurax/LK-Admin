package org.light_novel.lkadmin.logic.model

import com.google.gson.annotations.SerializedName


class HideUser(val uid: Int)

class ModifyAdventurer(
    val uid: Int,
    val passer: Int,
    @SerializedName("send_msg") val sendMsg: Int?
)

class ModifyCoinLog(val uid: Int, val coins: Int, val reason: String, val coinAction: Boolean)

class ModifyCoin(
    val uid: Int,
    val coin: Int,
    val coins: Int,
    val reason: String,
    @SerializedName("send_msg") val sendMsg: Int,
    val nickname: String,
    val avatar: String,
    val passer: Int,
    val sign: String,
    val gender: Int,
    val ip: String,
    @SerializedName("create_ip") val createIp: String,
    @SerializedName("create_date") val createDate: String
)

class MaskArticle(val mask: Int, @SerializedName("send_msg") val sendMsg: Int? = null)

class TopArticle(
    @SerializedName("last_time") val lastTime: String,
    @SerializedName("send_msg") val sendMsg: Int? = null
)

class HideComment(
    val status: Int,
    val tid: Int,
    val content: String,
    @SerializedName("is_topic") val isTopic: Boolean = true,
    @SerializedName("is_lock") val isLock: Boolean = true
)

class HideReply(
    val status: Int,
    val tid: Int,
    val rid: Int,
    val content: String,
    @SerializedName("is_topic") val isTopic: Boolean = false,
    @SerializedName("is_lock") val isLock: Boolean = true
)

class SendMessage(
    val title: String,
    val msg: String,
    @SerializedName("to_ids") val receivers: List<Int>
)

class HideScore(val sid: Int, val uid: Int, val status: Int)

class HideSeries(val status: Int, val name: String? = null, @SerializedName("send_msg") val sendMsg: Int? = null)

class HideSeriesAO(val hideSeries: HideSeries, val sid: Int)
