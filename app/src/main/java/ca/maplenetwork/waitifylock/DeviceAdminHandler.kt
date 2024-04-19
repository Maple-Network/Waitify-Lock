package ca.maplenetwork.waitifylock

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import ca.maplenetwork.waitifylock.MyReceiver.Companion.statusUpdate

class DeviceAdminHandler : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        statusUpdate(context)
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        return "If you disable this, you will be able to uninstall Waitify."
    }

    override fun onDisabled(context: Context, intent: Intent) {
        statusUpdate(context, false)
    }

    override fun onPasswordChanged(context: Context, intent: Intent, userHandle: UserHandle) {}
}