package com.example.ecommerceapp.presentation.ui.product.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.ecommerceapp.R
import com.example.ecommerceapp.base.BaseFragment
import com.example.ecommerceapp.databinding.FragmentProductDetailBinding
import com.example.ecommerceapp.utils.openToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment :
    BaseFragment<FragmentProductDetailBinding>(R.layout.fragment_product_detail) {
    val args: ProductDetailFragmentArgs by navArgs()
    private val viewModel: ProductDetailViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings()
        collectState()
        clickListeners()
    }

    private fun bindings(){
        binding.apply {
            product = args.product
            isFavorite = args.product.isFavorite
            lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun clickListeners(){
        binding.txtAddToCart.setOnClickListener {
            viewModel.addToCart(args.product)
        }
        binding.imgFavorite.setOnClickListener {
            viewModel.toggleFavorite(args.product)
        }
    }
    override fun setupToolbar() {
        configureToolbar(args.product.name,true)
    }

    private fun collectState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ProductDetailUiState.Error -> {
                        hideLoading()
                        showAppDialog(getString(R.string.error_text), state.message)
                    }

                    ProductDetailUiState.Loading -> {
                        showLoading()
                    }

                    is ProductDetailUiState.AddedFavorite -> {
                        hideLoading()
                        binding.isFavorite = true
                        requireContext().openToast(getString(R.string.added_to_favorites), Toast.LENGTH_SHORT)
                    }

                    ProductDetailUiState.AddedBasket -> {
                        hideLoading()
                        requireContext().openToast(getString(R.string.added_to_basket), Toast.LENGTH_SHORT)
                    }

                    ProductDetailUiState.RemoveFavorite -> {
                        hideLoading()
                        binding.isFavorite = false
                        requireContext().openToast(getString(R.string.removed_favorites), Toast.LENGTH_SHORT)
                    }

                    ProductDetailUiState.Empty -> Unit
                }
            }
        }
    }
}