package com.example.ecommerceapp.base

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerceapp.utils.isConnected

abstract class BaseActivity : AppCompatActivity() {

    fun showNoInternetDialog(onRetry: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("İnternet Yok")
            .setMessage("Lütfen internet bağlantınızı kontrol edin.")
            .setCancelable(false)
            .setPositiveButton("Tekrar Dene") { _, _ ->
                onRetry()
            }
            .show()
    }
}