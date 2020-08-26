package org.light_novel.lkadmin.logic.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.light_novel.lkadmin.LKAdminApplication
import java.util.*

/**
 * permission
 */
open class LKPermission(val id: Int, @SerializedName("route_name") val routeName: String)

class LKPermissionItem(id: Int, routeName: String, val showName: String) :
    LKPermission(id, routeName)

private val permissionMap = mapOf(
    8 to "APP推荐管理",
    9 to "PC推荐管理",
    10 to "用户信息管理",
    12 to "主题管理",
    13 to "回帖管理",
    14 to "楼中楼管理",
    15 to "系统通知",
    16 to "发送消息",
    17 to "合集管理",
    18 to "评分管理"
)

fun getPermissionName(id: Int) = permissionMap[id] ?: ""

fun getUsername() = getAuthorize("username")

fun getPassword() = getAuthorize("password")

fun getToken() = getAuthorize("token")

private fun getAuthorize(key: String) =
    LKAdminApplication.context.getSharedPreferences("authorize", Context.MODE_PRIVATE).getString(key, "") ?: ""

fun getPermissionItems(): List<LKPermissionItem> {
    val type = object : TypeToken<List<LKPermissionItem>>() {}.type
    return Gson().fromJson(getAuthorize("permissionItems"), type)
}


/**
 * config.properties
 */
val properties by lazy { Properties().apply { load(LKAdminApplication.context.assets.open("config.properties")) } }

fun getConfigProperty(key: String) = properties.getProperty(key) ?: ""


/**
 * whitelist
 */
object WhiteInfo {

    val whiteUidList = getConfigProperty("whitelistUid").let {
        if (it.isEmpty()) {
            listOf()
        } else {
            it.split(",")
        }
    }

    val whiteNameList = getConfigProperty("whitelistUserName").let {
        if (it.isEmpty()) {
            listOf()
        } else {
            it.split(",")
        }
    }
}

fun isWhitelistUser(uid: Int) = WhiteInfo.whiteUidList.contains(uid.toString())

fun isWhitelistUser(content: String) = WhiteInfo.whiteUidList.contains(content) or WhiteInfo.whiteNameList.contains(content)

