package com.example.bagshop.di

import android.content.Context
import androidx.room.Room
import com.example.bagshop.model.db.MyDatabase
import com.example.bagshop.model.net.createApiService
import com.example.bagshop.model.repository.cart.CartRepository
import com.example.bagshop.model.repository.cart.CartRepositoryImpl
import com.example.bagshop.model.repository.comment.CommentRepository
import com.example.bagshop.model.repository.comment.CommentRepositoryImpl
import com.example.bagshop.model.repository.product.ProductRepository
import com.example.bagshop.model.repository.product.ProductRepositoryImpl
import com.example.bagshop.model.repository.user.UserRepository
import com.example.bagshop.model.repository.user.UserRepositoryImpl
import com.example.bagshop.ui.features.SignIn.SignInViewModel
import com.example.bagshop.ui.features.cart.CartViewModel
import com.example.bagshop.ui.features.categoryScreen.CategoryViewModel
import com.example.bagshop.ui.features.mainScreen.MainScreenViewModel
import com.example.bagshop.ui.features.product.ProductViewModel
import com.example.bagshop.ui.features.profile.ProfileViewModel
import com.example.bagshop.ui.features.signUp.SignUpViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myModules = module {

    single { createApiService() }
    single { androidContext().getSharedPreferences("data", Context.MODE_PRIVATE) }

    single { Room.databaseBuilder(androidContext(), MyDatabase::class.java, "app_database.db").build() }

    single<UserRepository>{UserRepositoryImpl(get(), get())}
    single<ProductRepository> { ProductRepositoryImpl(get(), get<MyDatabase>().productDao()) }
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single<CartRepository> { CartRepositoryImpl(get(), get())}

    viewModel{ SignUpViewModel(get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { (isNetConnected:Boolean)->MainScreenViewModel(get(),get(),isNetConnected ) }
    viewModel{ CategoryViewModel(get()) }
    viewModel{ ProductViewModel(get(),get(),get() ) }
    viewModel { ProfileViewModel(get()) }
    viewModel { CartViewModel(get(), get()) }

}