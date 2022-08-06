package com.example.bagshop.util

import com.example.bagshop.R

const val KEY_CATEGORY_SCREEN = "categoryName"
const val KEY_PRODUCT_SCREEN = "categoryName"

const val BASE_URL = "https://dunijet.ir/Projects/DuniBazaar/"
const val SUCCESS = "success"

val CATEGORY = listOf(
    Pair("Backpack", R.drawable.ic_cat_backpack),
    Pair("Handbag", R.drawable.ic_cat_handbag),
    Pair("Shopping", R.drawable.ic_cat_shopping),
    Pair("Tote", R.drawable.ic_cat_tote),
    Pair("Satchel", R.drawable.ic_cat_satchel),
    Pair("Clutch", R.drawable.ic_cat_clutch),
    Pair("Wallet", R.drawable.ic_cat_wallet),
    Pair("Sling", R.drawable.ic_cat_sling),
    Pair("Bucket", R.drawable.ic_cat_bucket),
    Pair("Quilted", R.drawable.ic_cat_quilted)
)

val TAGS = listOf(
    "Newest",
    "Best Sellers",
    "Most Visited",
    "Highest Quality"
)

const val DETAIL1 = "Product authenticity guarantee"

const val DETAIL2 = "Available in stock to ship"

const val PAYMENT_SUCCESS = 1
const val PAYMENT_PENDING = 0
const val PAYMENT_FAILED = -1
const val NO_PAYMENT = -2