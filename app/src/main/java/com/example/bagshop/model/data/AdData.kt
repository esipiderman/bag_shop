package com.example.bagshop.model.data


import com.google.gson.annotations.SerializedName

data class AdData(
    val ads: List<Ad>,
    val success: Boolean
)

data class Ad(
    val adId: String,
    val imageURL: String,
    val productId: String
)
