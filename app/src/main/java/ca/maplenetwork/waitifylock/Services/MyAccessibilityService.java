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
        String[] AppsEnabledArray = new String[]{("com.android.settings")};


        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.packageNames = AppsEnabledArray;
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.eventTypes += AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        setServiceInfo(info);
    }

    // Check when an event occurs
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.d(TAG, "onAccessibilityEvent: " + event);
        //printAllNodes(getRootInActiveWindow());
        if (Variables.ProtectPermissions(this)) {
            CheckProtectedServices(event);
        }
    }
    
    private void CheckProtectedServices(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.getClassName() == null) {
            return;
        }

        // Accessibility
        if (event.getText().toString().contains("Waitify")) {
            if (!checkNodes(new String[]{"accessibility service"}, getRootInActiveWindow())) {
                return;
            }

            Log.d(TAG, "CheckProtectedServices: Accessibility");
            OpenAndroidSettings(this);
        }

        switch (event.getText().toString()) {
            // App Info
            case "[App info]": {
                if (!checkNodes(new String[]{"Waitify"}, getRootInActiveWindow())) {
                    return;
                }

                Log.d(TAG, "CheckProtectedServices: App Info");
                OpenAndroidSettings(this);
            }

            // Usage Access
            case "[Usage access]": {
                if (!checkNodes(new String[]{"Waitify"}, getRootInActiveWindow())) {
                    return;
                }

                Log.d(TAG, "CheckProtectedServices: Usage Access");
                OpenAndroidSettings(this);
            }

            default: {
                if (event.getClassName().toString().equals("com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd")) {
                    if (event.getContentDescription() == "Activate device admin app?") {
                        return;
                    }

                    if (!checkNodes(new String[]{"Waitify", "Deactivate"}, getRootInActiveWindow())) {
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
            Log.d(TAG, "checkNodes:");
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

    //When Accessibility Service Gets Disabled
    @Override
    public boolean onUnbind(Intent intent) {
        MainActivity.refreshAccessibilityButton(this);
        return super.onUnbind(intent);
    }
}
