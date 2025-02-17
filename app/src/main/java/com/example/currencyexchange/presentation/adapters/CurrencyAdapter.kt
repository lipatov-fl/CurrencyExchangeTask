package com.example.currencyexchange.presentation.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.data.currencymodel.CurrencySymbols
import com.example.currencyexchange.databinding.ItemCurrencySelectorBinding

class CurrencyAdapter(
    var currencies: List<String>,
    var balances: MutableMap<String, Double>,
    var rates: Map<String, Double>,
    private val currencySymbols: Map<String, String> = CurrencySymbols.symbols,
    var selectedCurrency: String? = null,
    var onAmountChanged: ((Double) -> Unit)? = null,
    var onCurrencySelected: ((String) -> Unit)? = null
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    var converterSelectedCurrency: String? = null
    var converterSelectedCurrencySymbol: String? = null
    private var conversionLabelCurrency: String? = null
    private var currentAmountInputText: String = ""

    inner class CurrencyViewHolder(private val binding: ItemCurrencySelectorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: String) {
            if (selectedCurrency != currency) {
                currentAmountInputText = ""
                binding.amountInputEditText.setText("")
            }
            binding.selectedCurrencyTextView.text = currency

            val balance = balances[currency] ?: 0.0
            val formattedBalance = String.format("%.2f", balance)
            binding.userBalanceTextView.text = formattedBalance

            val balanceSymbol = currencySymbols[currency] ?: currency
            binding.userBalanceIconTextView.text = balanceSymbol

            val rubToSelectedCurrencyRate = rates[selectedCurrency] ?: 0.0
            val rubToConverterCurrencyRate = if (converterSelectedCurrency != null) {
                rates[converterSelectedCurrency] ?: 0.0
            } else {
                0.0
            }

            val crossRate = if (rubToSelectedCurrencyRate != 0.0) {
                rubToConverterCurrencyRate / rubToSelectedCurrencyRate
            } else {
                0.0
            }

            Log.d(
                "CurrencyAdapter",
                "Selected Currency: $selectedCurrency, Rate: $rubToSelectedCurrencyRate"
            )
            Log.d(
                "CurrencyAdapter",
                "Converter Currency: $converterSelectedCurrency, Rate: $rubToConverterCurrencyRate"
            )
            Log.d("CurrencyAdapter", "Cross Rate: $crossRate")

            val formattedRate = String.format("%.2f", crossRate)
            binding.currencyRateTextView.text = formattedRate
            binding.currencyRateIconTextView.text = converterSelectedCurrencySymbol ?: balanceSymbol

            val conversionLabelSymbol = currencySymbols[currency] ?: currency
            binding.conversionLabelIconTextView.text = conversionLabelSymbol
            binding.amountInputEditText.removeTextChangedListener(textWatcher)
            binding.amountInputEditText.setText(currentAmountInputText)
            binding.amountInputEditText.setSelection(currentAmountInputText.length)
            binding.amountInputEditText.addTextChangedListener(textWatcher)

            selectedCurrency = currency
            onCurrencySelected?.invoke(currencySymbols[currency] ?: currency)
        }

        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputText = s?.toString() ?: ""
                currentAmountInputText = inputText

                val amount = inputText.toDoubleOrNull() ?: 0.0
                onAmountChanged?.invoke(amount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding =
            ItemCurrencySelectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(currencies[position])
    }

    override fun getItemCount(): Int = currencies.size

    fun getAmountInputText(): String {
        return currentAmountInputText
    }

    fun updateConversionLabelIcon(currency: String) {
        conversionLabelCurrency = currency
        notifyDataSetChanged()
    }

    fun updateBalance(currency: String, newBalance: String) {
        val balance = newBalance.toDoubleOrNull() ?: return
        balances[currency] = balance
        notifyDataSetChanged()
    }

    fun updateConverterCurrencySymbol(symbol: String?) {
        converterSelectedCurrencySymbol = symbol
        notifyDataSetChanged()
    }
}