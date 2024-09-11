package com.example.userblinkitclone.models

data class Product(
    var productRandomId: String? = null,
    var productTitle: String? = null,
    var productQuantity: String? = null,
    var productUnit: String? = null,
    var productPrice: String? = null,
    var productStock: Int? = null,
    var productCategory: String? = null,
    var productType: String? = null,
    var itemCount: Int? = null,
    var adminUid: String? = null,
    var productImageUris: ArrayList<String?>? = null

)
