package ca.maplenetwork.waitifylock.ui.setup

import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.DeviceAdminHandler
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.Variables
import ca.maplenetwork.waitifylock.databinding.SetupFragmentFinishBinding
import ca.maplenetwork.waitifylock.helpers.NavigationHelper
import ca.maplenetwork.waitifylock.helpers.PermissionHelper

class FinishFragment : Fragment() {
    private lateinit var binding: SetupFragmentFinishBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SetupFragmentFinishBinding.inflate(inflater, container, false)

        if (!Variables.ProtectPermissions(requireContext())) { Variables.ProtectPermissions(requireContext(), true) }
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        with (binding) {
            openButton.setOnClickListener {
                requireActivity().finish()
            }
            closeButton.setOnClickListener {
                if (PermissionHelper.isAppEnabled(requireContext())) {
                    closeWaitify()
                    return@setOnClickListener
                }

                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(R.string.exit_waitify_running_false_title))
                    setMessage(getString(R.string.exit_waitify_running_false_message))
                    setPositiveButton(getString(R.string.yes_button)) { dialog, _ ->
                        closeWaitify()
                        dialog.dismiss()
                    }
                    setNegativeButton(getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss() }
                    show()
                }
            }
        }
    }

    private fun closeWaitify() {
        NavigationHelper.openAndroidHome(requireActivity())
        requireActivity().finish()
    }

    private fun refreshPageState() {
        val running = PermissionHelper.isAppEnabled(requireContext())
        binding.waitifyRunningText.text = if (running) getString(R.string.waitify_running_true) else getString(R.string.waitify_running_false)
    }

    override fun onResume() {
        super.onResume()
        refreshPageState()
    }
}