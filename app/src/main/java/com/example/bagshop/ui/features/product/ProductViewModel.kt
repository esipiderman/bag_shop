package com.example.bagshop.ui.features.product

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bagshop.model.data.Comment
import com.example.bagshop.model.data.Product
import com.example.bagshop.model.repository.cart.CartRepository
import com.example.bagshop.model.repository.comment.CommentRepository
import com.example.bagshop.model.repository.product.ProductRepository
import com.example.bagshop.util.coroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val commentRepository: CommentRepository,
    private val cartRepository:CartRepository
) : ViewModel() {
    val productData = mutableStateOf(Product("", "", "", "", "", "", "", "", ""))
    val commentsData = mutableStateOf(listOf<Comment>())
    val badgeNumberCart = mutableStateOf(0)
    val isAddingProduct = mutableStateOf(false)

    fun loadData(productId: String, isInternetConnected: Boolean) {
        updateProductData(productId)
        if (isInternetConnected) {
            loadAllComments(productId)
            loadBadgeNumber()
        }
    }

    private fun updateProductData(productId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            productData.value = productRepository.getById(productId)
        }
    }

    private fun loadAllComments(productId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            commentsData.value = commentRepository.getAllComment(productId)
        }
    }

    fun addNewComment(productId: String, text: String, IsSuccess: (String) -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            commentRepository.addNewComment(productId, text, IsSuccess)

            delay(100)

            commentsData.value = commentRepository.getAllComment(productId)
        }
    }

    fun addToCart(productId: String, addingToCartResult:(String)->Unit){
        viewModelScope.launch(coroutineExceptionHandler) {
            isAddingProduct.value = true

            val result = cartRepository.addToCart(productId)
            delay(500)

            isAddingProduct.value = false

            if (result.success){
                addingToCartResult.invoke("Product added to cart")
            }else{
                addingToCartResult.invoke(result.message)
            }
        }
    }

    private fun loadBadgeNumber(){
        viewModelScope.launch(coroutineExceptionHandler) {
            badgeNumberCart.value = cartRepository.getCartSize()
        }
    }

}