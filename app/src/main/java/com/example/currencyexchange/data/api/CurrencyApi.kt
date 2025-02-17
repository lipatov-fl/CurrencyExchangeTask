package com.example.currencyexchange.data.api

import com.example.currencyexchange.data.models.CurrencyResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface CurrencyApi {
    @GET("latest.js")
    fun getLatestRates(): Single<CurrencyResponse>
}