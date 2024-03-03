package ca.maplenetwork.waitifylock.Helpers

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import ca.maplenetwork.waitifylock.Variables

object UsageHelper {
    private var currentLoop: Thread? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getLastTimeUsed(context: Context): Long {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000, time)

        val item = stats.find { it.packageName == "com.google.android.permissioncontroller" }

        return item?.lastTimeVisible ?: -1
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
            var lastTimeOpened = 0L
            while (true) {
                if (PermissionHelper.isUsageAccessGranted(context) && !Variables.ProtectPermissions(context)) {
                    Log.d("TAG", "Waiting")
                    Thread.sleep(10000)
                    continue
                }

                try {
                    val lastTimeUsed = getLastTimeUsed(context)
                    Log.d("TAG", "startLoop: $lastTimeUsed")
                    if (lastTimeUsed > lastTimeOpened && lastTimeUsed != -1L) {
                        lastTimeOpened = lastTimeUsed + 2000
                        NavigationHelper.openAndroidSettings(context)
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        currentLoop = loop
        currentLoop?.start()
    }
}