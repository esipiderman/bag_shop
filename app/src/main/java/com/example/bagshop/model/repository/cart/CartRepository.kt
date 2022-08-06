package com.example.bagshop.model.repository.cart

import com.example.bagshop.model.data.CartResponse
import com.example.bagshop.model.data.CheckOut
import com.example.bagshop.model.data.SubmitOrder
import com.example.bagshop.model.data.UserCartInfo

interface CartRepository {

    suspend fun addToCart(productId: String): CartResponse

    suspend fun getCartSize(): Int

    suspend fun removeFromCart(productId: String): Boolean
    suspend fun getUserCartInfo(): UserCartInfo

    suspend fun submitOrder(address: String, postalCode: String): SubmitOrder
    suspend fun checkout(orderId: String): CheckOut

    fun setOrderId(orderId: String)
    fun getOrderId(): String

    fun setPurchaseStatus(status: Int)
    fun getPurchaseStatus(): Int
}