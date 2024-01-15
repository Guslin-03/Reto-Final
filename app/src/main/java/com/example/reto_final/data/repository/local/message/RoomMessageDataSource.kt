package com.example.reto_final.data.repository.local.message

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reto_final.data.model.Message
import com.example.reto_final.data.repository.local.CommonMessageRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class RoomMessageDataSource : CommonMessageRepository {

    private val messageDao: MessageDao = MyApp.db.messageDao()

    override suspend fun getMessagesFromGroup(idGroup: Int): Resource<List<Message>> {
        val response = messageDao.getMessagesFromGroup(idGroup).map { it.toMessage() }
        return Resource.success(response)
    }

    override suspend fun createMessage(message: Message): Resource<Message> {
        val dbMessage = messageDao.createMessage(message.toDbMessage())
        message.id = dbMessage.toInt()
        return Resource.success(message)
    }

}

fun DbMessage.toMessage() = Message(id, text, groupId, userId)
fun Message.toDbMessage() = DbMessage(id, text, groupId, userId)

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE groupId = :groupId ORDER BY id ASC")
    suspend fun getMessagesFromGroup(groupId: Int): List<DbMessage>
    @Insert
    suspend fun createMessage(message: DbMessage) : Long
}