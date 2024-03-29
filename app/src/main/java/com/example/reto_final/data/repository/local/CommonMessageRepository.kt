package com.example.reto_final.data.repository.local

import com.example.reto_final.data.model.message.Message
import com.example.reto_final.utils.Resource

interface CommonMessageRepository {
    suspend fun getMessagesFromGroup(idGroup: Int) : Resource<List<Message>>
    suspend fun getUserNameMessage(allMessages : List<Message>): List<Message>
    suspend fun createMessage(message: Message) : Resource<Message>
    suspend fun updateMessage(message: Message) : Resource<Message>
    suspend fun getLastMessage() : Resource<Message?>
    suspend fun getPendingMessages() : Resource<List<Message>>
}