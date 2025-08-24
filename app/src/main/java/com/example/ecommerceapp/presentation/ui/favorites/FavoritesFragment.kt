package com.example.ecommerceapp.presentation.ui.favorites

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.base.BaseFragment
import com.example.ecommerceapp.databinding.FragmentFavoritesBinding
import com.example.ecommerceapp.presentation.ui.product.list.adapter.ProductListAdapter
import com.example.ecommerceapp.presentation.ui.product.list.adapter.click.OnClicksProduct
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.utils.EmptyView
import com.example.ecommerceapp.utils.isConnected
import com.example.ecommerceapp.utils.openToast
import com.example.netflixcloneapp.utils.BottomNavigationAnnotation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@BottomNavigationAnnotation
class FavoritesFragment:BaseFragment<FragmentFavoritesBinding>(R.layout.fragment_favorites) {
    private val viewModel: FavoriteViewModel by viewModels()
    private val productListAdapter by lazy { ProductListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectState()
        setAdapter()
        adapterOnClicks()
        checkInternetAndLoad()
        viewModel.getProductList()
    }
    private fun checkInternetAndLoad() {
        if (requireContext().isConnected()) {
            viewModel.getProductList()
        } else {
            showNoInternetDialogLoop{
                viewModel.getProductList()
            }
        }
    }
    private fun setAdapter() {
        binding.rcycFavoriteList.apply {
            this.adapter = productListAdapter
        }
    }
    private fun collectState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is FavoriteListUiState.Error -> {
                        hideLoading()
                        showAppDialog(getString(R.string.error_text), state.message)
                    }

                    FavoriteListUiState.Loading -> {
                        showLoading()
                    }

                    is FavoriteListUiState.Success -> {
                        hideLoading()
                        binding.rcycFavoriteList.layoutManager = GridLayoutManager(requireContext(), 2)
                        productListAdapter.setProductListData(state.productList ?: emptyList())
                    }


                    FavoriteListUiState.AddedBasket -> {
                        hideLoading()
                        requireContext().openToast(getString(R.string.added_to_basket), Toast.LENGTH_SHORT)
                    }

                    FavoriteListUiState.RemoveFavorite -> {
                        hideLoading()
                        requireContext().openToast(getString(R.string.removed_favorites), Toast.LENGTH_SHORT)
                    }

                    FavoriteListUiState.EmptyFavorite ->{
                        hideLoading()
                        binding.rcycFavoriteList.layoutManager = LinearLayoutManager(requireContext())
                        productListAdapter.setProductListData(
                            listOf(
                                ProductListViewItem.ItemBasketEmptyViewItem(
                                    EmptyView(
                                        getString(
                                            R.string.empty_favorite_description,
                                        ),
                                        getString(R.string.empty_favorite_button)
                                    )
                                )
                            )
                        )
                    }

                    FavoriteListUiState.Empty -> Unit
                }
            }
        }
    }
    private fun adapterOnClicks() {
        productListAdapter.onClick = { onClick ->
            when (onClick) {
                is OnClicksProduct.OnClickAddToCart -> {
                    viewModel.addToCart(onClick.product)
                }

                is OnClicksProduct.OnClickAddToFavorite -> {
                    viewModel.removeFromFavorite(onClick.id)
                    productListAdapter.removeItem(onClick.position)
                }

                is OnClicksProduct.OnClickRoot -> {
                    findNavController().navigate(
                        FavoritesFragmentDirections.actionFavoriteFragmentToProductDetailFragment(
                            onClick.product
                        )
                    )
                }
            }
        }
        productListAdapter.onClickEmpty={
            findNavController().popBackStack(R.id.productListFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dispose()
    }

    override fun setupToolbar() {
        configureToolbar(getString(R.string.e_market),false)
    }
}