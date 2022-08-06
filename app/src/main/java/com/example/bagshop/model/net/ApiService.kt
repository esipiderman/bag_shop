package com.example.bagshop.model.net

import com.example.bagshop.model.data.*
import com.example.bagshop.model.repository.TokenInMemory
import com.example.bagshop.util.BASE_URL
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("signUp")
    suspend fun signUp(@Body body: JsonObject): LoginResponse

    @POST("signIn")
    suspend fun signIn(@Body body: JsonObject): LoginResponse

    @GET("refreshToken")
    fun refreshToken(): Call<LoginResponse>

    @GET("getSliderPics")
    suspend fun getAdData(): AdData

    @GET("getProducts")
    suspend fun getProductsData(): ProductData

    @POST("getComments")
    suspend fun getAllComment(@Body jsonObject: JsonObject): CommentResponse

    @POST("addNewComment")
    suspend fun addNewComment(@Body jsonObject: JsonObject): AddNewCommentResponse

    @POST("addToCart")
    suspend fun addProductToCart(@Body jsonObject: JsonObject): CartResponse

    @POST("removeFromCart")
    suspend fun removeFromCart(@Body jsonObject: JsonObject): CartResponse

    @GET("getUserCart")
    suspend fun getUserCart(): UserCartInfo

    @POST("submitOrder")
    suspend fun submitOrder(@Body jsonObject: JsonObject): SubmitOrder

    @POST("checkout")
    suspend fun checkOut(@Body jsonObject: JsonObject): CheckOut
}

fun createApiService(): ApiService {
    val okHttp = OkHttpClient.Builder()
        .addInterceptor {

            val oldRequest = it.request()

            val newRequest = oldRequest.newBuilder()
            if (TokenInMemory.token != null)
                newRequest.addHeader("Authorization", TokenInMemory.token!!)

            newRequest.addHeader("Accept", "application/json")
            newRequest.method(oldRequest.method, oldRequest.body)

            return@addInterceptor it.proceed(newRequest.build())

        }.build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp)
        .build()

    return retrofit.create(ApiService::class.java)
}