package ca.maplenetwork.waitifylock.ui.setup

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.databinding.SetupActivityMainBinding

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: SetupActivityMainBinding
    private lateinit var adapter: PageSlider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SetupActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PageSlider(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 5

        setupListeners()
    }

    private fun setupListeners() {
        supportFragmentManager.setFragmentResultListener("openPage", this) { _, bundle ->
            bundle.getInt("page").let { page ->
                binding.viewPager.currentItem = page
            }
        }
        onBackPressedDispatcher.addCallback {
            AlertDialog.Builder(this@SetupActivity).apply {
                setTitle(getString(R.string.exit_setup_title))
                setMessage(getString(R.string.exit_setup_message))
                setPositiveButton(getString(R.string.yes_button)) { dialog, _ ->
                    finish()
                    dialog.dismiss()
                }
                setNegativeButton(getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss() }
                show()
            }
        }
    }

    private class PageSlider(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return PageList.values().size
        }

        override fun createFragment(position: Int): Fragment {
            if (position > PageList.values().size) return SetupFragment()
            return PageList.values()[position].fragment
        }
    }

    enum class PageList(val fragment: Fragment) {
        Welcome(WelcomeFragment()),
        Notifications(NotificationFragment()),
        UsageAccess(UsageAccessFragment()),
        Accessibility(AccessibilityFragment()),
        BatteryOptimization(BatteryOptimizationFragment()),
        ParentalPin(ParentalPinFragment()),
        UninstallProtection(UninstallProtectionFragment()),
        Finish(FinishFragment())
    }

    companion object {
        var setupGuide: Pair<Int, List<PageList>>? = null
        private fun open(parentFragmentManager: FragmentManager, pageKey: PageList) {
            val bundle = Bundle()
            bundle.putInt("page", pageKey.ordinal)
            parentFragmentManager.setFragmentResult("openPage", bundle)
        }

        fun nextPage(parentFragmentManager: FragmentManager) {
            val currentSetupGuide = setupGuide ?: return
            val (position, keys) = currentSetupGuide
            setupGuide = currentSetupGuide.copy(first = position + 1)

            var page = PageList.Finish
            if (position < keys.size) page = keys[position]

            if (page != PageList.Finish) {
                val setupFragment = page.fragment as SetupFragment
                if (setupFragment.skipWhenPossible && setupFragment.isSetupComplete()) {
                    nextPage(parentFragmentManager)
                    return
                }
            }

            open(parentFragmentManager, page)
        }

        fun previousPage(parentFragmentManager: FragmentManager) {
            val currentSetupGuide = setupGuide ?: return
            val (position, keys) = currentSetupGuide
            setupGuide = currentSetupGuide.copy(first = position - 1)

            var pageKey = PageList.Welcome
            if (position > 1) pageKey = keys[position - 2]

            open(parentFragmentManager, pageKey)
        }
    }
}