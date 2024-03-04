package ca.maplenetwork.waitifylock.helpers

import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.RequiresApi
import ca.maplenetwork.waitifylock.MyAccessibilityService
import ca.maplenetwork.waitifylock.Variables


object UsageHelper {
    private var currentLoop: Thread? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun isAppOpen(context: Context): Boolean {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val time = System.currentTimeMillis()
        val usageEvents = usageStatsManager.queryEvents(time - 10000, time)

        var lastEventType = 2

        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            if (event.packageName != "com.google.android.permissioncontroller") {
                continue
            }

            lastEventType = event.eventType
        }

        return lastEventType == UsageEvents.Event.ACTIVITY_RESUMED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun startLoop(context: Context) {
        if (currentLoop != null) {
            try {
                currentLoop?.interrupt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            currentLoop = null
        }

        val loop = Thread {
            var fastSpeed = 0
            while (true) {
                if (PermissionHelper.isUsageAccessGranted(context) && !Variables.ProtectPermissions(context)) {
                    Thread.sleep(10000)
                    continue
                }

                try {
                    if (isAppOpen(context)) {
                        clickClose(context)
                        fastSpeed = 2
                    }

                    val currentSpeed = if (fastSpeed > 0) {
                        fastSpeed -= 1
                        100L
                    }else {
                        1000L
                    }
                    Thread.sleep(currentSpeed)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        currentLoop = loop
        currentLoop?.start()
    }

    private fun clickClose(context: Context) {
        val screenSize = getScreenDimensions(context)

        val x = screenSize.x * 0.08f
        val y = screenSize.y * 0.1f

        val path = Path()
        path.moveTo(x, y)
        val stroke = StrokeDescription(path, 0, 50)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        MyAccessibilityService.accessibilityService?.dispatchGesture(gesture, null, null)
    }

    private fun getScreenDimensions(context: Context): Point {
        val size = Point()

        val metrics: DisplayMetrics = context.resources.displayMetrics
        size.x = metrics.widthPixels
        size.y = metrics.heightPixels

        return size
    }
}