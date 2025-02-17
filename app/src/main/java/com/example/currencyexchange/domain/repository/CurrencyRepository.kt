package com.example.currencyexchange.domain.repository

import com.example.currencyexchange.domain.models.CurrencyModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface CurrencyRepository {
    fun getLatestRates(): Single<CurrencyModel>
    fun getRatesFlow(): Observable<CurrencyModel>
}