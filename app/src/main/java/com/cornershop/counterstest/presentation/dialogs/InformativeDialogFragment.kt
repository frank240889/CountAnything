package com.cornershop.counterstest.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InformativeDialogFragment: DialogFragment() {

    companion object {
        const val TITLE = "title"
        const val MESSAGE = "message"
        const val POSITIVE_BUTTON_TEXT = "positive_button_text"
        const val NEGATIVE_BUTTON_TEXT = "negative_button_text"


        fun newInstance(
            title: String = "",
            message: String,
            positiveButtonText: String,
            negativeButtonText: String = "",
        ) = InformativeDialogFragment().apply {
            arguments = Bundle().apply {
                putString(TITLE, title)
                putString(MESSAGE, message)
                putString(POSITIVE_BUTTON_TEXT, positiveButtonText)
                putString(NEGATIVE_BUTTON_TEXT, negativeButtonText)
            }
        }
    }

    private val title: String by lazy {
        requireArguments().getString(TITLE)!!
    }

    private val message: String by lazy {
        requireArguments().getString(MESSAGE)!!
    }

    private val positiveButtonText: String by lazy {
        requireArguments().getString(POSITIVE_BUTTON_TEXT)!!
    }

    private val negativeButtonText: String by lazy {
        requireArguments().getString(NEGATIVE_BUTTON_TEXT)!!
    }

    private var callback: OnButtonPressed? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            return MaterialAlertDialogBuilder(it).apply {

                if (title.isNotEmpty() || title.isNotBlank()) {
                    setTitle(title)
                }

                if (negativeButtonText.isNotEmpty() || negativeButtonText.isNotBlank()) {
                    setNegativeButton(negativeButtonText) { _, _ ->
                        callback?.onNegative()
                    }
                }

                setPositiveButton(positiveButtonText) { _, _ ->
                    callback?.onPositive()
                }

                setMessage(message)
            }
            .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setCallback(onButtonPressed: OnButtonPressed) {
        this.callback = onButtonPressed
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    interface OnButtonPressed {
        fun onPositive()
        fun onNegative()
    }
}