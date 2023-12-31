package bsuir.labs.labwork_1_v8

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerFromCurrency: Spinner
    private lateinit var spinnerToCurrency: Spinner
    private lateinit var editTextAmount: EditText
    private lateinit var buttonConvert: Button
    private lateinit var textViewResult: TextView
    private lateinit var viewModel: CurrencyConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency)
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency)
        editTextAmount = findViewById(R.id.editTextAmount)
        buttonConvert = findViewById(R.id.buttonConvert)
        textViewResult = findViewById(R.id.textViewResult)

        val currencies = listOf("USD", "RUB", "BYN")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFromCurrency.adapter = adapter
        spinnerToCurrency.adapter = adapter

        viewModel = ViewModelProvider(this)[CurrencyConverterViewModel::class.java]

        spinnerFromCurrency.setSelection(currencies.indexOf("USD"))
        spinnerToCurrency.setSelection(currencies.indexOf("BYN"))

        spinnerFromCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFromCurrency = currencies[position]
                val selectedToCurrency = spinnerToCurrency.selectedItem.toString()
                if (selectedFromCurrency == selectedToCurrency) {
                    val toCurrencyPosition = (position + 1) % currencies.size
                    spinnerToCurrency.setSelection(toCurrencyPosition)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerToCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedToCurrency = currencies[position]
                val selectedFromCurrency = spinnerFromCurrency.selectedItem.toString()
                if (selectedToCurrency == selectedFromCurrency) {
                    val fromCurrencyPosition = (position + 1) % currencies.size
                    spinnerFromCurrency.setSelection(fromCurrencyPosition)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        buttonConvert.setOnClickListener {
            convertCurrency()
        }

        textViewResult.text = viewModel.result
    }

    @SuppressLint("SetTextI18n")
    private fun convertCurrency() {
        val amount = editTextAmount.text.toString().toDoubleOrNull()
        val fromCurrency = spinnerFromCurrency.selectedItem.toString()
        val toCurrency = spinnerToCurrency.selectedItem.toString()

        if (amount != null) {
            val exchangeRates = mapOf(
                "USD" to mapOf(
                    "RUB" to 100.12 / 1000.12,
                    "BYN" to 100.11 / 500.14
                ),
                "RUB" to mapOf(
                    "USD" to 1000.12 / 100.12,
                    "BYN" to 1.0 / 100.0
                ),
                "BYN" to mapOf(
                    "USD" to 500.14 / 100.11,
                    "RUB" to 100.0 / 1.0
                )
            )
            if (exchangeRates.containsKey(fromCurrency) && exchangeRates[fromCurrency]?.containsKey(
                    toCurrency
                ) == true
            ) {
                val exchangeRate = exchangeRates[fromCurrency]?.get(toCurrency) ?: 1.0
                val convertedAmount = amount * exchangeRate
                viewModel.result = "Результат: $amount $fromCurrency = $convertedAmount $toCurrency"
                textViewResult.text = viewModel.result
            } else {
                viewModel.result = "Не удалось выполнить конвертацию"
                textViewResult.text = viewModel.result
            }
        } else {
            viewModel.result = "Введите корректную сумму"
            textViewResult.text = viewModel.result
        }
    }
}

class CurrencyConverterViewModel : ViewModel() {
    var result: String = ""
}
