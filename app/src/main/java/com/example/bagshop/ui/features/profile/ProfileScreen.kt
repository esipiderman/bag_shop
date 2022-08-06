package com.example.bagshop.ui.features.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bagshop.R
import com.example.bagshop.ui.features.categoryScreen.CategoryViewModel
import com.example.bagshop.ui.theme.BackGroundMain
import com.example.bagshop.ui.theme.Blue
import com.example.bagshop.ui.theme.Shapes
import com.example.bagshop.util.MyScreens
import com.example.bagshop.util.NetworkChecker
import com.example.bagshop.util.styleTime
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel

@Composable
fun ProfileScreen() {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(BackGroundMain)
    }
    val context = LocalContext.current
    val navController = getNavController()
    val viewModel = getNavViewModel<ProfileViewModel>()
    viewModel.loadUserData()

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(bottom = 16.dp)
        ) {
            AppBarProfile { navController.popBackStack() }

            MainAnimation()

            Spacer(modifier = Modifier.padding(top = 6.dp))

            ShowDataSection("Email Address", viewModel.email.value, null)

            ShowDataSection("Address", viewModel.address.value) {
                viewModel.showLocationDialog.value = true
            }

            ShowDataSection("Postal Code", viewModel.postalCode.value) {
                viewModel.showLocationDialog.value = true
            }

            ShowDataSection("Login Time", styleTime(viewModel.loginTime.value.toLong()), null)

            Button(
                onClick = {
                    Toast.makeText(context, "Hope to see you again", Toast.LENGTH_SHORT).show()
                    viewModel.signOut()

                    navController.navigate(MyScreens.MainScreen.route) {
                        popUpTo(MyScreens.MainScreen.route) {
                            inclusive = true
                        }
                        navController.popBackStack()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 36.dp)
            ) {
                Text(text = "Sign Out")
            }
        }

        if (viewModel.showLocationDialog.value){
            GetLocationDialog(showSaveLocation = false, onDismiss = { viewModel.showLocationDialog.value = false},
                onPositiveClick ={ address, postalCode, _->
                    viewModel.setUserLocation(address, postalCode)
                })
        }
    }
}

@Composable
fun AppBarProfile(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 58.dp),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBackClick.invoke() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        backgroundColor = BackGroundMain,
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    )

}

@Composable
fun MainAnimation() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.profile_anim))

    LottieAnimation(
        composition = composition,
        modifier = Modifier
            .size(270.dp)
            .padding(top = 36.dp, bottom = 16.dp),
        iterations = LottieConstants.IterateForever
    )

}

@Composable
fun ShowDataSection(subject: String, text: String, onLocationClick: (() -> Unit)?) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .clickable { onLocationClick?.invoke() },
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = subject,
            style = TextStyle(fontSize = 18.sp, color = Blue, fontWeight = FontWeight.Bold)
        )
        Text(
            text = text,
            modifier = Modifier.padding(top = 2.dp),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
        )

        Divider(color = Blue, thickness = 0.5.dp, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun GetLocationDialog(
    showSaveLocation: Boolean,
    onDismiss: () -> Unit,
    onPositiveClick: (String, String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val checkedState = remember { mutableStateOf(true) }
    val userAddress = remember { mutableStateOf("") }
    val userPostalCode = remember { mutableStateOf("") }
    val fraction = if (showSaveLocation) 0.695f else 0.625f

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = Shapes.medium,
            modifier = Modifier.fillMaxHeight(fraction),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Add Location Data", textAlign = TextAlign.Center, style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userAddress.value,
                    onValueChange = { userAddress.value = it },
                    label = { Text(text = "your address...") },
                    placeholder = { Text(text = "your address :") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    shape = Shapes.medium,
                )

                OutlinedTextField(
                    value = userPostalCode.value,
                    onValueChange = { userPostalCode.value = it },
                    label = { Text(text = "your postal code...") },
                    placeholder = { Text(text = "your postal code :") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    shape = Shapes.medium,
                )

                if (showSaveLocation) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it })
                        Text(text = "Save To Profile")
                    }
                }

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { onDismiss.invoke() }) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(onClick = {
                        if (userAddress.value.isNotEmpty() && userAddress.value.isNotBlank() &&
                            userPostalCode.value.isNotEmpty() && userPostalCode.value.isNotBlank()
                        ) {

                            onPositiveClick.invoke(
                                userAddress.value,
                                userPostalCode.value,
                                checkedState.value
                            )
                            onDismiss.invoke()

                        } else {
                            Toast.makeText(
                                context,
                                "please enter all data...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text(text = "Ok")
                    }
                }
            }
        }
    }
}
