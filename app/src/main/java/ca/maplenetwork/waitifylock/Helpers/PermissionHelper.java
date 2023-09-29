package ca.maplenetwork.waitifylock.Helpers;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import ca.maplenetwork.waitifylock.Services.MyAccessibilityService;

public class PermissionHelper {
    public static boolean IsAccessibilityServiceGranted(Context context){
        ComponentName expectedComponentName = new ComponentName(context, MyAccessibilityService.class);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }
}
