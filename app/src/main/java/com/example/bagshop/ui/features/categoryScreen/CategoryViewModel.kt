package com.example.bagshop.ui.features.categoryScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bagshop.model.data.Product
import com.example.bagshop.model.repository.product.ProductRepository
import com.example.bagshop.util.coroutineExceptionHandler
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val productRepository: ProductRepository
): ViewModel() {
    val categoryData = mutableStateOf<List<Product>>(listOf())

    fun updateData(category: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            categoryData.value = productRepository.getByCategory(category)
        }
    }

}