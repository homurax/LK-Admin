package org.light_novel.lkadmin.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.light_novel.lkadmin.extension.sign
import org.light_novel.lkadmin.logic.Repository
import org.light_novel.lkadmin.logic.model.SendMessage
import org.light_novel.lkadmin.logic.model.getToken

class SendMsgViewModel : ViewModel() {

    private val sendLiveData = MutableLiveData<SendMessage>()

    val uidList = ArrayList<Int?>()

    val sendResult = Transformations.switchMap(sendLiveData) {
        val headers = mapOf("token" to getToken(), "signature" to it.sign())
        Repository.sendMessage(it, headers)
    }

    fun send(message: SendMessage) {
        sendLiveData.value = message
    }
}