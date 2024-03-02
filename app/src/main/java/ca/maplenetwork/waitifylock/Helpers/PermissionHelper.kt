package ca.maplenetwork.waitifylock.Helpers

import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import ca.maplenetwork.waitifylock.MyAccessibilityService

object PermissionHelper {
    fun isAccessibilityServiceGranted(context: Context): Boolean {
        val expectedComponentName = ComponentName(context, MyAccessibilityService::class.java)
        val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            ?: return false

        return SimpleStringSplitter(':').apply {
            setString(enabledServicesSetting)
        }.any { ComponentName.unflattenFromString(it) == expectedComponentName }
    }

    @Suppress("DEPRECATION")
    fun isUsageAccessGranted(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
}