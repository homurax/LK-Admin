package org.light_novel.lkadmin.logic.network

import okhttp3.ResponseBody
import org.light_novel.lkadmin.logic.model.*
import retrofit2.Call
import retrofit2.http.*

interface LKAdminService {

    /**
     * Login
     */
    @POST(LKAdminApi.LOGIN_URL)
    fun login(@Header("signature") signature: String, @Body loginUser: LoginUser): Call<LoginResponse>


    /**
     * Article
     */
    @GET(LKAdminApi.ARTICLE_URL)
    fun articlePage(@QueryMap searchMap: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ArticlePage>

    @PATCH(LKAdminApi.ARTICLE_URL + "/{aid}")
    fun articleMask(@Body mask: MaskArticle, @Path("aid") aid: String, @HeaderMap headers: Map<String, String>): Call<Msg>

    @PATCH(LKAdminApi.ARTICLE_URL + "/{aid}")
    fun articleTop(@Body top: TopArticle, @Path("aid") aid: String, @HeaderMap headers: Map<String, String>): Call<Msg>


    /**
     * Comment
     */
    @GET(LKAdminApi.COMMENT_URL)
    fun commentPage(@QueryMap searchMap: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<CommentPage>

    @PATCH(LKAdminApi.COMMENT_URL + "/{tid}")
    fun commentHide(@Body hideComment: HideComment, @Path("tid") tid: String, @HeaderMap headers: Map<String, String>): Call<Msg>


    /**
     * Reply
     */
    @GET(LKAdminApi.COMMENT_URL)
    fun replyPage(@QueryMap searchMap: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ReplyPage>

    @PATCH(LKAdminApi.COMMENT_URL + "/{tid}")
    fun replyHide(@Body hideReply: HideReply, @Path("tid") tid: Int, @HeaderMap headers: Map<String, String>): Call<Msg>


    /**
     * User
     */
    @GET(LKAdminApi.USER_URL)
    fun userPage(@QueryMap searchMap: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<UserPage>

    @POST(LKAdminApi.USER_URL + "/lock_all")
    fun hideAll(@Body hideUser: HideUser, @HeaderMap headers: Map<String, String>): Call<Msg>

    @PATCH(LKAdminApi.USER_URL + "/{uid}")
    fun userModify(@Body user: LKUser, @Path("uid") uid: String, @HeaderMap headers: Map<String, String>): Call<Msg>

    @PATCH(LKAdminApi.USER_URL + "/{uid}")
    fun modifyAdventurer(@Body adventurer: ModifyAdventurer, @Path("uid") uid: Int, @HeaderMap headers: Map<String, String>): Call<Msg>

    @GET(LKAdminApi.USER_URL + "/{uid}")
    fun userInfo(@Path("uid") uid: String, @HeaderMap headers: Map<String, String>): Call<LKUserInfo>

    @POST(LKAdminApi.USER_URL + "/coin_logs")
    fun coinModifyLog(@Body modifyCoinLog: ModifyCoinLog, @HeaderMap headers: Map<String, String>): Call<ModifyCoinInfo>

    @PATCH(LKAdminApi.USER_URL + "/{uid}")
    fun coinModify(@Body modifyCoin: ModifyCoin, @Path("uid") uid: Int, @HeaderMap headers: Map<String, String>): Call<Msg>


    /**
     * Message
     */
    @GET(LKAdminApi.MESSAGE_URL)
    fun messagePage(@QueryMap searchMap: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<MessagePage>

    @DELETE(LKAdminApi.MESSAGE_URL + "/{id}")
    fun deleteMessage(@Path("id") id: Int, @HeaderMap headers: Map<String, String>): Call<MsgCode>

    @POST(LKAdminApi.MESSAGE_URL)
    fun sendMessage(@Body message: SendMessage, @HeaderMap headers: Map<String, String>): Call<ResponseBody>


    /**
     * Score
     */
    @GET(LKAdminApi.SCORE_URL)
    fun scorePage(@QueryMap searchMap: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ScorePage>

    @PATCH(LKAdminApi.SCORE_URL)
    fun scoreHide(@Body hideScore: HideScore, @HeaderMap headers: Map<String, String>): Call<ResponseBody>


    /**
     * Series
     */
    @GET(LKAdminApi.SERIES_URL)
    fun seriesPage(@QueryMap searchMap: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<SeriesPage>

    @GET(LKAdminApi.SERIES_URL + "/detail/{sid}")
    fun seriesDetail(@Path("sid") sid: Int, @HeaderMap headers: Map<String, String>): Call<SeriesDetail>

    @PATCH(LKAdminApi.SERIES_URL + "/{sid}")
    fun seriesHide(@Body hideSeries: HideSeries, @Path("sid") sid: Int, @HeaderMap headers: Map<String, String>): Call<MsgCode>

    @GET(LKAdminApi.SERIES_URL + "/articles/{sid}")
    fun seriesArticle(@Path("sid") sid: Int, @HeaderMap headers: Map<String, String>): Call<List<SeriesArticle>>

    @POST(LKAdminApi.SERIES_URL + "/remove_article")
    fun seriesRemove(@Body article: SeriesArticle, @HeaderMap headers: Map<String, String>): Call<Msg>


    /**
     * recommend
     */
    @GET(LKAdminApi.RECOM_URL)
    fun recommendList(@QueryMap searchMap: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<List<Recommend>>

}