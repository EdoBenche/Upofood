package it.benche.upofood.utils

import java.text.SimpleDateFormat
import java.util.*

object Constants {
    val FULL_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    val CALENDER_DATE_FORMATTER = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH)
    val DATE_FORMATTER = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    val DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)
    val DD_MMM_YYYY = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    val YYYY_MM_DD = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)


    var PLAY_STORE_URL_PREFIX = "https://play.google.com/store/apps/details?id="

    const val myPreferences = "MyPreferences"
    object AssetFiles{
        const val PRODUCTS="data/products.json"
        const val CATEGORYS="data/category.json"
        const val REVIEWS="data/reviews.json"
        const val ATTRIBUTES="data/attributes.json"

    }
    object RequestCode {
        const val EDIT = 200
        const val ADD_ADDRESS = 201
        const val CARTDATA = 202
        const val COUNTCARTDATA = 203
        const val ACCOUNT = 204
        const val PAYMENT=205

    }

    object SharedPref {
        const val KEY_NEXT_TIME_BUY = "next_time_buy"
        const val KEY_ORDERS = "orders"
        const val KEY_ORDER_COUNT = "order_count"
        const val KEY_RECENTS = "recentProduct"
        const val IS_LOGGED_IN = "isLoggedIn"
        const val USER_ID = "user_id"
        const val USER_DISPLAY_NAME = "user_display_name"
        const val USER_EMAIL = "user_email"
        const val USER_FIRST_NAME = "user_first_name"
        const val USER_LAST_NAME = "user_last_name"
        const val USER_PASSWORD = "user_password"
        const val USER_USERNAME = "user_username"
        const val SHOW_SWIPE = "showswipe"
        const val KEY_USER_ADDRESS = "user_address"
        const val KEY_USER_CART = "user_cart"
        const val CART_DATA = "cart_data"
        const val WISHLIST_DATA = "wishlist_data"
        const val KEY_WISHLIST_COUNT = "wishlist_count"
        const val KEY_CART_COUNT = "cart_count"
        const val USER_REVIEWS = "user_reviews"
        const val USER_PROFILE = "user_profile"

    }

    object KeyIntent {
        const val CATEGORY_DATA="category_data"
        const val IS_ADDED_TO_CART = "isAddedToCart"
        const val TITLE = "title"
        const val ADDRESS_ID = "address_id"
        const val PRODUCTID = "product_id"
        const val KEYID = "key_id"
        const val DATA = "data"
        const val PRODUCTDATA = "productdata"
        const val VIEWALLID = "viewallid"
    }

    object ApiKeys

    object OrderStatus {
        const val PENDING = "pending"
        const val PROCESSING = "processing"
        const val ONHOLD = "on-hold"
        const val COMPLETED = "completed"
        const val CANCELLED = "cancelled"
        const val REFUNDED = "refunded"
        const val FAILED = "failed"
        const val TRASH = "trash"
    }

    object viewAllCode {
        const val RECENTSEARCH = 100
        const val OFFERS = 101
        const val NEWEST = 102
        const val FEATURED = 103
        const val CATEGORY_NEWEST = 104
        const val CATEGORY_FEATURED = 105
    }

    object AppBroadcasts {
        const val CART_COUNT_CHANGE = "app.broadcast.setCartCount"
        const val CART_ITEM_CHANGE = "app.broadcast.onCartItemChanged"
        const val ORDER_COUNT_CHANGE = "app.broadcast.OnOrderCountChanged"
        const val PROFILE_UPDATE = "app.broadcast.OnProfileUpdated"
        const val WISHLIST_UPDATE = "app.broadcast.OnWishListUpdated"
        const val CARTITEM_UPDATE = "app.broadcast.OnCartItemUpdated"
        const val MYCARTITEM_UPDATE = "app.broadcast.OnMyCartItemUpdated"
    }

    object DateFormatCodes {
        const val YMD_HMS = 0
        const val YMD = 1

    }
}
