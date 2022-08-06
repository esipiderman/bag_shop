package com.example.bagshop.ui.features.categoryScreen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bagshop.model.data.Product
import com.example.bagshop.ui.theme.BackGroundMain
import com.example.bagshop.ui.theme.Blue
import com.example.bagshop.ui.theme.Shapes
import com.example.bagshop.util.MyScreens
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel

@Composable
fun CategoryScreen(categoryName: String) {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(BackGroundMain)
    }
    val navController = getNavController()
    val viewModel = getNavViewModel<CategoryViewModel>()
    viewModel.updateData(categoryName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(rememberScrollState(), Orientation.Vertical)
    ) {
        AppBarCategory(name =categoryName)

        CategoryBar(viewModel.categoryData.value){
            navController.navigate(MyScreens.ProductScreen.route + "/" + it)
        }
    }

}

@Composable
fun AppBarCategory(name:String){
    TopAppBar(
        backgroundColor = BackGroundMain,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = name, style = MaterialTheme.typography.h6)
        }
    }

}

@Composable
fun CategoryBar(data:List<Product>, onCardClick:(productID:String)->Unit){
    LazyColumn(
        modifier = Modifier.padding(16.dp), contentPadding = PaddingValues(bottom = 8.dp)
    ){
        items(data.size){
            CardCategory(data[it], onCardClick)
        }
    }
}

@Composable
fun CardCategory(data:Product, onCardClick:(productID:String)->Unit){
    Card( backgroundColor = BackGroundMain,elevation = 10.dp, shape = Shapes.medium, modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)
        .clickable { onCardClick.invoke(data.productId) }) {
        Column (modifier = Modifier.fillMaxWidth()){
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

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),horizontalArrangement = Arrangement.SpaceBetween
                , verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = data.price + " Tomans",
                        style = TextStyle(fontSize = 14.sp)
                    )
                    Surface(shape = Shapes.large, color = Blue) {
                        Text(
                            text = data.soldItem + " Sold",
                            style = TextStyle(color = Color.White, fontSize = 14.sp),
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}