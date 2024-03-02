package ca.maplenetwork.waitifylock.Helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import ca.maplenetwork.waitifylock.MainActivity

object NavigationHelper {
    fun openMainMenu(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    fun openHomeScreen(context: Context) {
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
        context.startActivity(intent)
    }

    fun openUsageAccessSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                data = Uri.fromParts("package", "ca.maplenetwork.waitify", null)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("TAG", "openUsageAccessSettings: ", e)
        }
    }
}
