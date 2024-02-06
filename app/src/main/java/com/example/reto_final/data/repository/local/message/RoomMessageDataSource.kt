package com.example.reto_final.data.repository.local.message

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.repository.local.CommonMessageRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import java.util.Date

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

    override suspend fun updateMessage(message: Message): Resource<Message> {
        val dbMessage = messageDao.updateMessage(message.id ,message.idServer ,message.saved)
        message.id = dbMessage
        return Resource.success(message)
    }

    override suspend fun getLastMessage(): Resource<Message?> {
        val dbMessage = messageDao.getLastMessage()
        return if (dbMessage != null) {
            Resource.success(dbMessage.toMessage())
        } else {
            Resource.success(null)
        }
    }

    override suspend fun getPendingMessages(): Resource<List<Message>> {
        val listPendingMessage = messageDao.getPendingMessages().map { it.toMessage() }
        return Resource.success(listPendingMessage)
    }

}

fun DbMessage.toMessage() = Message(id, idServer, text, sentDate.time, saveDate?.time, groupId, userId, type)
fun Message.toDbMessage() = DbMessage(id, idServer, text, Date(sent), saved?.let { Date(it) }, type, chatId, userId)

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE groupId = :groupId ORDER BY id ASC")
    suspend fun getMessagesFromGroup(groupId: Int): List<DbMessage>

    @Query("SELECT * FROM messages WHERE idServer = (SELECT MAX(idServer) FROM messages)")
    suspend fun getLastMessage(): DbMessage?

    @Query("SELECT * FROM messages WHERE saved IS NULL")
    suspend fun getPendingMessages(): List<DbMessage>

    @Insert
    suspend fun createMessage(message: DbMessage) : Long

    @Query("UPDATE messages SET saved = :saved, idServer= :idServer WHERE id = :messageId")
    suspend fun updateMessage(messageId: Int?, idServer: Int?, saved: Long?) : Int
}