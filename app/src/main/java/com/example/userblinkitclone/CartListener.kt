package com.example.userblinkitclone

interface CartListener {

    fun showCartLayout(itemCount : Int)

    fun savingCardItemCount(itemCount: Int)
}