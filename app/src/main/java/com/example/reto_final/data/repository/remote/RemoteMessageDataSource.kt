package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.message.PendingMessages

class RemoteMessageDataSource : BaseDataSource(), RemoteMessageRepository {
    override suspend fun getMessages(messageId: Int?) = getResult{
        RetrofitClient.apiInterface.getMessages(messageId)
    }

    override suspend fun getMessagesFromGroup(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.getMessageByChatId(idGroup)
    }

    override suspend fun createMessage(message: Message) = getResult {
       RetrofitClient.apiInterface.createMessage(message)
    }

    override suspend fun setPendingMessages(listPendingMessages: List<PendingMessages?>) = getResult {
        RetrofitClient.apiInterface.setPendingMessages(listPendingMessages)
    }

}