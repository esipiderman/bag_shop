package com.example.bagshop.model.repository.comment

import com.example.bagshop.model.data.Comment
import com.example.bagshop.model.net.ApiService
import com.google.gson.JsonObject

class CommentRepositoryImpl(
    private val apiService: ApiService
) : CommentRepository {
    override suspend fun getAllComment(productId: String): List<Comment> {
        val jsonObject = JsonObject().apply {
            addProperty("productId", productId)
        }

        val data = apiService.getAllComment(jsonObject)

        if (data.success){
            return data.comments
        }else{
            return listOf()
        }
    }

    override suspend fun addNewComment(
        productId: String,
        text: String,
        IsSuccess: (String) -> Unit
    ) {

        val jsonObject = JsonObject().apply {
            addProperty("productId", productId)
            addProperty("text",text)
        }

        val result = apiService.addNewComment(jsonObject)

        if (result.success){
            IsSuccess.invoke(result.message)
        }else{
            IsSuccess.invoke("comment not added")
        }

    }

}