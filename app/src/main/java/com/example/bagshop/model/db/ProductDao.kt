package com.example.bagshop.model.db

import androidx.room.*
import com.example.bagshop.model.data.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM product_table")
    suspend fun getAll():List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(products:List<Product>)

    @Query("SELECT * FROM product_table WHERE productId = :productId")
    suspend fun getById(productId:String):Product

    @Query("SELECT * FROM product_table WHERE category = :category")
    suspend fun getByCategory(category:String):List<Product>
}