package com.example.bagshop.ui.features.product

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.bagshop.R
import com.example.bagshop.model.data.Comment
import com.example.bagshop.model.data.Product
import com.example.bagshop.ui.features.categoryScreen.CategoryViewModel
import com.example.bagshop.ui.features.signUp.TextFieldMain
import com.example.bagshop.ui.theme.*
import com.example.bagshop.util.MyScreens
import com.example.bagshop.util.NetworkChecker
import com.example.bagshop.util.stylePrice
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel

@Composable
fun ProductScreen(productId: String) {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(BackGroundMain)
    }
    val context = LocalContext.current
    val navController = getNavController()
    val viewModel = getNavViewModel<ProductViewModel>()
    viewModel.loadData(productId, NetworkChecker(context).isNetworkConnected)

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
            AppBarProduct(badgeNumber = viewModel.badgeNumberCart.value,
                onBackClick = { navController.popBackStack() },
                onCartClicked = {
                    if (NetworkChecker(context).isNetworkConnected) {
                        navController.navigate(MyScreens.CartScreen.route)
                    } else {
                        Toast.makeText(
                            context,
                            "please check your connection...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            val comments = if(NetworkChecker(context).isNetworkConnected) viewModel.commentsData.value else listOf()
            ProductDesign(viewModel.productData.value, comments,
                onCategoryClick = { navController.navigate(MyScreens.CategoryScreen.route + "/" + it) },
                addNewComment = {
                    viewModel.addNewComment(productId, it) { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                })
        }


        BottomCard(viewModel.productData.value.price, viewModel.isAddingProduct.value){
            if(NetworkChecker(context).isNetworkConnected){
                viewModel.addToCart(productId){
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "please check your connection...", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

@Composable
fun AppBarProduct(badgeNumber: Int, onBackClick: () -> Unit, onCartClicked: () -> Unit) {
    TopAppBar(
        backgroundColor = BackGroundMain,
        modifier = Modifier.fillMaxWidth().background(BackGroundMain),
        title = {
            Text(
                text = "Details",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackGroundMain)
                    .padding(end = 24.dp),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBackClick.invoke() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        elevation = 4.dp,
        actions = {
            IconButton(
                onClick = { onCartClicked.invoke() },
                modifier = Modifier.padding(bottom = 6.dp)
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
        }
    )

}

@Composable
fun ProductDesign(
    data: Product,
    comments: List<Comment>,
    onCategoryClick: (String) -> Unit,
    addNewComment: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {


        AsyncImage(
            model = data.imgUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(Shapes.medium),
            contentScale = ContentScale.Crop
        )

        DetailTexts(data, onCategoryClick)

        Divider(
            color = Color.LightGray, thickness = 1.dp,
            modifier = Modifier.padding(top = 12.dp)
        )

        DetailIcons(data, comments.size.toString())

        Divider(
            color = Color.LightGray, thickness = 1.dp,
            modifier = Modifier.padding(top = 10.dp)
        )

        ProductComment(comments, addNewComment)
    }
}

@Composable
fun DetailTexts(data: Product, onCategoryClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp), horizontalAlignment = Alignment.Start
    ) {
        Text(text = data.name, style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium))
        Text(
            text = data.detailText,
            style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Justify),
            modifier = Modifier.padding(top = 6.dp)
        )
        TextButton(onClick = { onCategoryClick.invoke(data.category) }) {
            Text(
                text = "#" + data.category,
                style = TextStyle(fontSize = 14.sp, color = Blue),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

    }
}

@Composable
fun DetailIcons(data: Product, commentNumber: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp), horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_details_comment),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            val commentText = if(NetworkChecker(context).isNetworkConnected) "$commentNumber Comments" else "No Internet"

            Text(
                text = commentText,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_details_material),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = data.material,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_details_sold),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = data.soldItem + " Sold",
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Surface(color = Blue, shape = Shapes.large) {
                Text(
                    text = data.tags,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Composable
fun ProductComment(comments: List<Comment>, addNewComment: (String) -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (comments.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comment",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium)
            )

            TextButton(onClick = {
                if (NetworkChecker(context).isNetworkConnected) {
                    showDialog.value = true
                } else {
                    Toast.makeText(context, "please check your connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }) {
                Text(text = "Add New Comment")
            }
        }

        comments.forEach {
            CommentItem(comment = it)
        }
    } else {
        TextButton(onClick = {
            if (NetworkChecker(context).isNetworkConnected) {
                showDialog.value = true
            } else {
                Toast.makeText(context, "please check your connection", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Add New Comment")
        }
    }

    if (showDialog.value) {
        AddNewCommentDialog(
            onDismiss = { showDialog.value = false },
            onPositiveClick = { addNewComment.invoke(it) }
        )
    }

}

@Composable
fun AddNewCommentDialog(onDismiss: () -> Unit, onPositiveClick: (String) -> Unit) {
    val context = LocalContext.current
    val userComment = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = Shapes.medium,
            modifier = Modifier.fillMaxHeight(0.53f),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Write Your Comment", textAlign = TextAlign.Center, style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userComment.value,
                    onValueChange = { userComment.value = it },
                    label = { Text(text = "write something...") },
                    placeholder = { Text(text = "your comment:") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    shape = Shapes.medium,
                )

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { onDismiss.invoke() }) {
                        Text(text = "cancel")
                    }
                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(onClick = {
                        if (userComment.value.isNotEmpty() && userComment.value.isNotBlank()) {
                            if (NetworkChecker(context).isNetworkConnected) {
                                onPositiveClick.invoke(userComment.value)
                                onDismiss.invoke()
                            } else {
                                Toast.makeText(
                                    context,
                                    "please check your connection...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "please enter your comment...",
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

@Composable
fun CommentItem(comment: Comment) {
    Card(
        backgroundColor = BackGroundMain,
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        elevation = 2.dp,
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = comment.userEmail,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
            Text(
                text = comment.text,
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun BottomCard(
    price:String,
    isAdding:Boolean,
    onCartClick:()->Unit
) {
    val configuration = LocalConfiguration.current
    val fraction = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)0.15f else 0.08f


    Surface(
        color = BackGroundMain, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction)
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {onCartClick.invoke()},
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue),
                shape = Shapes.medium,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(182.dp, 40.dp)
            ) {
                if (isAdding){
                    DotsTyping()
                }else{
                    Text(text = "Add To Cart",
                    modifier = Modifier.padding(2.dp),
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Surface(modifier = Modifier.padding(end = 8.dp).clip(Shapes.large), color = PriceBackGround) {
                Text(text = stylePrice(price), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium), modifier = Modifier.padding(
                    start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp
                ))
            }
        }
    }
}

@Composable
fun DotsTyping(){

    val dotSize = 10.dp
    val delayUnit = 350
    val maxOffset = 10f

    @Composable
    fun Dot(
        offset:Float,
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(y = -offset.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .padding(start = 8.dp, end = 8.dp)
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateOffsetWitDelay(delay:Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                maxOffset at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val offset1 by animateOffsetWitDelay(0)
    val offset2 by animateOffsetWitDelay(delayUnit)
    val offset3 by animateOffsetWitDelay(delayUnit * 2)
    
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offset = offset1)
        Spacer(modifier = Modifier.width(spaceSize))
        Dot(offset = offset2)
        Spacer(modifier = Modifier.width(spaceSize))
        Dot(offset = offset3)


    }
}
