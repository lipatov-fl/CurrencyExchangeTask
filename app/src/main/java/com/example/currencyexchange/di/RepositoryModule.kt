package com.example.currencyexchange.di

import com.example.currencyexchange.data.api.CurrencyApi
import com.example.currencyexchange.data.repository.CurrencyRepositoryImpl
import com.example.currencyexchange.domain.repository.CurrencyRepository
import com.example.currencyexchange.domain.usecase.GetCurrencyRatesUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideCurrencyRepository(api: CurrencyApi): CurrencyRepository {
        return CurrencyRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGetCurrencyRatesUseCase(repository: CurrencyRepository): GetCurrencyRatesUseCase {
        return GetCurrencyRatesUseCase(repository)
    }
}