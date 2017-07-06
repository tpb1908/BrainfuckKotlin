package com.tpb.brainfuck.editor

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import com.tpb.brainfuck.R
import com.tpb.brainfuck.db.*
import java.lang.Exception

/**
 * Created by theo on 03/07/17.
 */
class ConfigDialog : DialogFragment() {

    private lateinit var program: Program
    private var listener: ConfigDialogListener? = null
    private var type: ConfigDialogType = ConfigDialogType.SAVE

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val view = activity.layoutInflater.inflate(R.layout.dialog_config, null)
        builder.setView(view)
        type = arguments?.getSerializable(getString(R.string.extra_config_type)) as? ConfigDialogType ?: type
        if (type == ConfigDialogType.RUN) {
            view.findViewById<TextView>(R.id.settings_title).setText(R.string.title_settings_run_dialog)
            view.findViewById<TextView>(R.id.button_ok).setText(R.string.title_settings_run_dialog)
        } else {
            view.findViewById<TextView>(R.id.settings_title).setText(R.string.title_settings_save_dialog)
            view.findViewById<TextView>(R.id.button_ok).setText(R.string.title_settings_save_dialog)
        }

        if (program.name.isNotEmpty()) view.findViewById<EditText>(R.id.name_input).setText(program.name)
        if (program.description.isNotEmpty()) view.findViewById<EditText>(R.id.desc_input).setText(program.description)
        if (program.outSuffix.isNotEmpty()) view.findViewById<EditText>(R.id.output_suffix_input).setText(program.outSuffix)
        if (program.input.isNotEmpty()) view.findViewById<EditText>(R.id.input_stream_input).setText(program.input)

        view.findViewById<EditText>(R.id.size_input).setText(Integer.toString(program.memoryCapacity))
        view.findViewById<EditText>(R.id.min_input).setText(Integer.toString(program.minVal))
        view.findViewById<EditText>(R.id.max_input).setText(Integer.toString(program.maxVal))


        when (program.valueOverflowBehaviour) {
            ValueOverflowBehaviour.ERROR -> view.findViewById<RadioButton>(R.id.value_over_error_checkbox).isChecked = true
            ValueOverflowBehaviour.CAP -> view.findViewById<RadioButton>(R.id.value_over_cap_checkbox).isChecked = true
            ValueOverflowBehaviour.WRAP -> view.findViewById<RadioButton>(R.id.value_over_wrap_checkbox).isChecked = true
        }
        when (program.valueUnderflowBehaviour) {
            ValueUnderflowBehaviour.ERROR -> view.findViewById<RadioButton>(R.id.value_under_error_checkbox).isChecked = true
            ValueUnderflowBehaviour.CAP -> view.findViewById<RadioButton>(R.id.value_under_cap_checkbox).isChecked = true
            ValueUnderflowBehaviour.WRAP -> view.findViewById<RadioButton>(R.id.value_under_wrap_checkbox).isChecked = true
        }
        when (program.pointerOverflowBehaviour) {
            PointerOverflowBehaviour.ERROR -> view.findViewById<RadioButton>(R.id.pointer_over_error_checkbox).isChecked = true
            PointerOverflowBehaviour.EXPAND -> view.findViewById<RadioButton>(R.id.pointer_over_expand_checkbox).isChecked = true
            PointerOverflowBehaviour.WRAP -> view.findViewById<RadioButton>(R.id.pointer_over_wrap_checkbox).isChecked = true
        }
        when (program.pointerUnderflowBehaviour) {
            PointerUnderflowBehaviour.ERROR -> view.findViewById<RadioButton>(R.id.pointer_under_error_checkbox).isChecked = true
            PointerUnderflowBehaviour.WRAP -> view.findViewById<RadioButton>(R.id.pointer_under_wrap_checkbox).isChecked = true
        }
        when (program.emptyInputBehaviour) {
            EmptyInputBehaviour.KEYBOARD -> view.findViewById<RadioButton>(R.id.keyboard_input_checkbox).isChecked = true
            EmptyInputBehaviour.ZERO -> view.findViewById<RadioButton>(R.id.zero_input_checkbox).isChecked = true
        }

        addListeners(view)

        return builder.create()
    }

    private fun addListeners(view: View) {


        view.findViewById<RadioGroup>(R.id.empty_input_behaviour).setOnCheckedChangeListener { _, i ->
            program.emptyInputBehaviour = when (i) {
                R.id.keyboard_input_checkbox -> EmptyInputBehaviour.KEYBOARD
                else -> EmptyInputBehaviour.ZERO
            }
        }
        view.findViewById<RadioGroup>(R.id.pointer_overflow_behaviour).setOnCheckedChangeListener { _, i ->
            program.pointerOverflowBehaviour = when (i) {
                R.id.pointer_over_expand_checkbox -> PointerOverflowBehaviour.EXPAND
                R.id.pointer_over_wrap_checkbox -> PointerOverflowBehaviour.WRAP
                else -> PointerOverflowBehaviour.ERROR

            }
        }
        view.findViewById<RadioGroup>(R.id.pointer_underflow_behaviour).setOnCheckedChangeListener { _, i ->
            program.pointerUnderflowBehaviour = when (i) {
                R.id.pointer_under_wrap_checkbox -> PointerUnderflowBehaviour.WRAP
                else -> PointerUnderflowBehaviour.ERROR
            }
        }
        view.findViewById<RadioGroup>(R.id.value_overflow_behaviour).setOnCheckedChangeListener { _, i ->
            program.valueOverflowBehaviour = when (i) {
                R.id.value_over_wrap_checkbox -> ValueOverflowBehaviour.WRAP
                R.id.value_over_cap_checkbox -> ValueOverflowBehaviour.CAP
                else -> ValueOverflowBehaviour.ERROR
            }
        }
        view.findViewById<RadioGroup>(R.id.value_underflow_behaviour).setOnCheckedChangeListener { _, i ->
            program.valueUnderflowBehaviour = when (i) {
                R.id.value_under_wrap_checkbox -> ValueUnderflowBehaviour.WRAP
                R.id.value_over_cap_checkbox -> ValueUnderflowBehaviour.CAP
                else -> ValueUnderflowBehaviour.ERROR
            }
        }

        view.findViewById<Button>(R.id.button_ok).setOnClickListener {
            var error = false
            if (view.findViewById<TextView>(R.id.name_input).text.isEmpty()) {
                error = true
                view.findViewById<TextInputLayout>(R.id.name_wrapper).error = getString(R.string.error_no_program_name)
            } else {
                view.findViewById<TextInputLayout>(R.id.name_wrapper).error = null
            }
            val inputString = view.findViewById<EditText>(R.id.input_stream_input).text.toString()
            if (inputString.isNotEmpty()) {
                try {
                    val ints = ArrayList<Int>()
                    inputString.split(",").map { it.trim() }.mapTo(ints, {Integer.parseInt(it)})
                } catch (e: Exception) {
                    error = true
                    view.findViewById<TextInputLayout>(R.id.input_stream_wrapper).error = getString(R.string.error_invalid_input)
                }
            }

            var capacity = 10000
            val sizeInput = view.findViewById<EditText>(R.id.size_input)
            val sizeWrapper = view.findViewById<TextInputLayout>(R.id.size_wrapper)
            if (sizeInput.text.isNotEmpty()) {
                capacity = parseInt(sizeInput.text.toString(), capacity)
                if (capacity < 1) {
                    error = true
                    sizeWrapper.error = getString(R.string.error_min_memory_size)
                } else if (capacity > 100000) {
                    error = true
                    sizeWrapper.error = getString(R.string.error_max_memory_size)
                } else {
                    sizeWrapper.error = null
                }
            }
            var maxVal = Integer.MAX_VALUE
            val maxInput = view.findViewById<EditText>(R.id.max_input)
            if (maxInput.text.isNotEmpty()) {
                maxVal = parseInt(maxInput.text.toString(), maxVal)
            }
            var minVal = Integer.MIN_VALUE
            val minInput = view.findViewById<EditText>(R.id.min_input)
            if (minInput.text.isNotEmpty()) {
                minVal = parseInt(minInput.text.toString(), minVal)
            }
            if (minVal > maxVal) {
                error = true
                view.findViewById<TextInputLayout>(R.id.min_wrapper).error = getString(R.string.error_min_max)
                view.findViewById<TextInputLayout>(R.id.max_wrapper).error = getString(R.string.error_max_min)
            } else {
                view.findViewById<TextInputLayout>(R.id.min_wrapper).error = null
                view.findViewById<TextInputLayout>(R.id.max_wrapper).error = null
            }

            if (!error) {
                program.name = view.findViewById<TextView>(R.id.name_input).text.toString()
                program.memoryCapacity = capacity
                program.minVal = minVal
                program.maxVal = maxVal
                program.description = view.findViewById<EditText>(R.id.desc_input).text.toString()
                program.outSuffix = view.findViewById<EditText>(R.id.output_suffix_input).text.toString()
                program.input = inputString
                listener?.onPositiveClick(this, type, program)
                dialog?.dismiss()
            }
        }

        view.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            listener?.onNegativeClick(this, type)
            dialog?.cancel()
        }
    }

    private fun parseInt(string: String, default: Int): Int {
        try {
            return Integer.parseInt(string)
        } catch (e: Exception) {
            return default
        }
    }

    interface ConfigDialogListener {

        fun onPositiveClick(dialog: DialogFragment, launchType: ConfigDialogType, program: Program)

        fun onNegativeClick(dialog: DialogFragment, launchType: ConfigDialogType)

    }

    enum class ConfigDialogType {
        SAVE, RUN, CLOSE
    }

    class Builder(program: Program) {
        private val dialog: ConfigDialog = ConfigDialog()

        init {
            dialog.program = program
        }

        fun setListener(listener: ConfigDialogListener): Builder {
            dialog.listener = listener
            return this
        }

        fun setType(type: ConfigDialogType): Builder {
            dialog.type = type
            return this
        }

        fun build(): ConfigDialog = dialog

    }

}