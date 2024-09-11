package com.example.userblinkitclone.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.NetworkManager
import com.example.userblinkitclone.R
import com.example.userblinkitclone.adapters.AdapterProduct
import com.example.userblinkitclone.databinding.FragmentCategoryBinding
import com.example.userblinkitclone.databinding.ItemViewProductBinding
import com.example.userblinkitclone.models.Product
import com.example.userblinkitclone.roomdb.CartProducts
import com.example.userblinkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding

    private var category: String? = null
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener: CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor()

        getProductCategory()
        setToolbarTitle()

        fetchCategoryProduct()
        onSearchMenuClicked()
        onNavigationIConClicked()
        // checkInternet()
    }

    private fun checkInternet() {
        val networkManager = NetworkManager(requireContext())
        networkManager.observe(viewLifecycleOwner) { hasInternet ->
            if (hasInternet == true) {
                binding.shimmerViewContainer.visibility = View.VISIBLE
                binding.tvText.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
                fetchCategoryProduct()
            } else {
                binding.shimmerViewContainer.visibility = View.GONE
                binding.rvProducts.visibility = View.GONE
                binding.tvText.visibility = View.VISIBLE
                binding.tvText.text = "No Internet Connection"
            }

        }
    }

    private fun onNavigationIConClicked() {
        binding.tbSearchFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    private fun onSearchMenuClicked() {
        binding.tbSearchFragment.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.searchMenu -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun fetchCategoryProduct() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getCategoryProduct(category!!).collect {

                if (it.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(
                    ::onAddButtonClicked,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                )
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.setProductList(it)
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>

                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun setToolbarTitle() {
        binding.tbSearchFragment.title = category
    }

    private fun getProductCategory() {
        val bundle = arguments
        category = bundle?.getString("category")
    }

    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding) {
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        //step 1

        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)

        // step 2

        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingCardItemCount(1)
            saveProductInRoomDb(product)
        }

    }

    private fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding) {
        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++
        productBinding.tvProductCount.text = itemCountInc.toString()

        cartListener?.showCartLayout(1)


        //step 2

        product.itemCount = itemCountInc
        lifecycleScope.launch {
            cartListener?.savingCardItemCount(1)
            saveProductInRoomDb(product)
        }

    }

    private fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding) {

        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--

        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListener?.savingCardItemCount(-1)
            saveProductInRoomDb(product)
        }

        if (itemCountDec > 0) {
            productBinding.tvProductCount.text = itemCountDec.toString()
        } else {
            lifecycleScope.launch {
                viewModel.deleteCartProduct(productId = product.productRandomId!!)
            }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"
        }


        cartListener?.showCartLayout(-1)

        //step 2

    }

    private fun saveProductInRoomDb(product: Product) {

        val cartProduct = CartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹" + "${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid,
        )
        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProduct)
        }

    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.orange)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is CartListener) {
            cartListener = context
        } else {
            throw ClassCastException("Please implement cart listener")
        }
    }
}