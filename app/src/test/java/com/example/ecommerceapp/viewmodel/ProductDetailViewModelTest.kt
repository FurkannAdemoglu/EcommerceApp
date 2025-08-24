package com.example.ecommerceapp.viewmodel

import com.example.ecommerceapp.domain.usecase.basket.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.basket.GetBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.favorite.AddFavoriteProductUseCase
import com.example.ecommerceapp.domain.usecase.favorite.RemoveFavoriteUseCase
import com.example.ecommerceapp.presentation.ui.product.detail.ProductDetailUiState
import com.example.ecommerceapp.presentation.ui.product.detail.ProductDetailViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.utils.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val addFavoriteProductUseCase: AddFavoriteProductUseCase = mockk()
    private val removeFavoriteUseCase: RemoveFavoriteUseCase = mockk()
    private val addToBasketProductUseCase: AddToBasketProductUseCase = mockk()
    private val getBasketProductUseCase: GetBasketProductUseCase = mockk()

    private lateinit var viewModel: ProductDetailViewModel

    private val testProduct =   Product(
        id = "1",
        name = "iPhone 14",
        price = "999",
        brand = "Apple",
        model = "14",
        createdAt = "2023-01-01T10:00:00Z",
        description = "Test description",
        image = "test_image.jpg"
    )

    private val testFavoriteProduct = testProduct.copy(isFavorite = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { getBasketProductUseCase() } returns flow { emit(Resource.Success(emptyList<CartProduct>())) }

        viewModel = ProductDetailViewModel(
            addFavoriteProductUseCase,
            removeFavoriteUseCase,
            addToBasketProductUseCase,
            getBasketProductUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Empty`() {
        assertEquals(ProductDetailUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `toggleFavorite should add product to favorites when not favorite`() = runTest {
        coEvery { addFavoriteProductUseCase(any()) } returns flow {
            emit(Resource.Loading)
            emit(Resource.Success(Unit))
        }

        viewModel.toggleFavorite(testProduct)
        advanceUntilIdle()

        assertEquals(ProductDetailUiState.AddedFavorite, viewModel.uiState.value)
        coVerify { addFavoriteProductUseCase(FavoriteProduct(testProduct.id)) }
    }

    @Test
    fun `toggleFavorite should remove product from favorites when already favorite`() = runTest {
        coEvery { removeFavoriteUseCase(any()) } returns flow {
            emit(Resource.Loading)
            emit(Resource.Success(Unit))
        }

        viewModel.toggleFavorite(testFavoriteProduct)
        advanceUntilIdle()

        assertEquals(ProductDetailUiState.RemoveFavorite, viewModel.uiState.value)
        coVerify { removeFavoriteUseCase(FavoriteProduct(testFavoriteProduct.id)) }
    }

    @Test
    fun `toggleFavorite should show loading state during operation`() = runTest {
        coEvery { addFavoriteProductUseCase(any()) } returns flow {
            emit(Resource.Loading)
        }

        viewModel.toggleFavorite(testProduct)
        testDispatcher.scheduler.advanceTimeBy(100)

        assertEquals(ProductDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `toggleFavorite should show error when add favorite fails`() = runTest {
        val errorMessage = "Network error"
        coEvery { addFavoriteProductUseCase(any()) } returns flow {
            emit(Resource.Error(errorMessage))
        }

        viewModel.toggleFavorite(testProduct)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProductDetailUiState.Error)
        assertEquals(errorMessage, (state as ProductDetailUiState.Error).message)
    }

    @Test
    fun `toggleFavorite should show error when remove favorite fails`() = runTest {
        val errorMessage = "Database error"
        coEvery { removeFavoriteUseCase(any()) } returns flow {
            emit(Resource.Error(errorMessage))
        }

        viewModel.toggleFavorite(testFavoriteProduct)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProductDetailUiState.Error)
        assertEquals(errorMessage, (state as ProductDetailUiState.Error).message)
    }

    @Test
    fun `addToCart should add product to basket successfully`() = runTest {
        coEvery { addToBasketProductUseCase(any()) } returns flow {
            emit(Resource.Loading)
            emit(Resource.Success(Unit))
        }

        viewModel.addToCart(testProduct)
        advanceUntilIdle()

        assertEquals(ProductDetailUiState.AddedBasket, viewModel.uiState.value)

        val expectedCartProduct = CartProduct(
            testProduct.id,
            testProduct.name,
            testProduct.price,
            1
        )
        coVerify { addToBasketProductUseCase(expectedCartProduct) }
    }

    @Test
    fun `addToCart should show loading state during operation`() = runTest {
        coEvery { addToBasketProductUseCase(any()) } returns flow {
            emit(Resource.Loading)
        }

        viewModel.addToCart(testProduct)
        testDispatcher.scheduler.advanceTimeBy(100)

        assertEquals(ProductDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `addToCart should show error when operation fails`() = runTest {
        val errorMessage = "Cart is full"
        coEvery { addToBasketProductUseCase(any()) } returns flow {
            emit(Resource.Error(errorMessage))
        }

        viewModel.addToCart(testProduct)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProductDetailUiState.Error)
        assertEquals(errorMessage, (state as ProductDetailUiState.Error).message)
    }

    @Test
    fun `addToCart should call loadCartItemCount after successful addition`() = runTest {
        coEvery { addToBasketProductUseCase(any()) } returns flow {
            emit(Resource.Success(Unit))
        }

        viewModel.addToCart(testProduct)
        advanceUntilIdle()

        coVerify(atLeast = 2) { getBasketProductUseCase() }
    }

    @Test
    fun `multiple operations should work correctly in sequence`() = runTest {

        coEvery { addFavoriteProductUseCase(any()) } returns flow {
            emit(Resource.Success(Unit))
        }
        coEvery { addToBasketProductUseCase(any()) } returns flow {
            emit(Resource.Success(Unit))
        }

        viewModel.toggleFavorite(testProduct)
        advanceUntilIdle()

        assertEquals(ProductDetailUiState.AddedFavorite, viewModel.uiState.value)

        viewModel.addToCart(testProduct)
        advanceUntilIdle()

        assertEquals(ProductDetailUiState.AddedBasket, viewModel.uiState.value)
    }
}