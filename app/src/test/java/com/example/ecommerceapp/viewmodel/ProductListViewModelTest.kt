package com.example.ecommerceapp.presentation.ui.product.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ecommerceapp.domain.model.CartProduct
import com.example.ecommerceapp.domain.model.FavoriteProduct
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.usecase.favorite.AddFavoriteProductUseCase
import com.example.ecommerceapp.domain.usecase.basket.AddToBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.basket.GetBasketProductUseCase
import com.example.ecommerceapp.domain.usecase.product.GetProductsUseCase
import com.example.ecommerceapp.domain.usecase.favorite.RemoveFavoriteUseCase
import com.example.ecommerceapp.utils.Resource
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

@ExperimentalCoroutinesApi
class ProductListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ProductListViewModel

    private val getProductsUseCase = mockk<GetProductsUseCase>(relaxed = true)
    private val addFavoriteProductUseCase = mockk<AddFavoriteProductUseCase>(relaxed = true)
    private val removeFavoriteUseCase = mockk<RemoveFavoriteUseCase>(relaxed = true)
    private val addToBasketProductUseCase = mockk<AddToBasketProductUseCase>(relaxed = true)
    private val getBasketProductUseCase = mockk<GetBasketProductUseCase>(relaxed = true)

    private val testProducts = listOf(
        Product(
            id = "1",
            name = "iPhone 14",
            price = "999",
            brand = "Apple",
            model = "14",
            createdAt = "2023-01-01T10:00:00Z",
            description = "Test description",
            image = "test_image.jpg"
        ),
        Product(
            id = "2",
            name = "Samsung Galaxy S23",
            price = "899",
            brand = "Samsung",
            model = "Galaxy S23",
            createdAt = "2023-02-01T10:00:00Z",
            description = "Test description 2",
            image = "test_image2.jpg"
        ),
        Product(
            id = "3",
            name = "iPhone 13",
            price = "799",
            brand = "Apple",
            model = "13",
            createdAt = "2023-03-01T10:00:00Z",
            description = "Test description 3",
            image = "test_image3.jpg"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)


        MockKAnnotations.init(this, relaxUnitFun = true)

        every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(emptyList()))

        try {
            viewModel = ProductListViewModel(
                getProductsUseCase,
                addFavoriteProductUseCase,
                removeFavoriteUseCase,
                addToBasketProductUseCase,
                getBasketProductUseCase,
                ioDispatcher = UnconfinedTestDispatcher()
            )
        } catch (e: Exception) {
            println("ViewModel initialization error: ${e.message}")
            throw e
        }
    }

    @After
    fun tearDown() {

        if (::viewModel.isInitialized) {
            viewModel.dispose()
        }

        Dispatchers.resetMain()

        clearAllMocks()
    }

    @Test
    fun `getProductList should complete successfully`() {
        runTest {
            every { getProductsUseCase.invoke() } returns flowOf(Resource.Success(testProducts))
            viewModel.getProductList()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is ProductListUiState.Success)
            assertEquals(3, viewModel.fullProductList.size)
        }
    }


    @Test
    fun `getProductList should emit error state when usecase fails`() {
        runTest {
            val errorMessage = "Network error"

            coEvery { getProductsUseCase.invoke() } returns flow {
                emit(Resource.Error(errorMessage))
            }

            viewModel.getProductList()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is ProductListUiState.Error)
            assertEquals(errorMessage, (state as ProductListUiState.Error).message)
        }
    }

    @Test
    fun `toggleFavorite should add favorite when isFavorite is false`() = runTest(testDispatcher) {
        val productId = "1"
        every { addFavoriteProductUseCase.invoke(any()) } returns flowOf(Resource.Success(Unit))

        viewModel.toggleFavorite(false, productId)
        advanceUntilIdle()

        assertEquals(ProductListUiState.AddedFavorite, viewModel.uiState.value)
        verify { addFavoriteProductUseCase.invoke(FavoriteProduct(productId)) }
    }

    @Test
    fun `toggleFavorite should remove favorite when isFavorite is true`() = runTest(testDispatcher) {
        val productId = "1"
        every { removeFavoriteUseCase.invoke(any()) } returns flowOf(Resource.Success(Unit))

        viewModel.toggleFavorite(true, productId)
        advanceUntilIdle()

        assertEquals(ProductListUiState.RemoveFavorite, viewModel.uiState.value)
        verify { removeFavoriteUseCase.invoke(FavoriteProduct(productId)) }
    }

    @Test
    fun `toggleFavorite should emit error when add favorite fails`() = runTest(testDispatcher) {
        val productId = "1"
        val errorMessage = "Failed to add favorite"
        every { addFavoriteProductUseCase.invoke(any()) } returns flowOf(Resource.Error(errorMessage))

        viewModel.toggleFavorite(false, productId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProductListUiState.Error)
        assertEquals(errorMessage, (state as ProductListUiState.Error).message)
    }

    @Test
    fun `addToCart should add product to cart and update state`() = runTest(testDispatcher) {
        val product = testProducts[0]
        every { addToBasketProductUseCase.invoke(any()) } returns flowOf(Resource.Success(Unit))
        every { getBasketProductUseCase.invoke() } returns flowOf(Resource.Success(emptyList()))

        viewModel.addToCart(product)
        advanceUntilIdle()

        assertEquals(ProductListUiState.AddedBasket, viewModel.uiState.value)
        verify {
            addToBasketProductUseCase.invoke(
                CartProduct(
                    product.id,
                    product.name,
                    product.price,
                    1
                )
            )
        }
    }

    @Test
    fun `addToCart should emit error when add to cart fails`() = runTest(testDispatcher) {
        val product = testProducts[0]
        val errorMessage = "Failed to add to cart"
        every { addToBasketProductUseCase.invoke(any()) } returns flowOf(Resource.Error(errorMessage))

        viewModel.addToCart(product)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProductListUiState.Error)
        assertEquals(errorMessage, (state as ProductListUiState.Error).message)
    }

    @Test
    fun `dispose should set state to Empty`() {
        viewModel.dispose()

        assertEquals(ProductListUiState.Empty, viewModel.uiState.value)
    }


    @Test
    fun `loadNextPage should not load more items when all items are loaded`() = runTest(testDispatcher) {
        every { getProductsUseCase.invoke() } returns flowOf(Resource.Success(testProducts))
        viewModel.getProductList()
        advanceUntilIdle()

        val initialState = viewModel.uiState.value
        viewModel.loadNextPage()
        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertEquals(initialState, finalState)
    }

    @Test
    fun `toggleFavorite should emit error when remove favorite fails`() = runTest(testDispatcher) {
        val productId = "1"
        val errorMessage = "Failed to remove favorite"
        every { removeFavoriteUseCase.invoke(any()) } returns flowOf(Resource.Error(errorMessage))

        viewModel.toggleFavorite(true, productId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProductListUiState.Error)
        assertEquals(errorMessage, (state as ProductListUiState.Error).message)
    }

}