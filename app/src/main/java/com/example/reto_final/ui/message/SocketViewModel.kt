package com.example.reto_final.ui.message

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Message
import com.example.reto_final.data.socket.SocketEvents
import com.example.reto_final.data.socket.SocketMessageReq
import com.example.reto_final.data.socket.SocketMessageRes
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Date

class SocketViewModel() : ViewModel() {

    private val _connected = MutableLiveData<Resource<Boolean>>()
    val connected : LiveData<Resource<Boolean>> get() = _connected

    private val _message = MutableLiveData<Resource<List<Message>>>()
    val message : LiveData<Resource<List<Message>>> get() = _message

    init {
        /*
        if (!MyApp.userPreferences.mSocket.connected()) {
            Log.d("va", "initif")
        } else {
            MyApp.userPreferences.mSocket.disconnect()
        }

         */
    }

    fun onSendMessage(groupId: Int, message: String, sent: Date) {
        Log.d("Prueba", "onSendMessage $message")
        // la sala esta hardcodeada..
        val socketMessage = SocketMessageReq(groupId, message, sent)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        MyApp.userPreferences.mSocket.emit(SocketEvents.ON_SEND_MESSAGE.value, jsonObject)
    }

    fun deleteSocketList() {
        _message.value?.data ?: emptyList()
    }


}

class SocketViewModelFactory(
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return SocketViewModel() as T
    }

}

