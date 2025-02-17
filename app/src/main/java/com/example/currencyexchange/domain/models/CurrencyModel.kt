package com.example.currencyexchange.domain.models

data class CurrencyModel(
    val base: String,
    val rates: Map<String, Double>
)
