package ca.maplenetwork.waitifylock.ui.setup

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.databinding.SetupFragmentBinding
import pl.droidsonroids.gif.GifDrawable

open class SetupFragment : Fragment() {
    private lateinit var binding: SetupFragmentBinding

    open val setupTitle = "Setup"
    open val displayFragment: Fragment? = null
    open val acceptButtonEnabled = false
    open val skipWhenPossible = false
    open fun acceptButtonClicked() {}
    open fun isSetupComplete() = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SetupFragmentBinding.inflate(inflater, container, false)

        refreshPageState()
        setupListeners()
        setupFragment()

        return binding.root
    }

    private fun setupListeners() {
        with (binding) {
            childFragmentManager.setFragmentResultListener("refresh", this@SetupFragment) { _, _ ->
                refreshPageState()
            }
            backButton.setOnClickListener {
                SetupActivity.previousPage(parentFragmentManager)
            }
            acceptButton.setOnClickListener {
                acceptButtonClicked()
            }
            continueButton.setOnClickListener {
                if (isSetupComplete()) {
                    SetupActivity.nextPage(parentFragmentManager)
                    return@setOnClickListener
                }

                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(R.string.skip_permission_title))
                    setMessage(getString(R.string.skip_permission_title))
                    setPositiveButton(getString(R.string.yes_button)) { dialog, _ ->
                        SetupActivity.nextPage(parentFragmentManager)
                        dialog.dismiss()
                    }
                    setNegativeButton(getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss() }
                    show()
                }
            }
            exitButton.setOnClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(R.string.exit_setup_title))
                    setMessage(getString(R.string.exit_setup_message))
                    setPositiveButton(getString(R.string.yes_button)) { dialog, _ ->
                        requireActivity().finish()
                        dialog.dismiss()
                    }
                    setNegativeButton(getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss() }
                    show()
                }
            }
        }
    }
    private fun refreshPageState() {
        binding.setupTitle.text = setupTitle
        val state = isSetupComplete()
        binding.acceptButton.visibility = if (acceptButtonEnabled) View.VISIBLE else View.INVISIBLE
        binding.acceptButton.isEnabled = !state
        binding.continueButton.text = if (state) getString(R.string.next_button) else getString(R.string.skip_button)
    }
    private fun setupFragment() {
        displayFragment?.let {
            childFragmentManager.beginTransaction().apply {
                replace(binding.fragmentContainer.id, it)
                commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPageState()
    }

    companion object {
        fun stopGif(gifDrawable: GifDrawable) {
            gifDrawable.stop()
            gifDrawable.seekTo(0)
        }
        fun startGif(gifDrawable: GifDrawable) {
            gifDrawable.reset()
        }
    }
}