package com.example.currencyexchange.domain.usecase

import com.example.currencyexchange.domain.models.CurrencyModel
import com.example.currencyexchange.domain.repository.CurrencyRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetCurrencyRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    fun execute(): Observable<CurrencyModel> {
        return repository.getRatesFlow()
    }
}