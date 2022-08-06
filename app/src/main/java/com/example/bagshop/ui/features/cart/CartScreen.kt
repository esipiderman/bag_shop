package com.example.bagshop.ui.features.cart

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bagshop.R
import com.example.bagshop.model.data.Product
import com.example.bagshop.model.data.UserCartInfo
import com.example.bagshop.ui.features.categoryScreen.CardCategory
import com.example.bagshop.ui.features.product.DotsTyping
import com.example.bagshop.ui.features.product.ProductViewModel
import com.example.bagshop.ui.features.profile.GetLocationDialog
import com.example.bagshop.ui.theme.BackGroundMain
import com.example.bagshop.ui.theme.Blue
import com.example.bagshop.ui.theme.PriceBackGround
import com.example.bagshop.ui.theme.Shapes
import com.example.bagshop.util.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel

@Composable
fun CartScreen() {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(BackGroundMain)
    }
    val context = LocalContext.current
    val navController = getNavController()
    val viewModel = getNavViewModel<CartViewModel>()
    viewModel.loadCartData()

    val getDataDialogState = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(rememberScrollState(), Orientation.Vertical)
                .padding(bottom = 64.dp)
        ) {
            AppBarCart(onBackClick = { navController.popBackStack() },
                onProfileClicked = { navController.navigate(MyScreens.ProfileScreen.route) })

            if (viewModel.productList.value.isNotEmpty()) {
                MiddleOfScreen(data = viewModel.productList.value,
                    isChangingItem = viewModel.isChangingNumber.value,
                    onItemClick = { navController.navigate(MyScreens.ProductScreen.route + "/" + it) },
                    onAddItemClick = { viewModel.addItem(it) },
                    onRemoveItemClick = { viewModel.removeItem(it) })
            } else {
                NoDataAnimation()
            }
        }

        BottomCardCart(price = viewModel.totalPrice.value.toString()) {
            if (viewModel.productList.value.isNotEmpty()) {
                val locationData = viewModel.getUserLocation()
                if (locationData.first == "click to add" || locationData.second == "click to add") {
                    getDataDialogState.value = true
                } else {
                    viewModel.purchaseAll(
                        locationData.first,
                        locationData.second
                    ) { success, link ->
                        if (success) {
                            viewModel.setPaymentStatus(PAYMENT_PENDING)

                            Toast.makeText(context, "pay using zarinPal...", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "problem in payment...", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                }
            } else {
                Toast.makeText(context, "there is nothing to purchase!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (getDataDialogState.value) {
            GetLocationDialog(
                showSaveLocation = true,
                onDismiss = { getDataDialogState.value = false },
                onPositiveClick = { address, postalCode, isChecked ->

                    if (NetworkChecker(context).isNetworkConnected) {
                        if (isChecked) {
                            viewModel.saveLocation(address, postalCode)
                        }
                        viewModel.purchaseAll(address, postalCode) { success, link ->
                            if (success) {
                                viewModel.setPaymentStatus(PAYMENT_PENDING)

                                Toast.makeText(context, "pay using zarinPal...", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "problem in payment...", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }


                    } else {
                        Toast.makeText(context, "please connect to internet", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            )
        }
    }

}

@Composable
fun AppBarCart(onBackClick: () -> Unit, onProfileClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "My Cart",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp),
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
        modifier = Modifier.fillMaxWidth(),
        actions = {
            IconButton(
                onClick = { onProfileClicked.invoke() },
                modifier = Modifier.padding(bottom = 6.dp)
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null
                )

            }
        }
    )

}

@Composable
fun MiddleOfScreen(
    data: List<UserCartInfo.Product>,
    isChangingItem: Pair<String, Boolean>,
    onItemClick: (String) -> Unit,
    onAddItemClick: (String) -> Unit,
    onRemoveItemClick: (String) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(data.size) {
            CardCart(data[it], isChangingItem, onItemClick, onAddItemClick, onRemoveItemClick)
        }
    }

}

@Composable
fun CardCart(
    data: UserCartInfo.Product,
    isChangingItem: Pair<String, Boolean>,
    onItemClick: (String) -> Unit,
    onAddItemClick: (String) -> Unit,
    onRemoveItemClick: (String) -> Unit
) {
    Card(elevation = 10.dp, shape = Shapes.medium, modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)
        .clickable { onItemClick.invoke(data.productId) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = data.imgUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = data.name,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp)
                )

                Text(
                    text = "From ${data.category} Group",
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                Text(
                    text = DETAIL1,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = DETAIL2,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 2.dp, end = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(Shapes.large),
                        color = PriceBackGround
                    ) {
                        Text(
                            text = stylePrice(
                                (data.price.toInt() * (data.quantity ?: "1").toInt()).toString()
                            ),
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(
                                start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp
                            )
                        )
                    }

                    Surface(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Card(
                            border = BorderStroke(2.dp, Blue),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                if (data.quantity?.toInt() == 1) {
                                    IconButton(onClick = { onRemoveItemClick.invoke(data.productId) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { onRemoveItemClick.invoke(data.productId) }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_minus),
                                            contentDescription = null
                                        )
                                    }
                                }

                                if (isChangingItem.first == data.productId && isChangingItem.second) {
                                    Text(text = "...", style = TextStyle(fontSize = 18.sp))
                                } else {
                                    Text(
                                        text = data.quantity ?: "1",
                                        style = TextStyle(fontSize = 18.sp)
                                    )
                                }

                                IconButton(onClick = { onAddItemClick.invoke(data.productId) }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }

                        }
                    }

                }
            }
        }
    }
}

@Composable
fun NoDataAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data))

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

@Composable
fun BottomCardCart(
    price: String,
    onPurchaseClick: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val fraction =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.15f else 0.08f


    Surface(
        color = BackGroundMain, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (NetworkChecker(context).isNetworkConnected) {
                        onPurchaseClick.invoke()
                    } else {
                        Toast.makeText(
                            context,
                            "please check your connection...",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue),
                shape = Shapes.medium,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(182.dp,40.dp)
            ) {

                Text(
                    text = "Let's Purchase",
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 2.dp),
                    color = Color.White,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center
                )

            }
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(Shapes.large),
                color = PriceBackGround
            ) {
                Text(
                    text = "total: " + stylePrice(price),
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(
                        start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp
                    )
                )
            }
        }
    }
}
