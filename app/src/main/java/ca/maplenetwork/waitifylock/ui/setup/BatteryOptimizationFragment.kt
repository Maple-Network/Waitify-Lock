package ca.maplenetwork.waitifylock.ui.setup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.databinding.SetupFragmentBatteryOptimizationsBinding
import ca.maplenetwork.waitifylock.helpers.PermissionHelper

class BatteryOptimizationFragment : SetupFragment() {
    override val setupTitle by lazy { getString(R.string.battery_optimizations_title) }
    override val displayFragment = DisplayFragment()
    override val acceptButtonEnabled: Boolean = true
    override val skipWhenPossible: Boolean = true
    override fun acceptButtonClicked() {
        PermissionHelper.BatteryOptimization.request(requireContext())
    }

    override fun isSetupComplete(): Boolean {
        return PermissionHelper.BatteryOptimization.isDisabled(requireContext())
    }

    class DisplayFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val binding = SetupFragmentBatteryOptimizationsBinding.inflate(inflater, container, false)
            return binding.root
        }
    }
}