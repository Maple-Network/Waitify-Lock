package ca.maplenetwork.waitifylock.ui.setup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.databinding.SetupFragmentNotificationsBinding
import ca.maplenetwork.waitifylock.helpers.PermissionHelper

class NotificationFragment : SetupFragment() {
    override val setupTitle by lazy { getString(R.string.notifications_title) }
    override val displayFragment = DisplayFragment()
    override val acceptButtonEnabled: Boolean = true
    override val skipWhenPossible: Boolean = true
    override fun acceptButtonClicked() {
        PermissionHelper.Notification.request(requireActivity())
    }

    override fun isSetupComplete(): Boolean {
        return PermissionHelper.Notification.isGranted(requireContext())
    }

    class DisplayFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val binding = SetupFragmentNotificationsBinding.inflate(inflater, container, false)
            return binding.root
        }
    }
}