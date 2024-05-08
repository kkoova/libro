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

class MyDialogFragment : DialogFragment() {

    private var positiveText: String? = null
    private var negativeText: String? = null
    private var mainText: String? = null
    private var noMainText: String? = null
    private var positiveAction: (() -> Unit)? = null
    private var negativeAction: (() -> Unit)? = null

    fun setButtons(
        positiveText: String,
        negativeText: String,
        mainText: String,
        noMainText: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        this.positiveText = positiveText
        this.negativeText = negativeText
        this.mainText = mainText
        this.noMainText = noMainText
        this.positiveAction = positiveAction
        this.negativeAction = negativeAction
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.card_main, container, false)

        view.findViewById<TextView>(R.id.mainText).text = mainText
        view.findViewById<TextView>(R.id.noMainText).text = noMainText

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.yesBtn)
        val noButton = view.findViewById<Button>(R.id.noBtn)

        yesButton.text = positiveText
        noButton.text = negativeText

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