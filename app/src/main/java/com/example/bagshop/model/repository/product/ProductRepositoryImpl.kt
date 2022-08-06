package com.example.bagshop.model.repository.product

import com.example.bagshop.model.data.Ad
import com.example.bagshop.model.data.Product
import com.example.bagshop.model.db.ProductDao
import com.example.bagshop.model.net.ApiService

class ProductRepositoryImpl(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ProductRepository {
    override suspend fun getAllProducts(isInternetConnected: Boolean): List<Product> {
        if (isInternetConnected) {
            val response = apiService.getProductsData()
            if (response.success) {
                productDao.insertOrUpdate(response.products)
                return response.products
            }

        } else {
            return productDao.getAll()
        }
        return listOf()
    }

    override suspend fun getAllAds(isInternetConnected: Boolean): List<Ad> {

        if (isInternetConnected){
            val response = apiService.getAdData()
            if (response.success){
                return response.ads
            }
        }else{
            return listOf()
        }
        return listOf()
    }

    override suspend fun getByCategory(category: String): List<Product> {
        return productDao.getByCategory(category)
    }

    override suspend fun getById(productId: String): Product {
        return productDao.getById(productId)
    }

}