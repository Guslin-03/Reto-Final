package com.example.reto_final.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.AuthRequest
import com.example.reto_final.data.ChangePasswordRequest
import com.example.reto_final.data.User
import com.example.reto_final.data.repository.CommonUserRepository
import com.example.reto_final.data.repository.ProfileRequest
import com.example.reto_final.data.repository.RegisterRequest
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: CommonUserRepository) : ViewModel() {

    private val _user = MutableLiveData<Resource<User>>()
    val user : LiveData<Resource<User>> get() = _user
    private val _updatePassword = MutableLiveData<Resource<Void>>()
    val updatePassword : LiveData<Resource<Void>> get() = _updatePassword

    private val _logOut = MutableLiveData<Resource<Void>>()
    val logOut : LiveData<Resource<Void>> get() = _logOut

    private val _register = MutableLiveData<Resource<Void>>()
    val register : LiveData<Resource<Void>> get() = _register

    private val _updateProfile = MutableLiveData<Resource<Void>>()
    val updateProfile : LiveData<Resource<Void>> get() = _updateProfile


    private suspend fun logIn(email:String, password:String) : Resource<User> {
        return withContext(Dispatchers.IO) {
            val user = AuthRequest(email, password, android.os.Build.MODEL)
            userRepository.login(user)
        }
    }
    fun onLogIn(email:String, password:String) {
        viewModelScope.launch {
            _user.value = logIn(email,password)
        }
    }

    fun onLogOut() {
        viewModelScope.launch {
            _logOut.value = logOut()
        }
    }

    private suspend fun logOut() : Resource<Void> {
        return withContext(Dispatchers.IO) {
            userRepository.logout()
        }
    }

    fun onChangePassword(email: String, oldPassword: String, password: String) {
        viewModelScope.launch {
            val changePasswordRequest = ChangePasswordRequest(email, oldPassword, password)
            _updatePassword.value = changePassword(changePasswordRequest)
        }
    }

    private suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            userRepository.changePassword(changePasswordRequest)
        }
    }

    fun onRegister(DNI: String, name: String, surname: String, phoneNumber1: Int, phoneNumber2: Int, address: String,
                   photo: String, email: String, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            val registerRequest = RegisterRequest(DNI, name, surname, phoneNumber1, phoneNumber2,
                address, photo, email, oldPassword, newPassword)
            _register.value = register(registerRequest)
        }
    }

    private suspend fun register(registerRequest: RegisterRequest) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            userRepository.register(registerRequest)
        }
    }

    fun onUpdateProfile(DNI: String, name: String, surname: String, phoneNumber1: Int, phoneNumber2: Int, address: String,
                   photo: String, email: String) {
        viewModelScope.launch {
            val profileRequest = ProfileRequest(DNI, name, surname, phoneNumber1, phoneNumber2,
                address, photo, email)
            _updateProfile.value = updateProfile(profileRequest)
        }
    }

    private suspend fun updateProfile(profileRequest: ProfileRequest) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            userRepository.updateProfile(profileRequest)
        }
    }

}
class UserViewModelFactory(
    private val userRepository: CommonUserRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return UserViewModel(userRepository) as T
    }

}