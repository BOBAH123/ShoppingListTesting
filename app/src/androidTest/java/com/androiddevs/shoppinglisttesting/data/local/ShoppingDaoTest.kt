package com.androiddevs.shoppinglisttesting.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.androiddevs.shoppinglisttesting.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@HiltAndroidTest
class ShoppingDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("test_db")
    lateinit var database: ShoppingItemDatabase
    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.shoppingDao()
    }

    @Test
    fun insertShoppingItem() = runBlockingTest {
        val item = ShoppingItem(
            "name",
            1,
            1f,
            "imageUrl",
            1
        )
        dao.insertShoppingItem(shoppingItem = item)
        val allItems = dao.observeAllShoppingItems().getOrAwaitValue()
        assertThat(allItems).contains(item)
    }

    @Test
    fun deleteShoppingItem() = runBlockingTest {
        val item = ShoppingItem(
            "name",
            1,
            1f,
            "imageUrl",
            1
        )
        dao.insertShoppingItem(item)
        dao.deleteShoppingItem(item)
        val allItems = dao.observeAllShoppingItems().getOrAwaitValue()
        assertThat(allItems).doesNotContain(item)
    }

    @Test
    fun observeTotalPriceSum() = runBlockingTest {
        val item1 = ShoppingItem(
            "name",
            2,
            1f,
            "imageUrl",
            1
        )
        val item2 = ShoppingItem(
            "name",
            4,
            10f,
            "imageUrl",
            2
        )
        val item3 = ShoppingItem(
            "name",
            3,
            5.5f,
            "imageUrl",
            3
        )
        dao.insertShoppingItem(shoppingItem = item1)
        dao.insertShoppingItem(shoppingItem = item2)
        dao.insertShoppingItem(shoppingItem = item3)
        val allItems = dao.observeTotalPrice().getOrAwaitValue()
        assertThat(allItems).isEqualTo(2 * 1f + 4 * 10f + 3 * 5.5f)
    }

    @After
    fun teardown() {
        database.close()
    }
}