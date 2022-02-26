package com.kdjj.presentation.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.kdjj.presentation.R

internal class CustomProgressDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}