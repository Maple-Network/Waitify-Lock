package ca.maplenetwork.waitifylock

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import ca.maplenetwork.waitifylock.helpers.NavigationHelper.openAndroidSettings
import ca.maplenetwork.waitifylock.helpers.UsageHelper

class MyAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MyAccessibilityService"
        var accessibilityService: MyAccessibilityService? = null
    }

    override fun onServiceConnected() {
        accessibilityService = this
        configureAccessibilityService()
        MyReceiver().registerReceiver(this)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {UsageHelper.start(this)}

        Log.d(TAG, "Service connected and configured.")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        configureAccessibilityService()
        Log.d(TAG, "onStartCommand: Service started or restarted.")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun configureAccessibilityService() {
        val appsEnabledArray = arrayOf("com.android.settings", "com.google.android.permissioncontroller")
        val info = AccessibilityServiceInfo().apply {
            packageNames = appsEnabledArray
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        }
        serviceInfo = info
        Log.d(TAG, "Accessibility service configured for specific apps.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (Variables.ProtectPermissions(this)) {
            //Log.d(TAG, "onAccessibilityEvent: Event received from ${event.packageName}.")
            //printNodeInfo(rootInActiveWindow)
            handleProtectedServices(event)
        }
    }

    override fun onInterrupt() {}

    private fun printNodeInfo(node: AccessibilityNodeInfo?, depth: Int = 0) {
        if (node == null) return

        val indent = " ".repeat(depth * 2)
        Log.d(TAG, "$indent Node Info - Text: ${node.text}, Content Description: ${node.contentDescription}, Class Name: ${node.className}")

        for (i in 0 until node.childCount) {
            printNodeInfo(node.getChild(i), depth + 1)
        }
    }

    private fun handleProtectedServices(event: AccessibilityEvent) {
        when (event.packageName.toString()) {
            "com.android.settings" -> {
                checkNode(setOf("Waitify", "App info"), rootInActiveWindow)
                checkNode(setOf("Waitify", "admin app is active"), rootInActiveWindow)
                checkNode(setOf("Waitify", "accessibility service"), rootInActiveWindow)
                checkNode(setOf("Waitify", "Usage access allows"), rootInActiveWindow)
            }
            "com.google.android.permissioncontroller" -> {
                checkNode(setOf("Waitify"), rootInActiveWindow)
            }
        }
    }

    private fun checkNode(targetList: Set<String>, node: AccessibilityNodeInfo?) {
        if (node == null) return

        if (checkForTextsInNode(node, targetList)) {
            openAndroidSettings(this);
        }
    }

    private fun checkForTextsInNode(node: AccessibilityNodeInfo?, targetTexts: Set<String>, foundTexts: MutableSet<String> = mutableSetOf(), depth: Int = 0): Boolean {
        if (node == null || targetTexts.isEmpty()) return false

        // Check current node for any of the target texts.
        targetTexts.forEach { targetText ->
            if ((node.text?.toString()?.contains(targetText) == true || node.contentDescription?.toString()?.contains(targetText) == true) && !foundTexts.contains(targetText)) {
                foundTexts.add(targetText)
                if (foundTexts.size == targetTexts.size) return true
            }
        }

        // Recursively check each child node for remaining texts.
        for (i in 0 until node.childCount) {
            if (checkForTextsInNode(node.getChild(i), targetTexts, foundTexts, depth + 1)) { return true }
        }

        return foundTexts.size == targetTexts.size
    }
}
