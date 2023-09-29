package ca.maplenetwork.waitifylock;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

public class DeviceAdminSample extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {}

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "If you disable this, you will be able to uninstall Waitify.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        MainActivity.disableAdmin(context);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent, UserHandle userHandle) {}
}