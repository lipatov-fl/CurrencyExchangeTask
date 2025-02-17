package com.example.currencyexchange.presentation.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.data.currencymodel.CurrencySymbols
import com.example.currencyexchange.databinding.ItemConverterCurrencySelectorBinding

class ConverterCurrencyAdapter(
    var currencies: List<String>,
    var balances: Map<String, Double>,
    var rates: Map<String, Double>,
    private val currencySymbols: Map<String, String> = CurrencySymbols.symbols,
    var onAmountChanged: ((Double) -> Unit)? = null
) : RecyclerView.Adapter<ConverterCurrencyAdapter.ConverterCurrencyViewHolder>() {

    var selectedCurrency: String? = null
    var selectedCurrencySymbol: String? = null
    private var currentAmountInputText: String = ""

    inner class ConverterCurrencyViewHolder(private val binding: ItemConverterCurrencySelectorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: String) {
            if (selectedCurrency != currency) {
                currentAmountInputText = ""
                binding.amountInputEditText.setText("")
            } else {
                binding.amountInputEditText.setText(currentAmountInputText)
            }

            binding.selectedCurrencyTextView.text = currency

            val balance = balances[currency] ?: 0.0
            val formattedBalance = String.format("%.2f", balance)
            binding.userBalanceTextView.text = formattedBalance

            val balanceSymbol = currencySymbols[currency] ?: currency
            binding.userBalanceIconTextView.text = balanceSymbol

            val rubToSelectedCurrencyRate = rates[selectedCurrency] ?: 0.0
            val rubToConverterCurrencyRate = rates[currency] ?: 0.0

            val crossRate = if (rubToSelectedCurrencyRate != 0.0) {
                rubToConverterCurrencyRate / rubToSelectedCurrencyRate
            } else {
                0.0
            }

            Log.d(
                "ConverterCurrencyAdapter",
                "Selected Currency: $selectedCurrency, Rate: $rubToSelectedCurrencyRate"
            )
            Log.d(
                "ConverterCurrencyAdapter",
                "Converter Currency: $currency, Rate: $rubToConverterCurrencyRate"
            )
            Log.d("ConverterCurrencyAdapter", "Cross Rate: $crossRate")

            val formattedRate = String.format("%.2f", crossRate)
            binding.currencyRateTextView.text = formattedRate
            binding.currencyRateIconTextView.text = selectedCurrencySymbol ?: balanceSymbol
            val selectedCurrencySymbol = currencySymbols[currency] ?: currency
            binding.conversionLabelIconTextView.text = selectedCurrencySymbol
            binding.amountInputEditText.setText(currentAmountInputText)
            selectedCurrency = currency
            binding.amountInputEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val inputText = s?.toString() ?: ""
                    currentAmountInputText = inputText
                    val amount = inputText.toDoubleOrNull() ?: 0.0
                    onAmountChanged?.invoke(amount)
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConverterCurrencyViewHolder {
        val binding = ItemConverterCurrencySelectorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConverterCurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConverterCurrencyViewHolder, position: Int) {
        holder.bind(currencies[position])
    }

    override fun getItemCount(): Int = currencies.size

    fun setAmountInputText(amount: Double?) {
        currentAmountInputText = if (amount == null) {
            ""
        } else {
            String.format("%.2f", amount)
        }
        notifyDataSetChanged()
    }
}