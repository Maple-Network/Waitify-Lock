package ca.maplenetwork.waitifylock.ui.setup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.databinding.SetupFragmentUsageAccessBinding
import ca.maplenetwork.waitifylock.helpers.NavigationHelper
import ca.maplenetwork.waitifylock.helpers.PermissionHelper
import pl.droidsonroids.gif.GifDrawable

class UsageAccessFragment : SetupFragment() {
    override val setupTitle by lazy { getString(R.string.usage_access_title) }
    override val displayFragment = DisplayFragment()
    override val acceptButtonEnabled: Boolean = true
    override val skipWhenPossible: Boolean = true
    override fun acceptButtonClicked() {
        NavigationHelper.openUsageAccessSettings(requireContext())
    }

    override fun isSetupComplete(): Boolean {
        return PermissionHelper.isUsageAccessGranted(requireContext())
    }

    class DisplayFragment : Fragment() {
        private lateinit var binding: SetupFragmentUsageAccessBinding
        private lateinit var gifDrawable: GifDrawable
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            binding = SetupFragmentUsageAccessBinding.inflate(inflater, container, false)

            gifDrawable = binding.guideGif.drawable.current as GifDrawable
            stopGif(gifDrawable)

            return binding.root
        }

        override fun onResume() {
            super.onResume()
            startGif(gifDrawable)
        }
        override fun onPause() {
            super.onPause()
            stopGif(gifDrawable)
        }
    }
}