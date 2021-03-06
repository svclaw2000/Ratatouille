package com.kdjj.presentation.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.kdjj.presentation.databinding.DialogConfirmBinding
import com.kdjj.presentation.model.ConfirmDialogModel

internal object ConfirmDialogBuilder {

    fun create(
        context: Context,
        title: String,
        content: String,
        showCancel: Boolean = false,
        onCancelListener: (() -> Unit)? = null,
        onConfirmListener: () -> Unit
    ) {
        val binding = DialogConfirmBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.model = ConfirmDialogModel(
            dialog,
            title,
            content,
            onConfirmListener,
            showCancel,
            onCancelListener
        )

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}