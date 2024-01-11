package com.example.reto_final.data.repository.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reto_final.data.repository.local.group.DbGroup
import com.example.reto_final.data.repository.local.group.GroupType
import com.example.reto_final.data.repository.local.message.DbMessage
import com.example.reto_final.utils.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MyAppRoomDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Código para crear datos de prueba al crear la base de datos
        scope.launch {
            populateDatabase()
        }

        MyApp.db.close()
        MyApp.context.deleteDatabase("chat-db")

    }

    private suspend fun populateDatabase() {
        // Llama a los métodos de DAO para insertar datos de prueba
        val groupDao = MyApp.db.groupDao()
        groupDao.createGroup(DbGroup(1,"Profesores", GroupType.PRIVATE))
        groupDao.createGroup(DbGroup(2,"Alumnos", GroupType.PRIVATE))

        val messageDao = MyApp.db.messageDao()
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 2))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 2))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 2))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 2))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 2))

    }
}