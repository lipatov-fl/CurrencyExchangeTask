package com.example.currencyexchange.data.repository

import com.example.currencyexchange.data.api.CurrencyApi
import com.example.currencyexchange.domain.models.CurrencyModel
import com.example.currencyexchange.domain.repository.CurrencyRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(private val api: CurrencyApi) :
    CurrencyRepository {

    override fun getLatestRates(): Single<CurrencyModel> {
        return api.getLatestRates()
            .subscribeOn(Schedulers.io())
            .map { response ->
                CurrencyModel(response.base, response.rates)
            }
            .onErrorReturn {
                CurrencyModel("RUB", emptyMap())
            }
    }

    override fun getRatesFlow(): Observable<CurrencyModel> {
        return Observable.interval(0, 30, TimeUnit.SECONDS)
            .flatMapSingle {
                getLatestRates()
            }
    }
}