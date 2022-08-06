package com.example.bagshop.model.repository

object TokenInMemory {

    var username: String? = null
        private set

    var token: String? = null
        private set

    fun refreshToken(username:String?, token:String?){
        this.username = username
        this.token = token
    }
}