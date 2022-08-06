package com.example.bagshop.ui.features.SignIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bagshop.model.repository.user.UserRepository
import com.example.bagshop.util.coroutineExceptionHandler
import kotlinx.coroutines.launch

class SignInViewModel(private val userRepository: UserRepository):ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun onLogInClicked(LoggingEvent:(String)->Unit){
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = userRepository.signIn(email.value!!, password.value!!)

            LoggingEvent(result)
        }

    }
}