package org.light_novel.lkadmin.logic.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


class Msg(val type: String, val msg: String)

class MsgCode(val code: Int)

// user
@Parcelize
class LKUser(
    val uid: Int,
    val nickname: String,
    val avatar: String,
    val passer: Int,
    val sign: String,
    val coin: Int,
    val gender: Int,
    val ip: String,
    @SerializedName("create_ip") val createIp: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("ban_end_date") var banEndDate: String
) : Parcelable

@Parcelize
class LKUserInfo(
    val uid: Int,
    val nickname: String,
    val avatar: String,
    val sign: String,
    val gender: Int,
    val city: String,
    val msg: String?
) : Parcelable

@Parcelize
class UserPage(val count: Int, val pageSize: Int, val msg: String?, var users: List<LKUser>) :
    Parcelable

// coin modify response
@Parcelize
class ModifyCoinInfo(
    val uid: Int,
    val coin: Int,
    val remark: String,
    val time: String,
    val source: Int,
    val balance: String,
    val params: String
) : Parcelable

// group
@Parcelize
class ArticleSeries(
    val sid: Int,
    val name: String,
    val gid: Int
) : Parcelable

@Parcelize
class ForumGroup(
    val gid: Int,
    @SerializedName("parent_gid") val parentGid: Int,
    val name: String,
    @SerializedName("cover_type") val coverType: Int,
    @SerializedName("sub_groups") val subGroups: List<ForumGroup>?
) : Parcelable

@Parcelize
class UserArticleGroup (
    val series: List<ArticleSeries>,
    val groups: List<ForumGroup>,
    @SerializedName("sub_groups") val subGroups: List<ForumGroup>
) : Parcelable

@Parcelize
class ArticleGroup(
    val aid: Int,
    val gif: Int,
    @SerializedName("group_name") val groupName: String
) : Parcelable

// article
@Parcelize
class ArticleAuthor(val uid: Int, val nickname: String, val avatar: String) : Parcelable

@Parcelize
class Article(
    val aid: Int,
    val uid: Int,
    val title: String,
    val time: String,
    @SerializedName("last_time") val lastTime: String,
    val platform: Int,
    val mask: Int,
    @SerializedName("only_passer") val onlyPasser: Int,
    val banner: String,
    val author: ArticleAuthor,
    val group: ArticleGroup
) : Parcelable

@Parcelize
class ArticlePage(
    val count: Int,
    val pageSize: Int,
    val msg: String?,
    var articles: List<Article>
) : Parcelable

// comment
@Parcelize
class CommentAuthor(val uid: Int, val nickname: String) : Parcelable

@Parcelize
class CommentArticle(val aid: Int, val title: String) : Parcelable

@Parcelize
class Comment(
    val tid: Int,
    val uid: Int,
    val pid: Int,
    val time: String,
    val status: Int,
    val content: String,
    val article: CommentArticle,
    val author: CommentAuthor
) : Parcelable

@Parcelize
class CommentPage(
    val count: Int,
    val pageSize: Int,
    val msg: String?,
    var comments: List<Comment>
) : Parcelable

// reply
@Parcelize
class Reply(
    val uid: Int,
    val tid: Int,
    val pid: Int,
    val rid: Int,
    val time: String,
    val status: Int,
    val content: String,
    val article: CommentArticle,
    val author: CommentAuthor
) : Parcelable

@Parcelize
class ReplyPage(
    val count: Int,
    val pageSize: Int,
    val msg: String?,
    @SerializedName("comments") var replies: List<Reply>
) : Parcelable

// message
@Parcelize
class Message(
    val id: Int,
    val from: Int,
    val to: Int,
    val title: String,
    val msg: String,
    val time: String,
    val status: Int
) : Parcelable

@Parcelize
class MessagePage(
    val count: Int,
    val pageSize: Int,
    val rows: List<Message>,
    val msg: String?
) : Parcelable

// score
@Parcelize
class ScoreAuthor(val uid: Int, val nickname: String, val avatar: String) : Parcelable

@Parcelize
class ScoreSeries(val sid: Int, val rate: Double, val name: String, val cover: String) : Parcelable

@Parcelize
class Score(
    val sid: Int,
    val uid: Int,
    val text: String,
    val time: String,
    val rate: Int,
    val status: Int,
    val author: ScoreAuthor,
    @SerializedName("series_info") val seriesInfo: ScoreSeries
) : Parcelable

@Parcelize
class ScorePage(
    val count: Int,
    val pageSize: Int,
    var rows: List<Score>,
    val msg: String?
) : Parcelable

// series
@Parcelize
class SeriesGroup(
    val gid: Int,
    @SerializedName("parent_gid") val parentGid: Int,
    val name: String,
    val logo: String,
    @SerializedName("cover_type") val coverType: Int,
    val status: Int,
    val order: Int
) : Parcelable

@Parcelize
class Series(
    val sid: Int,
    val name: String,
    val author: String,
    val intro: String,
    @SerializedName("last_time") val lastTime: String,
    val rate: Double,
    val rates: Int,
    val status: Int,
    val weight: Int,
    val order: Int,
    val group: SeriesGroup,
    val gid: Int,
    @SerializedName("cover_type") val coverType: Int,
    val cover: String,
    val banner: String
) : Parcelable

@Parcelize
class SeriesPage(
    val count: Int,
    val pageSize: Int,
    val rows: List<Series>,
    val msg: String?
) : Parcelable

// series detail
@Parcelize
class SeriesDetailUser(val uid: Int, val nickname: String) : Parcelable

@Parcelize
class SeriesDetailArticle(val aid: Int, val title: String) : Parcelable

@Parcelize
class SeriesDetail(
    val users: List<SeriesDetailUser>,
    val articles: List<SeriesDetailArticle>
) : Parcelable

// series article
@Parcelize
class SeriesArticle(val sid: Int, val aid: Int, val title: String, val order: Int) : Parcelable

// recommend
@Parcelize
class RecommendGroup(
    val title: String,
    val gid: Int,
    val type: Int,
    @SerializedName("min_width") val minWidth: Int,
    @SerializedName("min_height") val minHeight: Int
) : Parcelable

@Parcelize
class Recommend(
    val id: Int,
    val title: String,
    @SerializedName("action_params") val actionParams: String,
    @SerializedName("action_type") val actionType: Int,
    @SerializedName("pic_url") val picUrl: String,
    @SerializedName("group_id") val groupId: Int,
    val group: RecommendGroup
) : Parcelable
