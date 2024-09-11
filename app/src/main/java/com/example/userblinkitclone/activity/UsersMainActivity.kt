package com.example.userblinkitclone.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.NetworkManager
import com.example.userblinkitclone.databinding.ActivityUsersMainBinding
import com.example.userblinkitclone.viewmodels.UserViewModel

class UsersMainActivity : AppCompatActivity(), CartListener {

    private lateinit var binding: ActivityUsersMainBinding

    private val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

      //  checkInternet()
        getTotalItemCountInCart()

    }

    private fun checkInternet() {
        val networkManager = NetworkManager(this)
        networkManager.observe(this) { hasInternet ->
            if (hasInternet == true) {
                binding.llCart.visibility = View.VISIBLE
                getTotalItemCountInCart()
            } else {
                binding.llCart.visibility = View.GONE
                binding.tvText.visibility = View.VISIBLE
            }
        }
    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this) {
            if (it > 0) {
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = it.toString()
            } else {
                binding.llCart.visibility = View.GONE
            }
        }
    }

    override fun showCartLayout(itemCount: Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if (updatedCount > 0) {
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        } else {
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }


    }

    override fun savingCardItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this) {
            viewModel.savingCardItemCount(it + itemCount)
        }
    }
}