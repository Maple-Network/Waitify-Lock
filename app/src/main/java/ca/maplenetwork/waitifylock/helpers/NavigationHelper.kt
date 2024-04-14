package ca.maplenetwork.waitifylock.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import ca.maplenetwork.waitifylock.MainActivity
import ca.maplenetwork.waitifylock.ui.setup.SetupActivity

object NavigationHelper {
    fun openMainMenu(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    fun openSetupActivity(context: Context) {
        val intent = Intent(context, SetupActivity::class.java)
        context.startActivity(intent)
    }

    fun openAndroidHome(context: Context) {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        context.startActivity(homeIntent)
    }

    fun openAndroidSettings(context: Context) {
        val settingsIntent = Intent(Settings.ACTION_SETTINGS)
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(settingsIntent)
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    fun openUsageAccessSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                data = Uri.fromParts("package", "ca.maplenetwork.waitifylock", null)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent)
        }
    }
}
