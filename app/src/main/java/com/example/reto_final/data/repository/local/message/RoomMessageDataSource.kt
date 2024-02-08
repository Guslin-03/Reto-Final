package com.example.reto_final.data.repository.local.message

import androidx.room.Dao
import androidx.room.Query
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.repository.local.CommonMessageRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import java.util.Date

class RoomMessageDataSource : CommonMessageRepository {

    private val messageDao: MessageDao = MyApp.db.messageDao()

    override suspend fun getMessagesFromGroup(idGroup: Int): Resource<List<Message>> {
        val allMessages = messageDao.getMessagesFromGroup(idGroup).map { it.toMessage() }
        val response = getUserNameMessage(allMessages)
        return Resource.success(response)
    }

    override suspend fun getUserNameMessage(allMessages : List<Message>): List<Message> {
        for (message in allMessages) {
            if (message.id != null) {
                val response = messageDao.getUserNameForMessage(message.userId, message.id!!)
                message.userName = response
            }
        }
        return allMessages
    }

    override suspend fun createMessage(message: Message): Resource<Message> {
        val dbMessage = message.toDbMessage()
        val dbMessageId = messageDao.createMessage(dbMessage.idServer, dbMessage.text, dbMessage.sentDate, dbMessage.saveDate, dbMessage.type, dbMessage.groupId, dbMessage.userId)
        message.id = dbMessageId.toInt()
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

fun DbMessage.toMessage() = Message(id, idServer, text, sentDate.time, saveDate?.time, groupId, userId, type, null)
fun Message.toDbMessage() = DbMessage(id, idServer, text, Date(sent), saved?.let { Date(it) }, type, chatId, userId)

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE groupId = :groupId ORDER BY id ASC")
    suspend fun getMessagesFromGroup(groupId: Int): List<DbMessage>

    @Query("SELECT users.name FROM users JOIN messages ON users.id = messages.userId WHERE messages.userId = :userId AND messages.id = :messageId")
    suspend fun getUserNameForMessage(userId: Int, messageId: Int): String

    @Query("SELECT * FROM messages WHERE idServer = (SELECT MAX(idServer) FROM messages)")
    suspend fun getLastMessage(): DbMessage?

    @Query("SELECT * FROM messages WHERE saved IS NULL")
    suspend fun getPendingMessages(): List<DbMessage>

    @Query("INSERT INTO messages (idServer, text, sent, saved, type, groupId, userId) " +
            "VALUES (:idServer, :text, :sentDate, :savedDate, :type, :chatId, :userId)")
    suspend fun createMessage(idServer: Int?, text: String, sentDate: Date, savedDate: Date?, type: String, chatId: Int, userId: Int) : Long

    @Query("UPDATE messages SET saved = :saved, idServer= :idServer WHERE id = :messageId")
    suspend fun updateMessage(messageId: Int?, idServer: Int?, saved: Long?) : Int
}