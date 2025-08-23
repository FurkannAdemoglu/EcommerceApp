package com.example.ecommerceapp.presentation.ui.product.list.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import com.example.ecommerceapp.R
import com.example.ecommerceapp.presentation.ui.product.list.SortBy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterDialogFragment(
    private val brandList: List<String>,
    private val modelList: List<String>,
    private val selectedBrands: List<String>, // Önceden seçilenleri al
    private val selectedModels: List<String>,
    private val selectedSort: SortBy?,
    private val onApplyFilter: (selectedBrands: List<String>, selectedModels: List<String>, sortBy: SortBy?) -> Unit
) : BottomSheetDialogFragment() {

    private val brandCheckBoxes = mutableListOf<CheckBox>()
    private val modelCheckBoxes = mutableListOf<CheckBox>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filter, container, false)

        val brandSearch = view.findViewById<EditText>(R.id.brandSearchEditText)
        val brandContainer = view.findViewById<LinearLayout>(R.id.brandContainer)
        val modelSearch = view.findViewById<EditText>(R.id.modelSearchEditText)
        val modelContainer = view.findViewById<LinearLayout>(R.id.modelContainer)
        val sortSpinner = view.findViewById<Spinner>(R.id.sortSpinner)
        val applyButton = view.findViewById<Button>(R.id.applyFilterButton)
        val clearButton = view.findViewById<Button>(R.id.clearFiltersButton)

        // Brand checkbox ekle ve önceden seçilenleri işaretle
        brandList.forEach { brand ->
            val cb = CheckBox(requireContext()).apply {
                text = brand
                isChecked = selectedBrands.contains(brand)
            }
            brandContainer.addView(cb)
            brandCheckBoxes.add(cb)
        }

        // Model checkbox ekle ve önceden seçilenleri işaretle
        modelList.forEach { model ->
            val cb = CheckBox(requireContext()).apply {
                text = model
                isChecked = selectedModels.contains(model)
            }
            modelContainer.addView(cb)
            modelCheckBoxes.add(cb)
        }

        // Brand search
        brandSearch.addTextChangedListener { query ->
            brandContainer.removeAllViews()
            brandCheckBoxes.filter { it.text.contains(query.toString(), ignoreCase = true) }
                .forEach { brandContainer.addView(it) }
        }

        // Model search
        modelSearch.addTextChangedListener { query ->
            modelContainer.removeAllViews()
            modelCheckBoxes.filter { it.text.contains(query.toString(), ignoreCase = true) }
                .forEach { modelContainer.addView(it) }
        }

        // Sort Spinner
        sortSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            listOf("Fiyat Artan", "Fiyat Azalan", "Yeni", "Eski"))
        selectedSort?.let {
            sortSpinner.setSelection(
                when(it) {
                    SortBy.PRICE_ASC -> 0
                    SortBy.PRICE_DESC -> 1
                    SortBy.DATE_NEWEST -> 2
                    SortBy.DATE_OLDEST -> 3
                }
            )
        }

        // Apply button
        applyButton.setOnClickListener {
            val selectedBrands = brandCheckBoxes.filter { it.isChecked }.map { it.text.toString() }
            val selectedModels = modelCheckBoxes.filter { it.isChecked }.map { it.text.toString() }
            val selectedSort = when(sortSpinner.selectedItem as String) {
                "Fiyat Artan" -> SortBy.PRICE_ASC
                "Fiyat Azalan" -> SortBy.PRICE_DESC
                "Yeni" -> SortBy.DATE_NEWEST
                "Eski" -> SortBy.DATE_OLDEST
                else -> null
            }
            onApplyFilter(selectedBrands, selectedModels, selectedSort)
            dismiss()
        }

        // Clear all button
        clearButton.setOnClickListener {
            brandCheckBoxes.forEach { it.isChecked = false }
            modelCheckBoxes.forEach { it.isChecked = false }
            brandSearch.text.clear()
            modelSearch.text.clear()
        }

        return view
    }
}

