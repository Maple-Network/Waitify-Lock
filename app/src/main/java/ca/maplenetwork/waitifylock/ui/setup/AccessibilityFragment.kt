package ca.maplenetwork.waitifylock.ui.setup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.databinding.SetupFragmentAccessibilityBinding
import ca.maplenetwork.waitifylock.helpers.NavigationHelper
import ca.maplenetwork.waitifylock.helpers.PermissionHelper
import pl.droidsonroids.gif.GifDrawable

class AccessibilityFragment : SetupFragment() {
    override val setupTitle by lazy { getString(R.string.accessibility_title) }
    override val displayFragment = DisplayFragment()
    override val acceptButtonEnabled: Boolean = true
    override val skipWhenPossible: Boolean = true
    override fun acceptButtonClicked() {
        NavigationHelper.openAccessibilitySettings(requireContext())
    }

    override fun isSetupComplete(): Boolean {
        return PermissionHelper.isAccessibilityServiceGranted(requireContext())
    }

    class DisplayFragment : Fragment() {
        private lateinit var binding: SetupFragmentAccessibilityBinding
        private lateinit var gifDrawable: GifDrawable
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            binding = SetupFragmentAccessibilityBinding.inflate(inflater, container, false)

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