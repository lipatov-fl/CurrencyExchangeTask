package com.example.currencyexchange.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencyexchange.domain.usecase.GetCurrencyRatesUseCase
import com.example.currencyexchange.presentation.viewmodel.CurrencyViewModel
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    fun provideCurrencyViewModelFactory(
        getCurrencyRatesUseCase: GetCurrencyRatesUseCase
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
                    return CurrencyViewModel(getCurrencyRatesUseCase) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}