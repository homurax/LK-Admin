package org.light_novel.lkadmin

import android.app.Application
import android.content.Context
import org.light_novel.lkadmin.logic.model.getConfigProperty
import java.util.*

class LKAdminApplication : Application() {

    companion object {
        lateinit var context: Context

        val PRIVATE_KEY by lazy { getConfigProperty("PRIVATE_KEY") }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}