package org.light_novel.lkadmin.extension

import android.widget.Toast
import org.light_novel.lkadmin.LKAdminApplication

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(LKAdminApplication.context, this, duration).show()
}

fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(LKAdminApplication.context, this, duration).show()
}