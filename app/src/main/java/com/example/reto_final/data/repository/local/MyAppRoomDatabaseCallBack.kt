package com.example.reto_final.data.repository.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reto_final.data.repository.local.group.DbGroup
import com.example.reto_final.data.repository.local.group.ChatEnumType
import com.example.reto_final.data.repository.local.group.DbUserGroup
import com.example.reto_final.data.repository.local.message.DbMessage
import com.example.reto_final.data.repository.local.role.DbRole
import com.example.reto_final.data.repository.local.user.DbUser
import com.example.reto_final.utils.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

class MyAppRoomDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Código para crear datos de prueba al crear la base de datos
        scope.launch {
            populateDatabase()
        }

    }

    private suspend fun populateDatabase() {

        val roleDao = MyApp.db.roleDao()
        roleDao.createRole(DbRole(null, "PROFESOR"))
        roleDao.createRole(DbRole(null, "ALUMNO"))

        val userDao = MyApp.db.userDao()
        userDao.createUser(DbUser(null,"David", "Comeron", "davidcomeron@elorrieta.com", 601269008,1))
        userDao.createUser(DbUser(null,"Joana", "Barber", "joanabarber@elorrieta.com", 601269009,2))
        userDao.createUser(DbUser(null,"Robson", "Garcia", "robsongarcia@elorrieta.com", 601269010,2))

        val groupDao = MyApp.db.groupDao()
        groupDao.createGroup(DbGroup(1,"Profesores", ChatEnumType.PRIVATE.name, 1))
        groupDao.createGroup(DbGroup(2,"Alumnos", ChatEnumType.PRIVATE.name, 2))
        groupDao.createGroup(DbGroup(3,"General", ChatEnumType.PUBLIC.name, 3))

        groupDao.addUserToGroup(DbUserGroup(1,1))
        groupDao.addUserToGroup(DbUserGroup(2,2))
        groupDao.addUserToGroup(DbUserGroup(3,3))

        val messageDao = MyApp.db.messageDao()
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 1, 1, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 1", 2, 2, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 2", 2, 2, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 3", 2, 2, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 4", 2, 2, Date()))
        messageDao.createMessage(DbMessage(null,"Mensaje 5", 2, 2, Date()))

    }
}