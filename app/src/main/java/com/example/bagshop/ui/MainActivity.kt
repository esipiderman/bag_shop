package com.example.bagshop.ui

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bagshop.di.myModules
import com.example.bagshop.model.repository.TokenInMemory
import com.example.bagshop.model.repository.user.UserRepository
import com.example.bagshop.ui.features.IntroScreen
import com.example.bagshop.ui.features.SignIn.SignInScreen
import com.example.bagshop.ui.features.cart.CartScreen
import com.example.bagshop.ui.features.categoryScreen.CategoryScreen
import com.example.bagshop.ui.features.mainScreen.MainScreen
import com.example.bagshop.ui.features.product.ProductScreen
import com.example.bagshop.ui.features.profile.ProfileScreen
import com.example.bagshop.ui.features.signUp.SignUpScreen
import com.example.bagshop.ui.theme.BackGroundMain
import com.example.bagshop.ui.theme.MainAppTheme
import com.example.bagshop.util.KEY_CATEGORY_SCREEN
import com.example.bagshop.util.KEY_PRODUCT_SCREEN
import com.example.bagshop.util.MyScreens
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import dev.burnoo.cokoin.navigation.KoinNavHost
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        setContent {
            Koin(appDeclaration = {
                modules(myModules)
                androidContext(this@MainActivity)
            }) {
                MainAppTheme {
                    Surface(
                        color = BackGroundMain,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val userRepository :UserRepository = get()
                        userRepository.loadToken()

                        BagShopUi()
                    }
                }
            }
        }
    }
}

@Composable
fun BagShopUi() {
    val navController = rememberNavController()

    KoinNavHost(
        navController = navController,
        startDestination = MyScreens.MainScreen.route
    ) {
        composable(route = MyScreens.MainScreen.route) {
            if (TokenInMemory.token==null || TokenInMemory.token == ""){
                IntroScreen()
            }else{
                MainScreen()
            }
        }

        composable(
            route = MyScreens.ProductScreen.route + "/{" + KEY_PRODUCT_SCREEN + "}",
            arguments = listOf(navArgument(KEY_PRODUCT_SCREEN) {
                type = NavType.StringType
            })
        ) {
            ProductScreen(it.arguments!!.getString(KEY_PRODUCT_SCREEN, "null"))
        }

        composable(
            route = MyScreens.CategoryScreen.route + "/{" + KEY_CATEGORY_SCREEN + "}",
            arguments = listOf(navArgument(
                KEY_CATEGORY_SCREEN
            ) {
                type = NavType.StringType
            })
        ) {
            CategoryScreen(it.arguments!!.getString(KEY_CATEGORY_SCREEN, "null"))
        }

        composable(MyScreens.ProfileScreen.route) {
            ProfileScreen()
        }

        composable(MyScreens.CartScreen.route) {
            CartScreen()
        }

        composable(MyScreens.SignUpScreen.route) {
            SignUpScreen()
        }

        composable(MyScreens.SignInScreen.route) {
            SignInScreen()
        }
    }

}




