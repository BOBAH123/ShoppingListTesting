package com.androiddevs.shoppinglisttesting.di

import android.content.Context
import androidx.room.Room
import com.androiddevs.shoppinglisttesting.R
import com.androiddevs.shoppinglisttesting.data.local.ShoppingDao
import com.androiddevs.shoppinglisttesting.data.local.ShoppingItemDatabase
import com.androiddevs.shoppinglisttesting.data.remote.PixabayAPI
import com.androiddevs.shoppinglisttesting.other.Constants.BASE_URL
import com.androiddevs.shoppinglisttesting.other.Constants.DATABASE_NAME
import com.androiddevs.shoppinglisttesting.repository.DefaultShoppingRepository
import com.androiddevs.shoppinglisttesting.repository.ShoppingRepository
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideShoppingItemDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, ShoppingItemDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideShoppingDao(
        database: ShoppingItemDatabase
    ) = database.shoppingDao()

    @Singleton
    @Provides
    fun providesPixabayAPI(): PixabayAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PixabayAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideDefaultShoppingRepository(
        dao: ShoppingDao,
        api: PixabayAPI
    ) = DefaultShoppingRepository(dao, api) as ShoppingRepository

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions().placeholder(R.drawable.ic_image)
    )
}