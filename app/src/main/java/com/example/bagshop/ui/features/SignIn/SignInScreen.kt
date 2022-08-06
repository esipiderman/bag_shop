package com.example.bagshop.ui.features.SignIn

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bagshop.R
import com.example.bagshop.ui.features.signUp.IconApp
import com.example.bagshop.ui.features.signUp.SignUpViewModel
import com.example.bagshop.ui.features.signUp.TextFieldMain
import com.example.bagshop.ui.features.signUp.TextFieldPassword
import com.example.bagshop.ui.theme.Blue
import com.example.bagshop.ui.theme.Shapes
import com.example.bagshop.util.MyScreens
import com.example.bagshop.util.NetworkChecker
import com.example.bagshop.util.SUCCESS
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel

@Composable
fun SignInScreen() {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(Blue)
    }
    val context = LocalContext.current
    val navController = getNavController()
    val signInViewModel = getNavViewModel<SignInViewModel>()

    clearInputs(signInViewModel)

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(Blue)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .scrollable(rememberScrollState(), Orientation.Vertical),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            IconApp()

            CreateCardSignIn(navController,signInViewModel) {
                signInViewModel.onLogInClicked{
                    if (it== SUCCESS){
                        navController.navigate(MyScreens.MainScreen.route){
                            popUpTo(MyScreens.IntroScreen.route){
                                inclusive = true
                            }
                        }
                    }else{
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }


    }
}

@Composable
fun CreateCardSignIn(navController: NavController, viewModel: SignInViewModel, signInEvent: () -> Unit) {
    val email = viewModel.email.observeAsState(initial = "")
    val password = viewModel.password.observeAsState(initial = "")
    val context = LocalContext.current


    Card(
        backgroundColor = Color.White,
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 10.dp
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Sign In",
                style = TextStyle(color = Blue, fontSize = 34.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(top = 18.dp)
            )

            TextFieldMain(edtValue = email.value, icon = R.drawable.ic_email, hint = "Email") {
                viewModel.email.value = it
            }

            TextFieldPassword(
                edtValue = password.value,
                leadingIcon = R.drawable.ic_password,
                hint = "Password"
            ) {
                viewModel.password.value = it
            }

            Button(
                onClick = {
                    if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                            if (password.value.length >=8){
                                if (NetworkChecker(context).isNetworkConnected){
                                    signInEvent.invoke()
                                }else{
                                    Toast.makeText(context, "please check your connection", Toast.LENGTH_LONG).show()
                                }
                            }else{
                                Toast.makeText(context, "passwords characters should be more than 8", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "email format is wrong", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "please write data first...", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = Shapes.medium,
                modifier = Modifier.padding(top = 28.dp, bottom = 16.dp)
            ) {
                Text(text = "Log In", modifier = Modifier.padding(8.dp))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            ) {
                Text(text = "Don't have an account?", color = Color.Black, fontSize = 16.sp)
                TextButton(onClick = {
                    navController.navigate(MyScreens.SignUpScreen.route){
                        popUpTo(MyScreens.SignInScreen.route){
                            inclusive = true
                        }
                    } }) {
                    Text(text = "Register Here", color = Blue)
                }
            }
        }

    }
}

fun clearInputs(viewModel: SignInViewModel){
    viewModel.email.value = ""
    viewModel.password.value = ""
}