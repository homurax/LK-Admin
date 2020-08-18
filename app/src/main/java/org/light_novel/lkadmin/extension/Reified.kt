package org.light_novel.lkadmin.extension

import android.content.Context
import android.content.Intent

inline fun <reified T> startActivity(context: Context, block: Intent.() -> Unit) {
    val intent = Intent(context, T::class.java)
    intent.block()
    context.startActivity(intent)
}