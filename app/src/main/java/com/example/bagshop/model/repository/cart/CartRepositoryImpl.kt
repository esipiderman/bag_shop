package com.example.bagshop.model.repository.cart

import android.content.SharedPreferences
import com.example.bagshop.model.data.CartResponse
import com.example.bagshop.model.data.CheckOut
import com.example.bagshop.model.data.SubmitOrder
import com.example.bagshop.model.data.UserCartInfo
import com.example.bagshop.model.net.ApiService
import com.example.bagshop.util.NO_PAYMENT
import com.google.gson.JsonObject

class CartRepositoryImpl(
    private val apiService: ApiService,
    private val sharedPref: SharedPreferences
) : CartRepository {
    override suspend fun addToCart(productId: String): CartResponse {
        val jsonObject = JsonObject().apply {
            addProperty("productId", productId)
        }
        return apiService.addProductToCart(jsonObject)


    }

    override suspend fun getCartSize(): Int {
        val result = apiService.getUserCart()
        if (result.success) {

            var counter = 0

            result.productList.forEach {
                counter += (it.quantity ?: "0").toInt()
            }

            return counter

        }

        return 0
    }

    override suspend fun removeFromCart(productId: String): Boolean {
        val jsonObject = JsonObject().apply {
            addProperty("productId", productId)
        }
        return apiService.removeFromCart(jsonObject).success
    }

    override suspend fun getUserCartInfo(): UserCartInfo {
        return apiService.getUserCart()
    }

    override suspend fun submitOrder(address: String, postalCode: String): SubmitOrder {
        val jsonObject = JsonObject().apply {
            addProperty("address", address)
            addProperty("postalCode", postalCode)
        }
        val result = apiService.submitOrder(jsonObject)

        setOrderId(result.orderId.toString())

        return result
    }

    override suspend fun checkout(orderId: String): CheckOut {
        val jsonObject = JsonObject().apply {
            addProperty("orderId", orderId)
        }

        return apiService.checkOut(jsonObject)
    }

    override fun setOrderId(orderId: String) {
        sharedPref.edit().putString("orderId", orderId).apply()
    }

    override fun getOrderId(): String {
        return sharedPref.getString("orderId", "0")!!
    }

    override fun setPurchaseStatus(status: Int) {
        sharedPref.edit().putInt("purchase_status", status).apply()
    }

    override fun getPurchaseStatus(): Int {
        return sharedPref.getInt("purchase_status", NO_PAYMENT)
    }
}