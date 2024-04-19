package ca.maplenetwork.waitifylock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import ca.maplenetwork.waitifylock.helpers.NotificationHelper
import ca.maplenetwork.waitifylock.helpers.PermissionHelper
import ca.maplenetwork.waitifylock.helpers.UsageHelper

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_OFF -> {
                Log.d(TAG, "Screen turned OFF")
                UsageHelper.stop(context)
            }
            Intent.ACTION_USER_PRESENT -> {
                Log.d(TAG, "Device unlocked")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    UsageHelper.start(context)
                }
            }
            ACTION_CHECK_UPDATE -> {
                Log.d(TAG, "Checking for updates")
                NotificationHelper.createUpdateNotification(context)
            }
            ACTION_CHECK_STATUS -> {
                Log.d(TAG, "Checking status")
                statusUpdate(context)
            }
        }
    }

    fun registerReceiver(context: Context) {
        if (!isRegistered) {
            NotificationHelper.createNotificationGroups(context)

            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                //addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_USER_PRESENT)
                addAction(ACTION_CHECK_UPDATE)
            }

            val receiverFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Context.RECEIVER_EXPORTED // Safe to use on API 33 and above
            } else {
                0 // No flags used for lower API levels
            }

            context.registerReceiver(this, filter, receiverFlags)
            isRegistered = true
        }
    }

    fun unregisterReceiver(context: Context) {
        if (isRegistered) {
            context.unregisterReceiver(this)
            isRegistered = false
        }
    }

    companion object {
        const val TAG = "MyReceiver"
        const val ACTION_CHECK_UPDATE = "ca.maplenetwork.waitifylock.action.CHECK_UPDATE"
        const val ACTION_CHECK_STATUS = "ca.maplenetwork.waitifylock.action.CHECK_STATUS"
        private const val ACTION_ANNOUNCE_ENABLED = "ca.maplenetwork.waitifylock.status.ENABLED"
        var isRegistered = false

        fun statusUpdate(context: Context, force: Boolean? = null) {
            val enabled = force ?: PermissionHelper.isAppEnabled(context)

            val intent = Intent(ACTION_ANNOUNCE_ENABLED).apply {
                setPackage("ca.maplenetwork.waitify")
                putExtra("status", enabled)
            }

            context.sendBroadcast(intent)
        }
    }
}