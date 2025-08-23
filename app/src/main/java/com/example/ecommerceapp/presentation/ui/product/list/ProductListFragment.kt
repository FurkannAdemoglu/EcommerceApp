package com.example.ecommerceapp.presentation.ui.product.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.base.BaseFragment
import com.example.ecommerceapp.databinding.FragmentProductListBinding
import com.example.ecommerceapp.presentation.ui.product.detail.ProductDetailUiState
import com.example.ecommerceapp.presentation.ui.product.list.adapter.ProductListAdapter
import com.example.ecommerceapp.presentation.ui.product.list.adapter.click.OnClicksProduct
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.presentation.ui.product.list.dialog.FilterSortDialogFragment
import com.example.netflixcloneapp.utils.BottomNavigationAnnotation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@BottomNavigationAnnotation
class ProductListFragment :
    BaseFragment<FragmentProductListBinding>(R.layout.fragment_product_list) {
    private val viewModel: ProductListViewModel by viewModels()
    private val productListAdapter by lazy { ProductListAdapter() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectState()
        setAdapter()
        adapterOnClicks()
        binding.edTxtSearch.addTextChangedListener { text->
            viewModel.searchProducts(text.toString())
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ProductListUiState.Error -> {
                        showAppDialog("Hata", state.message)
                    }

                    ProductListUiState.Loading -> {
                        showLoading()
                    }

                    is ProductListUiState.Success -> {
                        hideLoading()
                        productListAdapter.setProductListData(state.productList ?: emptyList())
                        filterDialog(state.productList?: emptyList())
                    }

                    is ProductListUiState.AddedFavorite -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Favoriye eklendi", Toast.LENGTH_SHORT)
                            .show()
                    }

                    ProductListUiState.AddedBasket -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Sepete eklendi", Toast.LENGTH_SHORT)
                            .show()
                    }

                    ProductListUiState.RemoveFavorite -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Favoriden kaldırıldı", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun filterDialog(productList:List<ProductListViewItem>){
        binding.txtSelectFilter.setOnClickListener {
            val brandList = viewModel.productList.mapNotNull { (it as? ProductListViewItem.ItemProductListViewItem)?.product?.brand }.distinct()
            val modelList = viewModel.productList.mapNotNull { (it as? ProductListViewItem.ItemProductListViewItem)?.product?.model }.distinct()

            FilterSortDialogFragment(brandList, modelList) { brand, model, sortBy ->
                viewModel.filterAndSortProducts(brand, model, sortBy)
            }.show(parentFragmentManager, "filterDialog")
        }
    }

    private fun setAdapter() {
        binding.recyclerViewProductList.apply {
            this.layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = productListAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    if (lastVisible >= productListAdapter.itemCount - 1) {
                        viewModel.loadNextPage() // ViewModel’e yeni sayfayı yüklemesini söyle
                    }
                }
            })
        }
    }

    private fun adapterOnClicks() {
        productListAdapter.onClick = { onClick ->
            when (onClick) {
                is OnClicksProduct.OnClickAddToCart -> {
                    viewModel.addToCart(onClick.product)
                }

                is OnClicksProduct.OnClickAddToFavorite -> {
                    viewModel.toggleFavorite(onClick.isFavorite, onClick.id)
                    productListAdapter.updateItem(onClick.position, onClick.isFavorite)
                }

                is OnClicksProduct.OnClickRoot -> {
                    findNavController().navigate(
                        ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(
                            onClick.product
                        )
                    )
                }
            }
        }
    }
}