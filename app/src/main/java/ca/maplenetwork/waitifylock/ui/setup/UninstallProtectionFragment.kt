package ca.maplenetwork.waitifylock.ui.setup

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.DeviceAdminHandler
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.databinding.SetupFragmentUninstallProtectionBinding

class UninstallProtectionFragment : SetupFragment() {
    override val setupTitle by lazy { getString(R.string.uninstall_protection_title) }
    override val displayFragment = DisplayFragment()

    private val mDPM by lazy {
        requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
    private val mAdminName by lazy {
        ComponentName(requireContext(), DeviceAdminHandler::class.java)
    }

    override fun isSetupComplete(): Boolean {
        return mDPM.isAdminActive(mAdminName)
    }

    class DisplayFragment : Fragment() {
        private lateinit var binding: SetupFragmentUninstallProtectionBinding
        private lateinit var deviceAdminResultLauncher : ActivityResultLauncher<Intent>

        private val mDPM by lazy {
            requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        }
        private val mAdminName by lazy {
            ComponentName(requireContext(), DeviceAdminHandler::class.java)
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            binding = SetupFragmentUninstallProtectionBinding.inflate(inflater, container, false)

            binding.preventUninstall.isChecked = mDPM.isAdminActive(mAdminName)

            setupListeners()

            return binding.root
        }

        private fun setupListeners() {
            with(binding) {
                deviceAdminResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode != Activity.RESULT_OK) {
                        preventUninstall.isChecked = false
                    }
                    refreshPageState()
                }

                preventUninstall.setOnCheckedChangeListener { _, isChecked ->
                    handlePreventUninstallSwitchChange(isChecked)
                    refreshPageState()
                }
            }
        }

        private fun refreshPageState() {
            parentFragmentManager.setFragmentResult("refresh", Bundle())
        }

        private fun handlePreventUninstallSwitchChange(isChecked: Boolean) {
            if (isChecked && !mDPM.isAdminActive(mAdminName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
                    putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_description))
                }
                deviceAdminResultLauncher.launch(intent)
            } else if (mDPM.isAdminActive(mAdminName)) {
                mDPM.removeActiveAdmin(mAdminName)
            }
        }
    }
}