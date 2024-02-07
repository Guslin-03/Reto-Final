package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.message.MessageGetResponse
import com.example.reto_final.data.model.message.PendingMessages
import com.example.reto_final.utils.Resource

interface RemoteMessageRepository {
    suspend fun getMessages(messageId: Int?): Resource<List<MessageGetResponse>>
    suspend fun getMessagesFromGroup(idGroup: Int) : Resource<List<Message>>
    suspend fun createMessage(message: Message) : Resource<Message>
    suspend fun setPendingMessages(listPendingMessages: List<PendingMessages?>) : Resource<List<MessageGetResponse>>
}