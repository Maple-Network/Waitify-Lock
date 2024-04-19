package ca.maplenetwork.waitifylock.helpers

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ca.maplenetwork.waitifylock.DeviceAdminHandler
import ca.maplenetwork.waitifylock.MainActivity
import ca.maplenetwork.waitifylock.MyAccessibilityService
import ca.maplenetwork.waitifylock.Variables

object PermissionHelper {
    fun isAppEnabled(context: Context): Boolean {
        val mDPM by lazy { context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
        val mAdminName by lazy { ComponentName(context, DeviceAdminHandler::class.java) }

        return (isUsageAccessGranted(context) || Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) && isAccessibilityServiceGranted(context) && Variables.ProtectPermissions(context) && mDPM.isAdminActive(mAdminName)
    }
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

    object Notification {
        fun request(activity : Activity) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), MainActivity.MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS)
        }
        fun isGranted(context: Context): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
    }

    object BatteryOptimization {
        fun request(context: Context) {
            val packageName = context.packageName
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            context.startActivity(intent)
        }
        fun isDisabled(context: Context): Boolean {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
    }
}