package com.example.ecommerceapp.presentation.ui.product.list.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.core.widget.addTextChangedListener
import com.example.ecommerceapp.databinding.DialogFilterBinding
import com.example.ecommerceapp.presentation.ui.product.list.SortBy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterDialogFragment(
    private val brandList: List<String>,
    private val modelList: List<String>,
    private val selectedBrands: List<String>,
    private val selectedModels: List<String>,
    private val selectedSort: SortBy?,
    private val onApplyFilter: (selectedBrands: List<String>, selectedModels: List<String>, sortBy: SortBy?) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    private val brandCheckBoxes = mutableListOf<CheckBox>()
    private val modelCheckBoxes = mutableListOf<CheckBox>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBrandCheckboxes()
        setupModelCheckboxes()
        setupSortSpinner()
        setupListeners()
    }

    private fun setupBrandCheckboxes() {
        binding.brandContainer.removeAllViews()
        brandList.forEach { brand ->
            val cb = CheckBox(requireContext()).apply {
                text = brand
                isChecked = selectedBrands.contains(brand)
            }
            binding.brandContainer.addView(cb)
            brandCheckBoxes.add(cb)
        }

        binding.brandSearchEditText.addTextChangedListener { query ->
            binding.brandContainer.removeAllViews()
            brandCheckBoxes.filter { it.text.contains(query.toString(), ignoreCase = true) }
                .forEach { binding.brandContainer.addView(it) }
        }
    }

    private fun setupModelCheckboxes() {
        binding.modelContainer.removeAllViews()
        modelList.forEach { model ->
            val cb = CheckBox(requireContext()).apply {
                text = model
                isChecked = selectedModels.contains(model)
            }
            binding.modelContainer.addView(cb)
            modelCheckBoxes.add(cb)
        }

        binding.modelSearchEditText.addTextChangedListener { query ->
            binding.modelContainer.removeAllViews()
            modelCheckBoxes.filter { it.text.contains(query.toString(), ignoreCase = true) }
                .forEach { binding.modelContainer.addView(it) }
        }
    }

    private fun setupSortSpinner() {
        val options = listOf("Fiyat Artan", "Fiyat Azalan", "Yeni", "Eski")
        binding.sortSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)

        selectedSort?.let {
            binding.sortSpinner.setSelection(
                when (it) {
                    SortBy.PRICE_ASC -> 0
                    SortBy.PRICE_DESC -> 1
                    SortBy.DATE_NEWEST -> 2
                    SortBy.DATE_OLDEST -> 3
                }
            )
        }
    }

    private fun setupListeners() {
        binding.applyFilterButton.setOnClickListener {
            val selectedBrands = brandCheckBoxes.filter { it.isChecked }.map { it.text.toString() }
            val selectedModels = modelCheckBoxes.filter { it.isChecked }.map { it.text.toString() }
            val selectedSort = when (binding.sortSpinner.selectedItem as String) {
                "Fiyat Artan" -> SortBy.PRICE_ASC
                "Fiyat Azalan" -> SortBy.PRICE_DESC
                "Yeni" -> SortBy.DATE_NEWEST
                "Eski" -> SortBy.DATE_OLDEST
                else -> null
            }
            onApplyFilter(selectedBrands, selectedModels, selectedSort)
            dismiss()
        }

        binding.clearFiltersButton.setOnClickListener {
            brandCheckBoxes.forEach { it.isChecked = false }
            modelCheckBoxes.forEach { it.isChecked = false }
            binding.brandSearchEditText.text.clear()
            binding.modelSearchEditText.text.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
