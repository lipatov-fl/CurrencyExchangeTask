package com.example.currencyexchange.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.currencyexchange.app.App
import com.example.currencyexchange.data.currencymodel.CurrencySymbols
import com.example.currencyexchange.databinding.ActivityMainBinding
import com.example.currencyexchange.presentation.adapters.ConverterCurrencyAdapter
import com.example.currencyexchange.presentation.adapters.CurrencyAdapter
import com.example.currencyexchange.presentation.viewmodel.CurrencyViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingAnimationView: LottieAnimationView
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CurrencyViewModel
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var converterCurrencyAdapter: ConverterCurrencyAdapter
    private var userBalances = mutableMapOf<String, Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingAnimationView = binding.loadingAnimationView
        (application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[CurrencyViewModel::class.java]
        userBalances = viewModel.loadBalances(this)

        currencyAdapter = CurrencyAdapter(
            currencies = emptyList(),
            balances = userBalances,
            rates = emptyMap()
        ).apply {
            onAmountChanged = { amount ->
                val fromCurrency = selectedCurrency
                val toCurrency = converterCurrencyAdapter.selectedCurrency
                val rate = converterCurrencyAdapter.rates[toCurrency]

                if (fromCurrency != null && toCurrency != null && rate != null) {
                    val convertedAmount = amount * rate
                    if (amount != 0.0) {
                        converterCurrencyAdapter.setAmountInputText(convertedAmount)
                    }
                }
            }
            onCurrencySelected = { symbol ->
                converterCurrencyAdapter.selectedCurrencySymbol = symbol
                converterCurrencyAdapter.notifyDataSetChanged()
            }
        }

        converterCurrencyAdapter = ConverterCurrencyAdapter(
            currencies = emptyList(),
            balances = userBalances,
            rates = emptyMap()
        )

        binding.currencySelectionRView.adapter = currencyAdapter
        binding.currencySelectionRView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.converterCurrencySelectionRView.adapter = converterCurrencyAdapter
        binding.converterCurrencySelectionRView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val snapHelper1 = LinearSnapHelper()
        val snapHelper2 = LinearSnapHelper()
        snapHelper1.attachToRecyclerView(binding.currencySelectionRView)
        snapHelper2.attachToRecyclerView(binding.converterCurrencySelectionRView)

        viewModel.rates.observe(this, Observer { rates ->
            val currencies = rates.rates.keys.toList()
            currencyAdapter.currencies = currencies
            converterCurrencyAdapter.currencies = currencies
            currencyAdapter.rates = rates.rates
            converterCurrencyAdapter.rates = rates.rates

            if (currencies.isNotEmpty()) {
                val initialCurrency = currencies[0]
                currencyAdapter.updateConversionLabelIcon(initialCurrency)
            }

            currencyAdapter.notifyDataSetChanged()
            converterCurrencyAdapter.notifyDataSetChanged()
        })

        viewModel.balances.observe(this, Observer { balances ->
            currencyAdapter.balances = balances
            converterCurrencyAdapter.balances = balances
            currencyAdapter.notifyDataSetChanged()
            converterCurrencyAdapter.notifyDataSetChanged()
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                loadingAnimationView.visibility = View.VISIBLE
                loadingAnimationView.playAnimation()
                binding.toolbar.visibility = View.GONE
                binding.downArrowImg.visibility = View.GONE
            } else {
                loadingAnimationView.visibility = View.GONE
                loadingAnimationView.cancelAnimation()
                binding.toolbar.visibility = View.VISIBLE
                binding.downArrowImg.visibility = View.VISIBLE
            }
        })

        binding.currencySelectionRView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                    val selectedCurrency = currencyAdapter.currencies[firstVisibleItemPosition]
                    currencyAdapter.selectedCurrency = selectedCurrency
                    currencyAdapter.notifyDataSetChanged()
                    val currencySymbol = CurrencySymbols.symbols[selectedCurrency] ?: selectedCurrency
                    binding.currencyConversionIconToolbar.text = currencySymbol
                }
            }
        })

        binding.converterCurrencySelectionRView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                    val selectedCurrency = converterCurrencyAdapter.currencies[firstVisibleItemPosition]

                    converterCurrencyAdapter.selectedCurrency = selectedCurrency
                    currencyAdapter.converterSelectedCurrency = selectedCurrency

                    val currencySymbol = CurrencySymbols.symbols[selectedCurrency] ?: selectedCurrency
                    binding.conversionLabelIconToolbar.text = currencySymbol

                    currencyAdapter.updateConverterCurrencySymbol(currencySymbol)

                    val rubToSelectedCurrencyRate = viewModel.rates.value?.rates?.get(currencyAdapter.selectedCurrency) ?: 0.0
                    val rubToConverterCurrencyRate = viewModel.rates.value?.rates?.get(selectedCurrency) ?: 0.0
                    val crossRate = if (rubToSelectedCurrencyRate != 0.0) {
                        rubToConverterCurrencyRate / rubToSelectedCurrencyRate
                    } else {
                        0.0
                    }

                    val formattedRate = String.format("%.2f", crossRate)
                    binding.currencyRateToolbar.text = formattedRate
                    converterCurrencyAdapter.notifyDataSetChanged()
                    currencyAdapter.notifyDataSetChanged()
                }
            }
        })

        binding.toolbarExchangeButton.setOnClickListener {
            val fromCurrency = currencyAdapter.selectedCurrency ?: return@setOnClickListener
            val toCurrency = converterCurrencyAdapter.selectedCurrency ?: return@setOnClickListener
            val amountText = currencyAdapter.getAmountInputText()
            val amount = amountText.toDoubleOrNull() ?: return@setOnClickListener

            if (userBalances[fromCurrency] ?: 0.0 < amount) {
                Toast.makeText(this, "Insufficient funds", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rate = converterCurrencyAdapter.rates[toCurrency] ?: return@setOnClickListener
            val convertedAmount = amount * rate
            userBalances[fromCurrency] = (userBalances[fromCurrency] ?: 0.0) - amount
            userBalances[toCurrency] = (userBalances[toCurrency] ?: 0.0) + convertedAmount
            viewModel.updateBalances(userBalances)
            currencyAdapter.balances = userBalances
            converterCurrencyAdapter.balances = userBalances
            currencyAdapter.notifyDataSetChanged()
            converterCurrencyAdapter.notifyDataSetChanged()

            val updatedBalance = userBalances[fromCurrency] ?: 0.0
            val formattedBalance = String.format("%.2f", updatedBalance)
            currencyAdapter.updateBalance(fromCurrency, formattedBalance)

            val receiptMessage = "Receipt to account: $convertedAmount\nAvailable balance: ${userBalances[toCurrency] ?: 0.0}"
            val availableAccounts = viewModel.getFormattedBalances()
            showReceiptDialog("$receiptMessage\n\n$availableAccounts")
        }
    }

    private fun showReceiptDialog(message: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Transaction Receipt")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onDestroy() {
        viewModel.saveBalances(this, userBalances)
        super.onDestroy()
    }
}