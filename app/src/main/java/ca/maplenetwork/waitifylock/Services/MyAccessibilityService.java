package ca.maplenetwork.waitifylock.Services;

import static ca.maplenetwork.waitifylock.Helpers.NavigationHelper.OpenAndroidSettings;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import ca.maplenetwork.waitifylock.MainActivity;
import ca.maplenetwork.waitifylock.Variables;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    @Override
    protected void onServiceConnected() {
        SetAccessibilityInfo();
        MainActivity.refreshAccessibilityButton(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SetAccessibilityInfo();
        return super.onStartCommand(intent, flags, startId);
    }

    public void SetAccessibilityInfo() {
        String[] AppsEnabledArray = new String[]{("com.android.settings"),("com.google.android.permissioncontroller")};

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.packageNames = AppsEnabledArray;
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.d(TAG, "onAccessibilityEvent: " + event);

        if (Variables.ProtectPermissions(this)) {
            CheckProtectedServices(event);
        }
    }
    
    private void CheckProtectedServices(AccessibilityEvent event) {
        if (event.getClassName() == null) {
            return;
        }

        switch (event.getPackageName().toString()) {
            // App Security
            case "com.google.android.permissioncontroller": {
                if (checkNodes(new String[]{"Waitify"}, getRootInActiveWindow())) {
                    Log.d(TAG, "CheckProtectedServices: Permissions");
                    OpenAndroidSettings(this);
                }
            }

            case "com.android.settings": {
                // Accessibility
                if (checkNodes(new String[]{"Waitify","accessibility service"}, getRootInActiveWindow())) {
                    Log.d(TAG, "CheckProtectedServices: Accessibility");
                    OpenAndroidSettings(this);
                    return;
                }

                // Usage Access
                if (checkNodes(new String[]{"Waitify","Permit usage access"}, getRootInActiveWindow())) {
                    Log.d(TAG, "CheckProtectedServices: Usage Access");
                    OpenAndroidSettings(this);
                    return;
                }

                // App info
                if (checkNodes(new String[]{"Waitify","App info"}, getRootInActiveWindow())) {
                    Log.d(TAG, "CheckProtectedServices: App Info");
                    OpenAndroidSettings(this);
                    return;
                }


                // Device Admin
                if (checkNodes(new String[]{"Waitify", "Deactivate", "Device Admin App"}, getRootInActiveWindow())) {
                    if (event.getContentDescription() == "Activate device admin app?") {
                        return;
                    }

                    Log.d(TAG, "CheckProtectedServices: Device Admin");
                    OpenAndroidSettings(this);
                }
            }
        }
    }

    private boolean checkNodes(String[] text, AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return false;
        }

        for (String s : text) {
            if (rootNode.findAccessibilityNodeInfosByText(s).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onInterrupt() {
        MainActivity.refreshAccessibilityButton(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MainActivity.refreshAccessibilityButton(this);
        return super.onUnbind(intent);
    }
}
