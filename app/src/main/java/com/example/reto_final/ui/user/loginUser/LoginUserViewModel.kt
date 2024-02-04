package com.example.reto_final.ui.user.loginUser

import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.user.AuthRequest
import com.example.reto_final.data.model.user.ChangePasswordRequest
import com.example.reto_final.data.model.user.LoginUser
import com.example.reto_final.data.repository.CommonLoginUserRepository
import com.example.reto_final.data.model.ProfileRequest
import com.example.reto_final.data.model.RegisterRequest
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginUserViewModel(
    private val userRepository: CommonLoginUserRepository,
    private val context: Context
) : ViewModel() {

    private val _login_user = MutableLiveData<Resource<LoginUser>>()
    val loginUser : LiveData<Resource<LoginUser>> get() = _login_user
    private val _updatePassword = MutableLiveData<Resource<Void>>()
    val updatePassword : LiveData<Resource<Void>> get() = _updatePassword

    private val _logOut = MutableLiveData<Resource<Void>>()
    val logOut : LiveData<Resource<Void>> get() = _logOut

    private val _register = MutableLiveData<Resource<Void>>()
    val register : LiveData<Resource<Void>> get() = _register

    private val _updateProfile = MutableLiveData<Resource<Void>>()
    val updateProfile : LiveData<Resource<Void>> get() = _updateProfile
    private val _secondLogin = MutableLiveData<Resource<LoginUser>>()
    val secondLogin : LiveData<Resource<LoginUser>> get() = _secondLogin
    private val _email = MutableLiveData<Resource<Int>>()
    val email : LiveData<Resource<Int>> get() = _email

    private val _reset = MutableLiveData<Resource<Int>>()
    val reset : LiveData<Resource<Int>> get() = _reset

    private suspend fun logIn(email:String, password:String) : Resource<LoginUser> {
        return withContext(Dispatchers.IO) {
            val user = AuthRequest(email, password, Build.MODEL)
            userRepository.login(user)
        }
    }

    fun onLogIn(email:String, password:String) {
        viewModelScope.launch {
            _login_user.value = logIn(email,password)
            if(_login_user.value!!.status==Resource.Status.SUCCESS && password!=MyApp.DEFAULT_PASS){
                _secondLogin.value = secondLogIn(email,password)
            }
        }
    }

    private suspend fun secondLogIn(email:String, password:String) : Resource<LoginUser> {
        return withContext(Dispatchers.IO) {
            val user = AuthRequest(email, password, Build.MODEL)
            userRepository.loginHibernate(user)
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
    fun onFindByMail(email: String) {
        viewModelScope.launch {
            _email.value = findUserByEmail(email)
        }
    }
    private suspend fun findUserByEmail(email: String) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            userRepository.findUserByEmail(email)
        }
    }
    fun onResetPassword(email:String){
        viewModelScope.launch {
         _reset.value=resetPassword(email)
        }
    }
    private suspend fun resetPassword(email:String) : Resource<Int>{
        return withContext(Dispatchers.IO) {
            userRepository.resetPassword(email)
        }
    }
}

class LoginUserViewModelFactory(
    private val userRepository: CommonLoginUserRepository,
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return LoginUserViewModel(userRepository, context) as T
    }

}