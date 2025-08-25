package com.example.ecommerceapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.utils.isConnected
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


abstract class BaseFragment<DB : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int
) : Fragment() {

    private var _binding: DB? = null

    protected val binding: DB
        get() = _binding ?: throw IllegalStateException(
            "ViewBinding erişilmeye çalışıldı ama lifecycle dışında!"
        )

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    protected fun configureToolbar(title: String, showBack: Boolean = false) {
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = title

        val navController = findNavController()
        if (showBack) {
            toolbar.navigationIcon = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_arrow_back
            )
            toolbar.setNavigationOnClickListener {
                navController.popBackStack()
            }
        } else {
            toolbar.navigationIcon = null
        }
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

    protected fun showNoInternetDialogLoop(onConnectionSuccess:(()->Unit)?) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("İnternet Bağlantısı Yok")
            .setMessage("İnternet açılınca ürünler tekrar yüklenecek.")
            .setCancelable(false)
            .setPositiveButton("Tamam") { dialogInterface, _ ->
                dialogInterface.dismiss()
                lifecycleScope.launch {
                    if (!requireContext().isConnected()) {
                        delay(500)
                        showNoInternetDialogLoop(onConnectionSuccess)
                    } else {
                        onConnectionSuccess?.invoke()
                    }
                }
            }.create()
        dialog.show()
    }
    abstract fun setupToolbar()
}
