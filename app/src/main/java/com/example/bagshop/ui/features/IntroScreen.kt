package com.example.bagshop.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bagshop.R
import com.example.bagshop.ui.theme.BackGroundMain
import com.example.bagshop.ui.theme.Blue
import com.example.bagshop.ui.theme.MainAppTheme
import com.example.bagshop.ui.theme.Shapes
import com.example.bagshop.util.MyScreens
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController

@Composable
fun IntroScreen() {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(Blue)
    }
    val navigation = getNavController()

    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(id = R.drawable.img_intro),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier
            .fillMaxHeight(0.78f)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = { navigation.navigate(MyScreens.SignUpScreen.route) },
            modifier = Modifier
                .fillMaxWidth(0.7f),
            shape = Shapes.medium
        ) {
            Text(text = "Sign Up", color = Color.White)

        }
        Button(
            onClick = { navigation.navigate(MyScreens.SignInScreen.route)},
            modifier = Modifier
                .fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            shape = Shapes.medium
        ) {
            Text(text = "Sign In", color = Blue)

        }
    }

}