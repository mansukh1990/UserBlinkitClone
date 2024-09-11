package com.example.userblinkitclone.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.userblinkitclone.models.Product
import com.example.userblinkitclone.roomdb.CartProducts
import com.example.userblinkitclone.roomdb.CartProductsDao
import com.example.userblinkitclone.roomdb.CartProductsDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {

    //initializations
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("My_Pref", Context.MODE_PRIVATE)

    val cardProductDao: CartProductsDao =
        CartProductsDatabase.getDatabaseInstance(application).cartProductsDao()

    //Room db

    suspend fun insertCartProduct(cartProducts: CartProducts) {
        cardProductDao.insertCartProduct(cartProducts)
    }

    suspend fun updateCartProduct(cartProducts: CartProducts) {
        cardProductDao.updateCartProduct(cartProducts)
    }

    suspend fun deleteCartProduct(productId: String){
        cardProductDao.deleteCartProduct(productId)
    }

    //Firebase call
    fun fetchAllTheProducts(): Flow<ArrayList<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        db.addValueEventListener(eventListener)

        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun getCategoryProduct(category: String): Flow<ArrayList<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductCategory/${category}")

        val eventListener = object : ValueEventListener  {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)

                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(getApplication(), error.message, Toast.LENGTH_SHORT).show();

            }

        }
        db.addValueEventListener(eventListener)

        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    //share preference

    fun savingCardItemCount(itemCount: Int) {
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }

    fun fetchTotalCartItemCount(): MutableLiveData<Int> {
        val totalCount = MutableLiveData<Int>()
        totalCount.value = sharedPreferences.getInt("itemCount", 0)
        return totalCount
    }
}