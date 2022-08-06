package com.example.bagshop.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bagshop.model.data.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}