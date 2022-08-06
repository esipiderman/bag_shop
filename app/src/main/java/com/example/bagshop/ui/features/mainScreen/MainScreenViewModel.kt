package com.example.bagshop.ui.features.mainScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bagshop.model.data.Ad
import com.example.bagshop.model.data.CheckOut
import com.example.bagshop.model.data.Product
import com.example.bagshop.model.repository.cart.CartRepository
import com.example.bagshop.model.repository.product.ProductRepository
import com.example.bagshop.util.coroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    isInternetConnected: Boolean
) : ViewModel() {

    val productData = mutableStateOf<List<Product>>(listOf())
    val adsData = mutableStateOf<List<Ad>>(listOf())
    val progressBar = mutableStateOf(false)
    val badgeNumberCart = mutableStateOf(0)
    val showPaymentResultDialog = mutableStateOf(false)
    val checkOutData = mutableStateOf(CheckOut(null ,null))

    init {
        refreshData(isInternetConnected)
    }

    private fun refreshData(internetConnected: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            progressBar.value = true
            val products = async { productRepository.getAllProducts(internetConnected) }
            val ads = async { productRepository.getAllAds(internetConnected) }

            updateData(products.await(), ads.await())

            progressBar.value = false
        }
    }

    private fun updateData(products: List<Product>, ads: List<Ad>) {
        productData.value = products
        adsData.value = ads
    }

    fun loadBadgeNumber() {
        viewModelScope.launch(coroutineExceptionHandler) {
            badgeNumberCart.value = cartRepository.getCartSize()
        }
    }

    fun getCheckoutData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.checkout(cartRepository.getOrderId())

            if (result.success!!) {
                checkOutData.value = result
                showPaymentResultDialog.value = true
            }
        }
    }

    fun getPaymentStatus(): Int {
        return cartRepository.getPurchaseStatus()
    }

    fun setPaymentStatus(status: Int) {
        cartRepository.setPurchaseStatus(status)
    }
}