package ca.maplenetwork.waitifylock.ui.setup

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.Variables
import ca.maplenetwork.waitifylock.databinding.DialogParentalSetBinding
import ca.maplenetwork.waitifylock.databinding.SetupFragmentParentalPinBinding

class ParentalPinFragment : SetupFragment() {
    override val setupTitle by lazy { "Parental PIN" }
    override val displayFragment = DisplayFragment()

    override fun isSetupComplete(): Boolean {
        return Variables.AppLockEnabled(requireContext()) && !Variables.AppLockPin(requireContext()).isNullOrBlank()
    }

    class DisplayFragment : Fragment() {
        private lateinit var binding: SetupFragmentParentalPinBinding
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            binding = SetupFragmentParentalPinBinding.inflate(inflater, container, false)

            val isChecked = Variables.AppLockEnabled(requireContext())
            binding.parentalLock.isChecked = isChecked
            binding.setParentalPinContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
            refreshIsPinSet()

            setupListeners()

            return binding.root
        }

        private fun refreshIsPinSet() {
            val isPinSet = Variables.AppLockEnabled(requireContext())
            binding.setParentalPin.text = if (isPinSet) getText(R.string.parental_pin_set) else getText(R.string.parental_pin_unset)
        }

        private fun setupListeners() {
            with(binding) {
                parentalLock.setOnCheckedChangeListener { _, isChecked ->
                    setParentalPinContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
                    Variables.AppLockEnabled(requireContext(), isChecked)
                    refreshPageState()
                }

                setParentalPin.setOnClickListener {
                    showParentalPinDialog()
                }
            }
        }

        private fun refreshPageState() {
            parentFragmentManager.setFragmentResult("refresh", Bundle())
        }

        private fun showParentalPinDialog() {
            val pinDialogBinding = DialogParentalSetBinding.inflate(layoutInflater)
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Set PIN")
                .setView(pinDialogBinding.root)
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", null)
                .create()

            alertDialog.setOnShowListener {
                val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                pinDialogBinding.apply {
                    positiveButton.setOnClickListener {
                        val pin = pinEditText.text.toString()
                        val confirmPin = confirmPinEditText.text.toString()
                        if (pin.isBlank() || pin != confirmPin) {
                            Toast.makeText(requireContext(), getString(R.string.parental_pin_guidelines), Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        Variables.AppLockPin(requireContext(), pin)
                        refreshIsPinSet()
                        refreshPageState()
                        alertDialog.dismiss()
                    }

                    showPinCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        pinEditText.transformationMethod = if (isChecked) null else PasswordTransformationMethod.getInstance()
                        confirmPinEditText.transformationMethod = if (isChecked) null else PasswordTransformationMethod.getInstance()
                    }
                }
            }

            alertDialog.show()
        }
    }
}