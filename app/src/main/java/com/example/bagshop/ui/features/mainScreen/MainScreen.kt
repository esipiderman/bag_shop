package com.example.bagshop.ui.features.mainScreen

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.bagshop.R
import com.example.bagshop.model.data.Ad
import com.example.bagshop.model.data.CheckOut
import com.example.bagshop.model.data.Product
import com.example.bagshop.ui.theme.BackGroundMain
import com.example.bagshop.ui.theme.Blue
import com.example.bagshop.ui.theme.CategoryItemBackGround
import com.example.bagshop.ui.theme.Shapes
import com.example.bagshop.util.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainScreen() {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(BackGroundMain)
    }
    val context = LocalContext.current
    val navController = getNavController()
    val viewModel =
        getNavViewModel<MainScreenViewModel>(parameters = { parametersOf(NetworkChecker(context).isNetworkConnected) })

    if(NetworkChecker(context).isNetworkConnected){
        viewModel.loadBadgeNumber()
    }

    if (viewModel.getPaymentStatus() == PAYMENT_PENDING){
        if (NetworkChecker(context).isNetworkConnected){
            viewModel.getCheckoutData()
        }
    }

    Box {
        Column(
            horizontalAlignment = Alignment.Start, modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(bottom = 16.dp)
        ) {
            if (viewModel.progressBar.value) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Blue
                )
            }

            AppBarMainScreen(viewModel.badgeNumberCart.value, {
                if(NetworkChecker(context).isNetworkConnected){
                    navController.navigate(MyScreens.CartScreen.route)
                }else{
                    Toast.makeText(context, "please check your connection...", Toast.LENGTH_SHORT).show()
                }

            }, {
                navController.navigate(MyScreens.ProfileScreen.route)
            })
            CategoryBar(CATEGORY) {
                navController.navigate(MyScreens.CategoryScreen.route + "/" + it)
            }

            BuildUi(TAGS, viewModel.productData.value, viewModel.adsData.value){
                navController.navigate(MyScreens.ProductScreen.route + "/" + it)
            }

        }

        if (viewModel.showPaymentResultDialog.value){
            PaymentDialog(result = viewModel.checkOutData.value) {
                viewModel.showPaymentResultDialog.value = false
                viewModel.setPaymentStatus(NO_PAYMENT)
            }
        }


    }

}

@Composable
fun BuildUi(
    tags: List<String>,
    products: List<Product>,
    ads: List<Ad>,
    onProductClick: (String) -> Unit
) {
    val context = LocalContext.current
    if (products.isNotEmpty()) {

        Column {
            tags.forEachIndexed { index, it ->
                val filteredList = products.filter { product -> product.tags == it }

                ProductSubject(it, filteredList.shuffled(), onProductClick)

                if (ads.size >= 2)
                    if (index == 1 || index == 2)
                        BigPicture(ads[index - 1], onProductClick)

            }
        }
    }
}


@Composable
fun AppBarMainScreen(badgeNumber:Int,onCartClick: () -> Unit, onProfileClick: () -> Unit) {
    TopAppBar(
        backgroundColor = BackGroundMain,
        elevation = 0.dp,
        title = { Text(text = "Bag Shop") },
        actions = {
            IconButton(
                onClick = { onCartClick.invoke() },
            ) {
                if (badgeNumber > 0) {
                    BadgedBox(badge = { Badge { Text(text = badgeNumber.toString()) } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shopping_cart),
                            contentDescription = null
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shopping_cart),
                        contentDescription = null
                    )
                }
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun CategoryBar(category: List<Pair<String, Int>>, onCategoryClick: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(top = 16.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(category.size) {
            CategoryItem(category[it], onCategoryClick)
        }
    }
}

@Composable
fun CategoryItem(subject: Pair<String, Int>, onCategoryClick: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 16.dp)
            .clickable { onCategoryClick.invoke(subject.first) }
    ) {
        Surface(
            color = CategoryItemBackGround,
            shape = Shapes.medium
        ) {
            Image(
                painter = painterResource(id = subject.second),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(
            text = subject.first,
            style = TextStyle(color = Color.Gray),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ProductSubject(name: String, data: List<Product>,onProductClick: (String) -> Unit) {

    Column(
        modifier = Modifier.padding(top = 32.dp)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.h6
        )

        ProductBar(data,onProductClick)
    }
}

@Composable
fun ProductBar(data: List<Product>, onProductClick: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(top = 16.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(data.size) {
            ProductItem(data[it], onProductClick)
        }
    }
}

@Composable
fun ProductItem(data: Product, onProductClick: (String) -> Unit) {
    Card(
        backgroundColor = BackGroundMain,
        elevation = 10.dp,
        shape = Shapes.medium,
        modifier = Modifier
            .padding(start = 16.dp)
            .clickable { onProductClick.invoke(data.productId) }
    ) {
        Column (
            modifier = Modifier.background(BackGroundMain),
                ){
            AsyncImage(
                model = data.imgUrl,
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = data.name,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp)
                )
                Text(
                    text = stylePrice(data.price),
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(fontSize = 14.sp)
                )
                Text(
                    text = data.soldItem + " Sold",
                    style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                )
            }
        }
    }
}

@Composable
fun BigPicture(data: Ad, onProductClick: (String) -> Unit) {
    AsyncImage(
        model = data.imageURL, contentDescription = null,
        contentScale = ContentScale.Crop, modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
            .clip(Shapes.medium)
            .clickable { onProductClick.invoke(data.adId) }
    )
}

@Composable
fun PaymentDialog(result:CheckOut, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val userComment = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = Shapes.medium,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Payment Result", textAlign = TextAlign.Center, style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    ),modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (result.order?.status?.toInt() == PAYMENT_SUCCESS){
                    AsyncImage(model = R.drawable.success_anim, contentDescription = null,
                    contentScale = ContentScale.Crop, modifier = Modifier.size(110.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "payment was successful!", fontSize = 16.sp)

                    Text(text = "purchase Amount: " + stylePrice(
                        (result.order.amount).substring(
                            0, (result.order.amount).length - 1
                        )
                    ))


                }else{
                    AsyncImage(model = R.drawable.fail_anim, contentDescription = null,
                        contentScale = ContentScale.Crop, modifier = Modifier
                            .size(110.dp)
                            .padding(top = 6.dp, bottom = 6.dp))

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "payment was not successful!!!", fontSize = 16.sp)

                    Text(text = "purchase Amount: " + stylePrice(
                        (result.order!!.amount).substring(
                            0, (result.order.amount).length - 1
                        )
                    ))
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ){
                    TextButton(onClick = { onDismiss.invoke() }) {
                        Text(text = "ok")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
