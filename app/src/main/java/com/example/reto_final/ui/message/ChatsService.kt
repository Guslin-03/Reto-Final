package com.example.reto_final.ui.message

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.lifecycle.MutableLiveData
import com.example.reto_final.R
import com.example.reto_final.data.model.Role
import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.group.GroupResponse
import com.example.reto_final.data.model.group.PendingGroupRequest
import com.example.reto_final.data.model.userGroup.UserGroup
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.message.MessageGetResponse
import com.example.reto_final.data.model.message.PendingMessages
import com.example.reto_final.data.model.user.User
import com.example.reto_final.data.model.user.UserRequest
import com.example.reto_final.data.model.userGroup.UserChatInfo
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.message.MessageEnumClass
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.repository.local.role.RoomRoleDataSource
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.data.repository.local.user.UserRoleType
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteMessageDataSource
import com.example.reto_final.data.repository.remote.RemoteRoleDataSource
import com.example.reto_final.data.repository.remote.RemoteUserDataSource
import com.example.reto_final.data.socket.SocketEvents
import com.example.reto_final.data.socket.SocketMessageRes
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.MyApp.Companion.API_SERVER
import com.example.reto_final.utils.MyApp.Companion.API_SOCKET_PORT
import com.example.reto_final.utils.MyApp.Companion.AUTHORIZATION_HEADER
import com.example.reto_final.utils.MyApp.Companion.BEARER
import com.example.reto_final.utils.Resource
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class ChatsService : Service() {

    private val channelId = "download_channel"
    private val notificationId = 1
    private lateinit var serviceScope: CoroutineScope

    private val localGroupRepository = RoomGroupDataSource()
    private val remoteGroupRepository = RemoteGroupDataSource()

    private val localMessageRepository = RoomMessageDataSource()
    private val remoteMessageRepository = RemoteMessageDataSource()

    private val localUserRepository = RoomUserDataSource()
    private val remoteUserRepository = RemoteUserDataSource()

    private val localRoleRepository = RoomRoleDataSource()
    private val remoteRoleRepository = RemoteRoleDataSource()
    private val fileManager = FileManager(this)
    private val _savedMessage = MutableLiveData<Resource<Message>>()
    private val _savedGroup = MutableLiveData<Resource<Group>>()


    private val _allMessage = MutableLiveData<Resource<List<MessageGetResponse>>>()

    private val _allGroup = MutableLiveData<Resource<List<Group>>>()

    private val _allUser = MutableLiveData<Resource<List<UserRequest>>>()

    private val _allRole = MutableLiveData<Resource<List<Role>>>()

    private val _lastUser = MutableLiveData<Resource<User?>>()

    private val _lastGroup = MutableLiveData<Resource<Group?>>()

    private val _lastMessage = MutableLiveData<Resource<Message?>>()

    private val _allPendingMessages = MutableLiveData<Resource<List<MessageGetResponse>>>()

    private val _pendingMessage = MutableLiveData<Resource<List<Message>>>()

    private val _allPendingGroups = MutableLiveData<Resource<List<UserChatInfo>>>()

    private val _pendingGroup = MutableLiveData<Resource<List<UserChatInfo?>>>()

    private val userChatInfo = mutableListOf<UserChatInfo>()

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Main)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("services", "onStartCommand")
        val contentText = "Iniciando socket"
        startForeground(notificationId, createNotification(contentText))
        startSocket()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Descargas Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(contentText: String): Notification {
        val context = this
        val intent = Intent(context, MessageActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Chat en directo")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.resume_logo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        Log.i("services", "onDestroy")
        if (MyApp.userPreferences.mSocket.connected()) {
            MyApp.userPreferences.mSocket.disconnect()
        }
        super.onDestroy()
    }

    private fun startSocket() {
        val socketOptions = createSocketOptions()
        MyApp.userPreferences.mSocket = IO.socket("${API_SERVER}:${API_SOCKET_PORT}", socketOptions)
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_JOIN_RECEIVED.value, onChatJoin())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_LEAVE_RECEIVED.value, onChatLeft())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_ADD_RECEIVED.value, onChatAdded())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_THROW_OUT_RECEIVED.value, onChatThrowOut())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_RECEIVED.value, onChatReceive())
        serviceScope.launch {
            connect()
        }
    }

    private suspend fun connect() {
        withContext(Dispatchers.IO) {
            MyApp.userPreferences.mSocket.connect()
        }
    }

    private fun onConnect(): Emitter.Listener {
        return Emitter.Listener {
            updateNotification("conectado")
            toInit()
        }
    }

    private fun onDisconnect(): Emitter.Listener {
        return Emitter.Listener {
            updateNotification("disConnect")
        }
    }

    private fun onNewMessage(): Emitter.Listener {
        return Emitter.Listener {
            Log.d("Prueba", "Lo recibio")
            val response = onJSONtoAnyClass(it[0], SocketMessageRes::class.java) as SocketMessageRes
            val newMessage = response.toMessage()
            val updatedMessage = onNewMessageOwner(newMessage)
            Log.d("Hola", "$updatedMessage")
            if (_savedMessage.value?.status != Resource.Status.ERROR) {
                EventBus.getDefault().post(newMessage)
            }
            updateNotification(response.message)
        }
    }

    private fun onNewMessageOwner(incomingMessage : Message) {
        serviceScope.launch {
            val loginUser = MyApp.userPreferences.getUser()
            if (loginUser != null) {
                val updateMessage = onIsFile(incomingMessage)
                _savedMessage.value = if (incomingMessage.userId == loginUser.id) {
                    localMessageRepository.updateMessage(updateMessage)
                } else {
                    Log.d("prueba1", "$updateMessage")
                    localMessageRepository.createMessage(updateMessage)
                }
            }
        }
    }

    private fun onIsFile(incomingMessage : Message) : Message {
        return if (incomingMessage.type == MessageEnumClass.FILE.toString()){
            val location = fileManager.saveBase64ToFile(incomingMessage.text)
            Message(
                incomingMessage.id,
                incomingMessage.idServer,
                location, incomingMessage.sent,
                incomingMessage.saved,
                incomingMessage.chatId,
                incomingMessage.userId,
                incomingMessage.type,
                null)
        }else{
            incomingMessage
        }
    }

    private fun onChatJoin(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], UserGroup::class.java) as UserGroup
            updateNotification("${response.userName} se ha unido al grupo.")
            serviceScope.launch {
                addUserToGroup(response)
            }
        }
    }

    private fun onChatAdded(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], UserGroup::class.java) as UserGroup
            updateNotification("${response.adminName} ha añadido a ${response.userName}.")
            serviceScope.launch {
                addUserToGroup(response)
            }
        }
    }

    private suspend fun addUserToGroup(userGroupRes: UserGroup) {
        localGroupRepository.addUserToGroup(userGroupRes.toUserChatInfo())
    }

    private fun onChatLeft(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], UserGroup::class.java) as UserGroup
            updateNotification("${response.userName} ha salido del grupo.")
            serviceScope.launch {
                leaveGroup(response)
            }
        }
    }

    private fun onChatThrowOut(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], UserGroup::class.java) as UserGroup
            Log.d("p1", "$response")
            updateNotification("${response.adminName} ha expulsado a ${response.userName}.")
            serviceScope.launch {
                chatThrowOut(response)
            }
        }
    }

    private suspend fun chatThrowOut(userGroupRes: UserGroup) {
        localGroupRepository.chatThrowOutLocal(userGroupRes.toUserChatInfo())
    }

    private suspend fun leaveGroup(userGroupRes: UserGroup) {
        localGroupRepository.leaveGroup(userGroupRes.roomId, userGroupRes.userId)
    }

    private fun onChatReceive(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], Group::class.java) as Group
            updateNotification("El grupo ${response.name} ha sido creado.")
            chatReceive(response)
            if (_savedGroup.value?.status != Resource.Status.ERROR) {
                EventBus.getDefault().post(response)
            }
        }
    }

    private fun chatReceive(newGroup: Group) {
        serviceScope.launch {
            _savedGroup.value = localGroupRepository.createGroup(newGroup)
        }
    }

    private fun updateNotification(contentText: String) {
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            val notification = createNotification(contentText)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun createSocketOptions(): IO.Options {
        val options = IO.Options()
        val headers = mutableMapOf<String, MutableList<String>>()
        headers[AUTHORIZATION_HEADER] = mutableListOf(BEARER + MyApp.userPreferences.fetchHibernateToken())

        options.extraHeaders = headers

        return options
    }

    private fun onJSONtoAnyClass(data : Any, convertClass: Class<*>): Any? {
        return try {
            val jsonObject = data as JSONObject
            val jsonObjectString = jsonObject.toString()
            Gson().fromJson(jsonObjectString, convertClass)
        } catch (ex: Exception) {
//            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    private fun toInit() {

        serviceScope.launch {
            getAllLastData()
//            Log.d("p1", "${_lastGroup.value?.status}")
//            Log.d("p1", "${_lastMessage.value?.status}")
//            Log.d("p1", "${_lastUser.value?.status}")
            Log.d("p1", "${_pendingGroup.value?.status}")
            Log.d("p1", "${_pendingGroup.value?.data}")
            if (_lastUser.value?.status == Resource.Status.SUCCESS
                && _lastGroup.value?.status == Resource.Status.SUCCESS
                && _lastMessage.value?.status == Resource.Status.SUCCESS
                && _pendingMessage.value?.status == Resource.Status.SUCCESS
                && _pendingGroup.value?.status == Resource.Status.SUCCESS
            ) {
//              Log.d("p1", "GetAllLastData")
                getAllData()
//                Log.d("p1", "${_allMessage.value?.data}")
//                Log.d("p1", "${_allUser.value?.data}")
//                Log.d("p1", "${_allGroup.value?.data}")
                Log.d("p1", "${_allPendingGroups.value?.status}")
                Log.d("p1", "${_allPendingGroups.value?.data}")
                Log.d("p1", "${_allPendingGroups.value?.message}")
                if (_allMessage.value?.status == Resource.Status.SUCCESS
                    && _allUser.value?.status == Resource.Status.SUCCESS
                    && _allGroup.value?.status == Resource.Status.SUCCESS
                    && _allPendingMessages.value?.status == Resource.Status.SUCCESS
                    && _allPendingGroups.value?.status == Resource.Status.SUCCESS
                ) {
                    setAllData()
//                    Log.d("p1", "getAllData")
                    if (_allGroup.value!!.data?.isNotEmpty() == true) {
                        EventBus.getDefault().post(_allGroup.value!!.data)
                    }
                    if (_allMessage.value!!.data?.isNotEmpty() == true) {
                        EventBus.getDefault().post(_allMessage.value!!.data)
                    }
                    if (_allPendingMessages.value!!.data?.isNotEmpty() == true) {
                        EventBus.getDefault().post(_allPendingMessages.value!!.data)
                    }
                    if (_allPendingGroups.value!!.data?.isNotEmpty() == true) {
                        EventBus.getDefault().post(_allPendingGroups.value!!.data)
                    }

                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // RECOGIDA DE LOS ULTIMOS DATOS DE ROOM
    private suspend fun getAllLastData() {
        _lastUser.value = getLastUser()
        _lastGroup.value = getLastGroup()
        _lastMessage.value = getLastMessage()
        _pendingMessage.value = getPendingMessages()
        _pendingGroup.value = getPendingGroups()
    }

    private suspend fun getLastMessage(): Resource<Message?> {
        return withContext(Dispatchers.IO) {
            localMessageRepository.getLastMessage()
        }
    }

    private suspend fun getPendingGroups(): Resource<List<UserChatInfo?>> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.getPendingGroups()
        }
    }

    private suspend fun getPendingMessages(): Resource<List<Message>> {
        return withContext(Dispatchers.IO) {
            localMessageRepository.getPendingMessages()
        }
    }

    private suspend fun getLastGroup(): Resource<Group?> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.getLastGroup()
        }
    }

    private suspend fun getLastUser(): Resource<User?> {
        return withContext(Dispatchers.IO) {
            localUserRepository.getLastUser()
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // LLAMADAS A BBDD REMOTA PARA POBLAR ROOM
    private suspend fun getAllData() {
//        _allRole.value = getAllRoles()
        _allUser.value = getAllUsers(_lastUser.value?.data)
        _allGroup.value = getAllGroups(_lastGroup.value?.data)
        _allMessage.value = getAllMessages(_lastMessage.value?.data)
        val pendingMessage = _pendingMessage.value?.data
        val pendingMessageRequest = pendingMessage?.map { it.toPendingMessageRequest()}
        _allPendingMessages.value = setPendingMessages(pendingMessageRequest)
        _allPendingGroups.value = setPendingGroups(_pendingGroup.value?.data)
    }

    private suspend fun getAllMessages(message: Message?): Resource<List<MessageGetResponse>> {
        return withContext(Dispatchers.IO) {
            if (message != null) {
                remoteMessageRepository.getMessages(message.id)
            } else {
                remoteMessageRepository.getMessages(0)
            }
        }
    }

    private suspend fun setPendingMessages(listPendingMessages: List<PendingMessages?>?) : Resource<List<MessageGetResponse>> {
        return withContext(Dispatchers.IO) {
            if (listPendingMessages != null) {
                remoteMessageRepository.setPendingMessages(listPendingMessages)
            } else {
                Resource.success()
            }
        }
    }

    private suspend fun setPendingGroups(listPendingGroups: List<UserChatInfo?>?) : Resource<List<UserChatInfo>> {
        return withContext(Dispatchers.IO) {
            if (listPendingGroups != null) {
                remoteGroupRepository.setPendingGroups(listPendingGroups)
            } else {
                Resource.success()
            }
        }
    }

    private suspend fun getAllUsers(user: User?): Resource<List<UserRequest>> {
        return withContext(Dispatchers.IO) {
            if (user != null) {
                remoteUserRepository.findUsers(user.id)
            } else {
                remoteUserRepository.findUsers(0)
            }
        }
    }

    private suspend fun getAllGroups(group: Group?): Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            if (group != null) {
                remoteGroupRepository.getGroups(group.id)
            } else {
                remoteGroupRepository.getGroups(0)
            }
        }
    }

    private suspend fun getAllRoles(): Resource<List<Role>> {
        return withContext(Dispatchers.IO) {
            remoteRoleRepository.getRoles()
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // INSERTS EN ROOM DE LA INFORMACION RECOGIDA EN REMOTO
    private suspend fun setAllData() {
        setAllRoles()
        setAllUsers()
        setAllGroups()
        setAllUsersToGroups()
        setAllMessages()
        updateAllPendingMessages()
        updateAllPendingGroups()
    }

    private suspend fun setAllRoles() {
        return withContext(Dispatchers.IO) {
//            val allRole = _allRole.value?.data
//            if (allRole != null) {
            val roles = listOf(
                Role(2, UserRoleType.Profesor.toString()),
                Role(3, UserRoleType.Alumno.toString())
            )
            for (role in roles) {
                localRoleRepository.createRole(role)
            }
//            }
        }
    }

    private suspend fun setAllUsers() {
        return withContext(Dispatchers.IO) {
            val allUser = _allUser.value?.data
            if (allUser != null) {
                for (userRequest in allUser) {
                    val user = User(userRequest.id, userRequest.name, userRequest.surname, userRequest.email, userRequest.phoneNumber1, userRequest.roleId)
                    localUserRepository.createUser(user)
                    userChatInfo.addAll(userRequest.userChatInfo)
                }
            }
        }
    }

    private suspend fun setAllGroups() {
        return withContext(Dispatchers.IO) {
            val allGroup = _allGroup.value?.data
            if (allGroup != null) {
                for (group in allGroup) {
                    localGroupRepository.createGroup(group)
                }
            }
        }
    }

    private suspend fun setAllUsersToGroups() {
        return withContext(Dispatchers.IO) {
            for (userChatInfo in userChatInfo) {
                localGroupRepository.addUserToGroup(userChatInfo)
            }
        }
    }

    private suspend fun setAllMessages() {
        return withContext(Dispatchers.IO) {
            val allMessage = _allMessage.value?.data
            if (allMessage != null) {
                for (messageResponse in allMessage) {
                    onNewMessageOwner(messageResponse.toMessage())
                }
            }
        }
    }

    private suspend fun updateAllPendingMessages() {
        return withContext(Dispatchers.IO) {
            val allPendingMessagesResponse = _allPendingMessages.value?.data
            if (allPendingMessagesResponse != null) {
                for (pendingMessageResponse in allPendingMessagesResponse) {
                    onNewMessageOwner(pendingMessageResponse.toMessage())
                }
            }
        }
    }

    private suspend fun updateAllPendingGroups() {
        return withContext(Dispatchers.IO) {
            val allPendingUserChatInfoResponse = _allPendingGroups.value?.data
            if (allPendingUserChatInfoResponse != null) {
                for (pendingUserChatInfoResponse in allPendingUserChatInfoResponse) {
                    localGroupRepository.updateUserChatInfo(pendingUserChatInfoResponse)
                }
            }
        }
    }

    private fun Message.toPendingMessageRequest() =
        id?.let { PendingMessages(
            chatId,
            userId,
            it,
            text,
            sent,
            type) }

    private fun MessageGetResponse.toMessage() =
        Message(
            id,
            text,
            sent,
            saved,
            type,
            chatId,
            userId)

    private fun UserGroup.toUserChatInfo() = UserChatInfo(userId, roomId, joined, deleted)

    private fun SocketMessageRes.toMessage() = Message(localId, messageServerId, message, sent, saved, room, authorId, type, null)

}