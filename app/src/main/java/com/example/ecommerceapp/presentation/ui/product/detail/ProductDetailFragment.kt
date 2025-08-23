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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment :
    BaseFragment<FragmentProductDetailBinding>(R.layout.fragment_product_detail) {
    val args: ProductDetailFragmentArgs by navArgs()
    private val viewModel: ProductDetailViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setProduct(args.product)
        collectState()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.product.collect { product ->
                binding.product = product
            }
        }
        binding.txtAddToCart.setOnClickListener {
            viewModel.addToCart()
        }
        binding.imgFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ProductDetailUiState.Error -> {
                        hideLoading()
                        showAppDialog("Hata", state.message)
                    }

                    ProductDetailUiState.Loading -> {
                        showLoading()
                    }

                    is ProductDetailUiState.AddedFavorite -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Favoriye eklendi", Toast.LENGTH_SHORT)
                            .show()
                    }

                    ProductDetailUiState.AddedBasket -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Sepete eklendi", Toast.LENGTH_SHORT)
                            .show()
                    }

                    ProductDetailUiState.RemoveFavorite -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Favoriden kaldırıldı", Toast.LENGTH_SHORT)
                            .show()
                    }

                    ProductDetailUiState.Empty -> Unit
                }
            }
        }
    }
}