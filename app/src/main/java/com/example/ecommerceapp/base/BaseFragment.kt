package com.example.ecommerceapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


abstract class BaseFragment<DB : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int
) : Fragment() {

    private var _binding: DB? = null
    protected val binding get() = _binding!!
    private var progressDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    protected fun showAppDialog(
        title: String,
        message: String,
        positiveText: String = "Tamam",
        negativeText: String? = null,
        cancelable: Boolean = true,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
            .apply {
                if (!negativeText.isNullOrEmpty()) {
                    setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
                }
            }
            .show()
    }

    fun showLoading() {
        if (progressDialog == null) {
            progressDialog = AlertDialog.Builder(requireContext())
                .setView(ProgressBar(requireContext()))
                .setCancelable(false)
                .create()
        }
        progressDialog?.show()
    }

    fun hideLoading() {
        progressDialog?.dismiss()
    }
}