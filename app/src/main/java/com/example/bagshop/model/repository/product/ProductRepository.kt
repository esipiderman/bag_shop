package com.example.bagshop.model.repository.product

import com.example.bagshop.model.data.Ad
import com.example.bagshop.model.data.Product

interface ProductRepository {

    suspend fun getAllProducts(isInternetConnected:Boolean):List<Product>

    suspend fun getAllAds(isInternetConnected:Boolean):List<Ad>

    suspend fun getByCategory(category:String):List<Product>

    suspend fun getById(productId:String):Product

}