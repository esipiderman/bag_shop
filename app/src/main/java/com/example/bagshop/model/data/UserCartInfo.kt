package com.example.bagshop.model.data


import com.google.gson.annotations.SerializedName

data class UserCartInfo(
    @SerializedName("productList")
    val productList: List<Product>,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("totalPrice")
    val totalPrice: Int
) {
    data class Product(
        @SerializedName("category")
        val category: String,
        @SerializedName("imgUrl")
        val imgUrl: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("price")
        val price: String,
        @SerializedName("productId")
        val productId: String,
        @SerializedName("quantity")
        val quantity: String?,
        @SerializedName("tags")
        val tags: String
    )
}