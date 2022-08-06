package com.example.bagshop.ui.features.signUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bagshop.model.repository.user.UserRepository
import com.example.bagshop.util.coroutineExceptionHandler
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepository: UserRepository):ViewModel() {
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    fun onRegisterClicked(LoggingEvent:(String)->Unit){
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = userRepository.signUp(name.value!!, email.value!!, password.value!!)
            LoggingEvent(result)
        }
    }
}