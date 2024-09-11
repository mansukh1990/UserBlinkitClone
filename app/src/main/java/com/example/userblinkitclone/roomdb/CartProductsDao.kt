package com.example.userblinkitclone.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProduct(cartProducts: CartProducts)

    @Update
    fun updateCartProduct(cartProducts: CartProducts)

    @Query("DELETE FROM CartProducts WHERE productId = :productId")
    fun deleteCartProduct(productId: String)

}