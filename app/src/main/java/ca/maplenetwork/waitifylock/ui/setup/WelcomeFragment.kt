package ca.maplenetwork.waitifylock.ui.setup

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.databinding.SetupFragmentWelcomeBinding
import ca.maplenetwork.waitifylock.ui.setup.SetupActivity.PageList

class WelcomeFragment : Fragment() {
    private lateinit var binding: SetupFragmentWelcomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SetupFragmentWelcomeBinding.inflate(inflater, container, false)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        with (binding) {
            skipSetupButton.setOnClickListener {
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
            startButton.setOnClickListener {
                val setupGuide = Pair(0, listOf(PageList.Notifications, PageList.UsageAccess, PageList.Accessibility, PageList.BatteryOptimization, PageList.ParentalPin, PageList.UninstallProtection))
                SetupActivity.setupGuide = setupGuide
                SetupActivity.nextPage(parentFragmentManager)
            }
        }
    }
}