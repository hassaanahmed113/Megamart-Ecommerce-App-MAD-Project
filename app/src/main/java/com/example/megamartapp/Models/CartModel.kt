package com.example.megamartapp.Models

data class CartModel(
    val pid:String? = null,
    val uid :String? = null,
    val imageUrl :String? = null,
    val name : String? = null,
    val price : String? = null,
    val size : String? = null,
    var quantity: Int? = null

)