package com.example.bagshop.ui.features.cart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bagshop.model.data.UserCartInfo
import com.example.bagshop.model.repository.cart.CartRepository
import com.example.bagshop.model.repository.user.UserRepository
import com.example.bagshop.util.coroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val productList = mutableStateOf(listOf<UserCartInfo.Product>())
    val totalPrice = mutableStateOf(0)
    val isChangingNumber = mutableStateOf(Pair("", false))

    fun loadCartData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.getUserCartInfo()

            productList.value = result.productList
            totalPrice.value = result.totalPrice
        }
    }

    fun addItem(productId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            isChangingNumber.value = isChangingNumber.value.copy(productId, true)
            val isSuccess = cartRepository.addToCart(productId)
            if (isSuccess.success){
                loadCartData()
            }
            delay(100)
            isChangingNumber.value = isChangingNumber.value.copy(productId, false)
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            isChangingNumber.value = isChangingNumber.value.copy(productId, true)
            val isSuccess = cartRepository.removeFromCart(productId)
            if (isSuccess){
                loadCartData()
            }
            delay(100)
            isChangingNumber.value = isChangingNumber.value.copy(productId, false)
        }
    }

    fun getUserLocation():Pair<String, String>{
        return userRepository.getUserLocation()
    }

    fun saveLocation(address:String, postalCode:String){
        userRepository.saveUserLocation(address, postalCode)
    }

    fun setPaymentStatus(status:Int){
        cartRepository.setPurchaseStatus(status)
    }

    fun purchaseAll(address: String, postalCode: String, IsSuccess:(Boolean, String)->Unit){
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.submitOrder(address, postalCode)
            IsSuccess.invoke(result.success, result.paymentLink)
        }
    }

}