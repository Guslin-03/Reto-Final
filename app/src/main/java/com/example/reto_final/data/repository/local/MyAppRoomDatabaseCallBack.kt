package com.example.reto_final.data.repository.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reto_final.data.repository.local.role.DbRole
import com.example.reto_final.data.repository.remote.PopulateDataBase
import com.example.reto_final.utils.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MyAppRoomDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

    private val populateDataBase = PopulateDataBase()

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Código para crear datos de prueba al crear la base de datos
        scope.launch {
            populateDatabase()
        }

    }

    private suspend fun populateDatabase() {

//        val roleDao = MyApp.db.roleDao()
//        roleDao.createRole(DbRole(1, "PROFESOR"))
//        roleDao.createRole(DbRole(2, "ALUMNO"))
//
//        val userDao = MyApp.db.userDao()
//        userDao.createUser(DbUser(34,"Margarita", "Lazaro", "margaritalazaro@elorrieta.com", 853389841,1))
//        userDao.createUser(DbUser(58,"Ander", "Galvan", "andergalvan@elorrieta.com", 515897142,2))
//
//
//        val groupDao = MyApp.db.groupDao()
//        groupDao.createGroup(DbGroup(2,"Grupo2", ChatEnumType.PUBLIC.name, 58))
//        groupDao.createGroup(DbGroup(3,"David", ChatEnumType.PUBLIC.name, 34))
//
//        groupDao.addUserToGroup(DbUserGroup(2,34))
//        groupDao.addUserToGroup(DbUserGroup(2,58))
//        groupDao.addUserToGroup(DbUserGroup(3,34))
//
//        val messageDao = MyApp.db.messageDao()

//        val allUsers = populateDataBase.getAllUsers().data?.toList()
//        if (allUsers != null) {
//            for (user in allUsers) {
//                userDao.createUser(user.toDbUser())
//            }
//        }
//        Log.d("Prueba", "Prueba")
//        val allGroups = populateDataBase.getGroups().data?.toList()
//        Log.d("Prueba", ""+allGroups)
//        if (allGroups != null) {
//            for (group in allGroups) {
//                groupDao.createGroup(group.toDbGroup())
//                if (group.id != null) {
//                    val allMessages = populateDataBase.getMessageByChatId(group.id!!).data?.toList()
//                    if (allMessages != null) {
//                        for (message in allMessages) {
//                            messageDao.createMessage(message.toDbMessage())
//                        }
//                    }
//                }
//            }
//        }

    }

}