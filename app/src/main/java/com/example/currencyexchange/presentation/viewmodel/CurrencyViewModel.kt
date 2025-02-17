package com.example.currencyexchange.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyexchange.domain.models.CurrencyModel
import com.example.currencyexchange.domain.usecase.GetCurrencyRatesUseCase
import com.example.currencyexchange.data.currencymodel.CurrencySymbols
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CurrencyViewModel @Inject constructor(
    private val getCurrencyRatesUseCase: GetCurrencyRatesUseCase
) : ViewModel() {

    private val _rates = MutableLiveData<CurrencyModel>()
    val rates: LiveData<CurrencyModel> get() = _rates

    private val _balances = MutableLiveData<MutableMap<String, Double>>()
    val balances: LiveData<MutableMap<String, Double>> get() = _balances

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val disposables = CompositeDisposable()

    init {
        fetchRates()
    }

    private fun fetchRates() {
        _isLoading.value = true
        disposables.add(
            getCurrencyRatesUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ rates ->
                    _rates.value = rates
                    _isLoading.value = false
                }, { error ->
                    _errorMessage.value = error.message
                    _isLoading.value = false
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun saveBalances(context: Context, balances: MutableMap<String, Double>) {
        val sharedPreferences = context.getSharedPreferences("user_balances", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        balances.forEach { (currency, balance) ->
            editor.putFloat(currency, balance.toFloat())
        }
        editor.apply()
    }

    fun loadBalances(context: Context): MutableMap<String, Double> {
        val sharedPreferences = context.getSharedPreferences("user_balances", Context.MODE_PRIVATE)
        val balances = mutableMapOf<String, Double>()
        CurrencySymbols.symbols.keys.forEach { currency ->
            balances[currency] = sharedPreferences.getFloat(currency, 100.0f).toDouble()
        }
        _balances.value = balances
        return balances
    }

    fun getFormattedBalances(): String {
        val availableAccounts = StringBuilder()
        availableAccounts.append("Available accounts:\n")
        _balances.value?.forEach { (currency, balance) ->
            availableAccounts.append("$currency: ${String.format("%.2f", balance)}\n")
        }
        return availableAccounts.toString()
    }

    fun updateBalances(newBalances: MutableMap<String, Double>) {
        _balances.value = newBalances
    }
}