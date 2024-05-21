package com.korobeynikova.libro

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class MyDialogEdit : DialogFragment() {
    private var positiveText: String? = null
    private var negativeText: String? = null
    private var positiveAction: ((String) -> Unit)? = null
    private var negativeAction: (() -> Unit)? = null

    fun setButtons(
        positiveText: String,
        negativeText: String,
        positiveAction: (String) -> Unit,
        negativeAction: () -> Unit
    ) {
        this.positiveText = positiveText
        this.negativeText = negativeText
        this.positiveAction = positiveAction
        this.negativeAction = negativeAction
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.card_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.yesBtn2)
        val noButton = view.findViewById<Button>(R.id.noBtn2)

        yesButton.text = positiveText
        noButton.text = negativeText

        yesButton.setOnClickListener {
            val loginEditText = view.findViewById<EditText>(R.id.loginTextText)

            positiveAction?.invoke(loginEditText.text.toString())
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