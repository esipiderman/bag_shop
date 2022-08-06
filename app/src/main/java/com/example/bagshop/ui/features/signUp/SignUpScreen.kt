package com.example.bagshop.ui.features.signUp

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bagshop.R
import com.example.bagshop.ui.theme.BackGroundMain
import com.example.bagshop.ui.theme.Blue
import com.example.bagshop.ui.theme.MainAppTheme
import com.example.bagshop.ui.theme.Shapes
import com.example.bagshop.util.MyScreens
import com.example.bagshop.util.NetworkChecker
import com.example.bagshop.util.SUCCESS
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import java.util.regex.Pattern

@Composable
fun SignUpScreen() {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(Blue)
    }
    val context = LocalContext.current
    val navController = getNavController()
    val signUpViewModel = getNavViewModel<SignUpViewModel>()

    clearInputs(signUpViewModel)

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
                .fillMaxHeight(0.95f)
                .scrollable(rememberScrollState(), Orientation.Vertical),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconApp()

            CreateCardSignUp(navController, signUpViewModel) {
                signUpViewModel.onRegisterClicked{
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
fun IconApp() {
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .size(64.dp)
    ) {
        Image(
            modifier = Modifier.padding(16.dp),
            painter = painterResource(id = R.drawable.ic_icon_app),
            contentDescription = null
        )
    }
}

@Composable
fun CreateCardSignUp(navController: NavController, viewModel: SignUpViewModel, signUpEvent: () -> Unit) {
    val name = viewModel.name.observeAsState(initial = "")
    val email = viewModel.email.observeAsState(initial = "")
    val password = viewModel.password.observeAsState(initial = "")
    val confirmPassword = viewModel.confirmPassword.observeAsState(initial = "")
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
                text = "Sign Up",
                style = TextStyle(color = Blue, fontSize = 34.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(top = 18.dp)
            )

            TextFieldMain(
                edtValue = name.value,
                icon = R.drawable.ic_person,
                hint = "Your Full Name"
            ) {
                viewModel.name.value = it
            }

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

            TextFieldPassword(
                edtValue = confirmPassword.value,
                leadingIcon = R.drawable.ic_password,
                hint = "Confirm Password"
            ) {
                viewModel.confirmPassword.value = it
            }

            Button(
                onClick = {
                    if (name.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty() && confirmPassword.value.isNotEmpty()) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                            if (password.value == confirmPassword.value) {
                                if (password.value.length >= 8) {
                                    if (NetworkChecker(context).isNetworkConnected){
                                        signUpEvent.invoke()
                                    }else{
                                        Toast.makeText(context, "please check your connection", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(context, "passwords characters should be more than 8", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "passwords are not the same!", Toast.LENGTH_SHORT).show()
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
                Text(text = "Register Account", modifier = Modifier.padding(8.dp))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            ) {
                Text(text = "Already have an account?  ", color = Color.Black, fontSize = 16.sp)
                TextButton(onClick = {
                    navController.navigate(MyScreens.SignInScreen.route) {
                        popUpTo(MyScreens.SignUpScreen.route) {
                            inclusive = true
                        }
                    }
                }) {
                    Text(text = "Log In", color = Blue)
                }
            }
        }

    }
}

@Composable
fun TextFieldMain(edtValue: String, icon: Int, hint: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        value = edtValue,
        onValueChange = onValueChange,
        label = { Text(text = hint) },
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        },
        placeholder = { Text(text = hint) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        shape = Shapes.medium,
    )
}

@Composable
fun TextFieldPassword(edtValue: String, leadingIcon: Int, hint: String, onValueChange: (String) -> Unit) {
    val passwordVisible = remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = edtValue,
        onValueChange = onValueChange,
        label = { Text(text = hint) },
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(id = leadingIcon),
                contentDescription = null
            )
        },
        placeholder = { Text(text = hint) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        shape = Shapes.medium,
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image =
                if (passwordVisible.value) painterResource(id = R.drawable.ic_invisible)
                else painterResource(id = R.drawable.ic_visible)

            Icon(
                painter = image,
                contentDescription = null,
                modifier = Modifier.clickable { passwordVisible.value = !passwordVisible.value })
        }
    )
}

fun clearInputs(viewModel:SignUpViewModel){
    viewModel.name.value = ""
    viewModel.email.value = ""
    viewModel.password.value = ""
    viewModel.confirmPassword.value = ""

}