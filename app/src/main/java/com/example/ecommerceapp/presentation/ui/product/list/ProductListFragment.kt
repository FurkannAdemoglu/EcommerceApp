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
import com.example.ecommerceapp.presentation.ui.product.list.adapter.ProductListAdapter
import com.example.ecommerceapp.presentation.ui.product.list.adapter.click.OnClicksProduct
import com.example.ecommerceapp.presentation.ui.product.list.adapter.viewitem.ProductListViewItem
import com.example.ecommerceapp.presentation.ui.product.list.dialog.FilterDialogFragment
import com.example.ecommerceapp.utils.isConnected
import com.example.ecommerceapp.utils.openToast
import com.example.netflixcloneapp.utils.BottomNavigationAnnotation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
        binding.edTxtSearch.addTextChangedListener { text ->
            viewModel.filterProducts(
                selectedBrands = viewModel.selectedBrands,
                selectedModels = viewModel.selectedModels,
                searchQuery = text.toString(),
                sortBy = viewModel.selectedSort
            )
        }
        filterDialog()
        checkInternetAndLoad()
    }

    private fun checkInternetAndLoad() {
        if (requireContext().isConnected()) {
            viewModel.getProductList()
        } else {
            showNoInternetDialogLoop{
                viewModel.getProductList(true)
            }
        }
    }



    private fun collectState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ProductListUiState.Error -> {
                        hideLoading()
                        showAppDialog(getString(R.string.error_text), state.message)
                    }

                    ProductListUiState.Loading -> showLoading()
                    is ProductListUiState.Success -> {
                        hideLoading()
                        productListAdapter.setProductListData(state.productList ?: emptyList())
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    ProductListUiEvent.AddedFavorite ->{
                        hideLoading()
                        requireContext().openToast(
                            getString(R.string.added_to_favorites),
                            Toast.LENGTH_SHORT
                        )
                    }

                    ProductListUiEvent.RemovedFavorite ->{
                        hideLoading()
                        requireContext().openToast(
                            getString(R.string.removed_favorites),
                            Toast.LENGTH_SHORT
                        )
                    }

                    ProductListUiEvent.AddedBasket ->{
                        hideLoading()
                        requireContext().openToast(
                            getString(R.string.added_to_basket),
                            Toast.LENGTH_SHORT
                        )
                    }

                }
            }
        }
    }

    private fun filterDialog() {
        binding.txtSelectFilter.setOnClickListener {
            val allProducts = viewModel.fullProductList
            val brandList = allProducts
                .mapNotNull { (it as? ProductListViewItem.ItemProductListViewItem)?.product?.brand }
                .distinct()
            val modelList = allProducts
                .mapNotNull { (it as? ProductListViewItem.ItemProductListViewItem)?.product?.model }
                .distinct()

            FilterDialogFragment(
                brandList = brandList,
                modelList = modelList,
                selectedBrands = viewModel.selectedBrands,
                selectedModels = viewModel.selectedModels,
                selectedSort = viewModel.selectedSort
            ) { brands, models, sort ->
                viewModel.selectedBrands = brands
                viewModel.selectedModels = models
                viewModel.selectedSort = sort

                viewModel.filterProducts(
                    selectedBrands = viewModel.selectedBrands,
                    selectedModels = viewModel.selectedModels,
                    searchQuery = binding.edTxtSearch.text.toString(),
                    sortBy = viewModel.selectedSort
                )
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
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun adapterOnClicks() {
        productListAdapter.onClick = { onClick ->
            when (onClick) {
                is OnClicksProduct.OnClickAddToCart -> viewModel.addToCart(onClick.product)
                is OnClicksProduct.OnClickAddToFavorite -> {
                    viewModel.toggleFavorite(onClick.isFavorite, onClick.id)
                    productListAdapter.updateItem(onClick.position, onClick.isFavorite)
                }

                is OnClicksProduct.OnClickRoot -> findNavController().navigate(
                    ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(
                        onClick.product
                    )
                )
            }
        }
    }

    override fun setupToolbar() {
        configureToolbar(getString(R.string.e_market), false)
    }
}
