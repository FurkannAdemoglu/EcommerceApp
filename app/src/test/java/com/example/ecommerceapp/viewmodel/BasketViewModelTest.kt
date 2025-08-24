package com.example.ecommerceapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.usecase.basket.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.basket.DeleteAllBasketUseCase
import com.example.ecommerceapp.domain.usecase.basket.GetBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.basket.RemoveFromBasketUseCase
import com.example.ecommerceapp.presentation.ui.basket.BasketListUiState
import com.example.ecommerceapp.presentation.ui.basket.BasketViewModel
import com.example.ecommerceapp.presentation.ui.basket.adapter.viewitem.BasketListViewItem
import com.example.ecommerceapp.utils.Resource
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BasketViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: BasketViewModel

    private val addToBasketProductUseCase = mockk<AddToBasketProductUseCase>(relaxed = true)
    private val removeToBasketProductUseCase = mockk<RemoveFromBasketUseCase>(relaxed = true)
    private val getBasketProductUseCase = mockk<GetBasketProductUseCase>(relaxed = true)
    private val deleteAllBasketUseCase = mockk<DeleteAllBasketUseCase>(relaxed = true)

    private val testCartProducts = listOf(
        CartProduct(
            id = "1",
            name = "iPhone 14",
            price = "999.0",
            quantity = 1
        ),
        CartProduct(
            id = "2",
            name = "Samsung Galaxy S23",
            price = "899.0",
            quantity = 2
        ),
        CartProduct(
            id = "3",
            name = "iPad Pro",
            price = "1299.0",
            quantity = 1
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = true)

        every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(emptyList()))
    }

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) {
            clearAllMocks()
        }
        Dispatchers.resetMain()
    }

    private fun initViewModel() {
        viewModel = BasketViewModel(
            addToBasketProductUseCase,
            removeToBasketProductUseCase,
            getBasketProductUseCase,
            deleteAllBasketUseCase
        )
    }

    @Test
    fun `init should call getCartProductList and emit empty success when basket is empty`() {
        runTest {
            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(emptyList()))

            initViewModel()
            advanceUntilIdle()

            assertEquals(BasketListUiState.EmptySuccess, viewModel.uiState.value)
            assertTrue(viewModel.productList.isEmpty())
            assertTrue(viewModel.cartList.isEmpty())
            verify { getBasketProductUseCase.invoke() }
        }
    }

    @Test
    fun `init should call getCartProductList and emit success with data when basket has items`() {
        runTest {
            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(testCartProducts))

            initViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue("State should be Success", state is BasketListUiState.Success)

            val successState = state as BasketListUiState.Success
            assertEquals(testCartProducts.size, successState.cartProductList?.size)
            assertNotNull(successState.totalPrice)

            assertEquals(testCartProducts.size, viewModel.productList.size)
            assertEquals(testCartProducts.size, viewModel.cartList.size)

            verify { getBasketProductUseCase.invoke() }
        }
    }

    @Test
    fun `init should emit error when getCartProductList fails`() {
        runTest {
            val errorMessage = "Failed to load basket"
            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Error(errorMessage))

            initViewModel()
            advanceUntilIdle()
            val state = viewModel.uiState.value
            assertTrue("State should be Error", state is BasketListUiState.Error)
            assertEquals(errorMessage, (state as BasketListUiState.Error).message)
        }
    }

    @Test
    fun `addToBasket should add product and refresh basket list`() {
        runTest {
            val newProduct = testCartProducts[0]
            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(emptyList()))
            every { addToBasketProductUseCase.invoke(newProduct) } returns flowOf(Resource.Success(Unit))

            every { getBasketProductUseCase.invoke() } returnsMany listOf(
                flowOf(Resource.Success(emptyList())),
                flowOf(Resource.Success(listOf(newProduct)))
            )

            initViewModel()
            advanceUntilIdle()

            viewModel.addToBasket(newProduct)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue("State should be Success", state is BasketListUiState.Success)

            verify { addToBasketProductUseCase.invoke(newProduct) }
            verify(atLeast = 2) { getBasketProductUseCase.invoke() } // init + refresh
        }
    }

    @Test
    fun `addToBasket should emit error when add operation fails`() {
        runTest {
            val errorMessage = "Failed to add to basket"
            val newProduct = testCartProducts[0]

            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(emptyList()))
            every { addToBasketProductUseCase.invoke(newProduct) } returns flowOf(Resource.Error(errorMessage))

            initViewModel()
            advanceUntilIdle()

            viewModel.addToBasket(newProduct)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue("State should be Error", state is BasketListUiState.Error)
            assertEquals(errorMessage, (state as BasketListUiState.Error).message)
        }
    }

    @Test
    fun `removeFromBasket should remove product and refresh basket list`() {
        runTest {

            val productToRemove = testCartProducts[0]
            val remainingProducts = testCartProducts.drop(1)

            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(testCartProducts))
            every { removeToBasketProductUseCase.invoke(productToRemove) } returns flowOf(Resource.Success(Unit))

            every { getBasketProductUseCase.invoke() } returnsMany listOf(
                flowOf(Resource.Success(testCartProducts)),
                flowOf(Resource.Success(remainingProducts))
            )

            initViewModel()
            advanceUntilIdle()

            viewModel.removeFromBasket(productToRemove)
            advanceUntilIdle()

            verify { removeToBasketProductUseCase.invoke(productToRemove) }
            verify(atLeast = 2) { getBasketProductUseCase.invoke() }
        }
    }

    @Test
    fun `removeFromBasket should emit error when remove operation fails`() {
        runTest {
            val errorMessage = "Failed to remove from basket"
            val productToRemove = testCartProducts[0]

            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(testCartProducts))
            every { removeToBasketProductUseCase.invoke(productToRemove) } returns flowOf(Resource.Error(errorMessage))

            initViewModel()
            advanceUntilIdle()

            viewModel.removeFromBasket(productToRemove)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue("State should be Error", state is BasketListUiState.Error)
            assertEquals(errorMessage, (state as BasketListUiState.Error).message)
        }
    }



    @Test
    fun `deleteAllBasket should emit error when delete operation fails`() {
        runTest {
            val errorMessage = "Failed to delete all items"
            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(testCartProducts))
            every { deleteAllBasketUseCase.invoke(any()) } returns flowOf(Resource.Error(errorMessage))

            initViewModel()
            advanceUntilIdle()

            viewModel.deleteAllBasket()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue("State should be Error", state is BasketListUiState.Error)
            assertEquals(errorMessage, (state as BasketListUiState.Error).message)
        }
    }


    @Test
    fun `productList and cartList should be populated correctly on successful basket load`() {
        runTest {
            every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(testCartProducts))

            initViewModel()
            advanceUntilIdle()

            assertEquals(testCartProducts.size, viewModel.productList.size)
            assertEquals(testCartProducts.size, viewModel.cartList.size)

            viewModel.productList.forEach { item ->
                assertTrue(item is BasketListViewItem.ItemProductBasketListViewItem)
            }

            assertEquals(testCartProducts, viewModel.cartList)
        }
    }

}