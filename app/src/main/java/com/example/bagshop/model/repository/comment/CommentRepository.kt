package com.example.bagshop.model.repository.comment

import com.example.bagshop.model.data.Ad
import com.example.bagshop.model.data.Comment
import com.example.bagshop.model.data.Product

interface CommentRepository {

    suspend fun getAllComment(productId:String):List<Comment>

    suspend fun addNewComment(productId:String, text:String, IsSuccess:(String)->Unit)
}