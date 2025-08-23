package com.example.ecommerceapp.presentation.ui.product.list.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.ecommerceapp.R
import com.example.ecommerceapp.presentation.ui.product.list.SortBy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterSortDialogFragment(
    private val brandList: List<String>,
    private val modelList: List<String>,
    private val onApplyFilter: (brand: String?, model: String?, sortBy: SortBy?) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filter, container, false)
        val brandSpinner = view.findViewById<Spinner>(R.id.brandSpinner)
        val modelSpinner = view.findViewById<Spinner>(R.id.modelSpinner)
        val sortSpinner = view.findViewById<Spinner>(R.id.sortSpinner)
        val applyButton = view.findViewById<Button>(R.id.applyFilterButton)

        brandSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brandList)
        modelSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modelList)
        sortSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            listOf("Fiyat Artan", "Fiyat Azalan", "Yeni", "Eski"))

        applyButton.setOnClickListener {
            val selectedBrand = brandSpinner.selectedItem as? String
            val selectedModel = modelSpinner.selectedItem as? String
            val selectedSort = when(sortSpinner.selectedItem as String) {
                "Fiyat Artan" -> SortBy.PRICE_ASC
                "Fiyat Azalan" -> SortBy.PRICE_DESC
                "Yeni" -> SortBy.DATE_NEWEST
                "Eski" -> SortBy.DATE_OLDEST
                else -> null
            }
            onApplyFilter(selectedBrand, selectedModel, selectedSort)
            dismiss()
        }

        return view
    }
}