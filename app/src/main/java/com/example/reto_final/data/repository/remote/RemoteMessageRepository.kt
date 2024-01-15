package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.Message
import com.example.reto_final.utils.Resource

interface RemoteMessageRepository {
    suspend fun getMessages() : Resource<List<Message>>
    suspend fun getMessagesFromGroup(idGroup: Int) : Resource<List<Message>>
    suspend fun createMessage(message: Message) : Resource<Message>
}