package com.korobeynikova.libro

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogSearch : DialogFragment() {

    private var positiveAction: (() -> Unit)? = null
    private var negativeAction: (() -> Unit)? = null

    fun setButtons(
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        this.positiveAction = positiveAction
        this.negativeAction = negativeAction
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.card_add, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.goSearch)
        val noButton = view.findViewById<Button>(R.id.noSeach)


        yesButton.setOnClickListener {
            positiveAction?.invoke()
            dismiss()
        }

        noButton.setOnClickListener {
            negativeAction?.invoke()
            dismiss()
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}