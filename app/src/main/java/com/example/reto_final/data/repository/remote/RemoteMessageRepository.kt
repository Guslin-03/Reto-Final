package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.message.MessageResponse
import com.example.reto_final.data.model.message.PendingMessageRequest
import com.example.reto_final.data.socket.SocketMessageReq
import com.example.reto_final.utils.Resource

interface RemoteMessageRepository {
    suspend fun getMessages(messageId: Int?): Resource<List<MessageResponse>>
    suspend fun getMessagesFromGroup(idGroup: Int) : Resource<List<Message>>
    suspend fun createMessage(message: Message) : Resource<Message>
    suspend fun setPendingMessages(listPendingMessages: List<PendingMessageRequest>) : Resource<List<MessageResponse>>
}