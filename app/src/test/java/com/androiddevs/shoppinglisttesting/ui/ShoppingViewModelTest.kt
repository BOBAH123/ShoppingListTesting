package com.androiddevs.shoppinglisttesting.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.shoppinglisttesting.MainCoroutineRule
import com.androiddevs.shoppinglisttesting.getOrAwaitValue
import com.androiddevs.shoppinglisttesting.other.Constants
import com.androiddevs.shoppinglisttesting.other.Status
import com.androiddevs.shoppinglisttesting.repository.FakeShoppingRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShoppingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setup() {
        viewModel = ShoppingViewModel(FakeShoppingRepository())
    }

    @Test
    fun `insert shopping item with empty field, returns error`() {
        viewModel.insertShoppingItem("name", "", "2.0")

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValue()
        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long name, field returns error`() {
        val string = buildString {
            repeat(Constants.MAX_NAME_LENGTH + 1) {
                append("n")
            }
        }
        viewModel.insertShoppingItem(string, "2", "2.0")

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValue()
        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long price, field returns error`() {
        val string = buildString {
            repeat(Constants.MAX_PRICE_LENGTH + 1) {
                append(1)
            }
        }
        viewModel.insertShoppingItem("name", "2", string)

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValue()
        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too big amount, field returns error`() {
        viewModel.insertShoppingItem("name", "9999999999999999999999", "2.0")

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValue()
        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item valid inputs, field returns success`() {
        viewModel.insertShoppingItem("name", "2", "2.0")

        val result = viewModel.insertShoppingItemStatus.getOrAwaitValue()
        assertThat(result.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `is image url empties after successful inset, returns success`() {
        viewModel.setCurImageUrl("ImageURL")
        viewModel.insertShoppingItem("name", "2", "2.0")

        val result = viewModel.curImageUrl.getOrAwaitValue()
        assertThat(result).isEmpty()
    }
}