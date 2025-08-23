package com.example.ecommerceapp.presentation.ui.basket

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.base.BaseFragment
import com.example.ecommerceapp.databinding.FragmentBasketBinding
import com.example.ecommerceapp.presentation.ui.basket.adapter.BasketAdapter
import com.example.ecommerceapp.presentation.ui.basket.adapter.click.OnClickBasketButton
import com.example.ecommerceapp.presentation.ui.basket.adapter.viewitem.BasketListViewItem
import com.example.ecommerceapp.utils.EmptyView
import com.example.netflixcloneapp.utils.BottomNavigationAnnotation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@BottomNavigationAnnotation
class BasketFragment : BaseFragment<FragmentBasketBinding>(R.layout.fragment_basket) {
    private val viewModel: BasketViewModel by viewModels()
    private val basketAdapter by lazy { BasketAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectState()
        setAdapter()
        adapterOnClicks()

    }

    private fun collectState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is BasketListUiState.Error -> {

                    }

                    BasketListUiState.Loading -> {

                    }

                    is BasketListUiState.Success -> {
                        basketAdapter.setBasketListData(state.cartProductList ?: emptyList())
                        binding.txtComplete.setOnClickListener {
                            viewModel.deleteAllBasket()
                        }
                    }

                    BasketListUiState.DeleteSuccess -> {
                        Toast.makeText(requireContext(), "Siparişiniz Alındı", Toast.LENGTH_LONG)
                            .show()
                    }

                    BasketListUiState.EmptySuccess -> {
                        basketAdapter.setBasketListData(
                            listOf(
                                BasketListViewItem.ItemBasketEmptyViewItem(
                                    EmptyView(
                                        getString(
                                            R.string.empty_basket_description,
                                        ),
                                        getString(R.string.empty_basket_button)
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        binding.recyclerViewProductList.apply {
            this.adapter = basketAdapter
        }
    }

    private fun adapterOnClicks() {
        basketAdapter.onClick = { onClick ->
            when (onClick) {
                is OnClickBasketButton.OnClickMinus -> {
                    viewModel.removeFromBasket(onClick.cartProduct)
                }

                is OnClickBasketButton.OnClickPlus -> {
                    viewModel.addToBasket(onClick.cartProduct)
                }
            }
        }
        basketAdapter.onClickEmptyButton={
            findNavController().popBackStack(R.id.productListFragment, false)
        }
    }

}