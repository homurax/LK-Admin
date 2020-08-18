package org.light_novel.lkadmin.extension

import com.google.gson.Gson

fun <T> T.sign() = Gson().toJson(this).sign()