package org.light_novel.lkadmin.logic.model


class LoginUser(val username: String, val password: String)

class LoginResponse(
    val id: Int,
    val username: String,
    val token: String,
    val msg: String,
    val permissions: List<LKPermission>
)

