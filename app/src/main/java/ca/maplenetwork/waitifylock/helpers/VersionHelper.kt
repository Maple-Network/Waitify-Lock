package ca.maplenetwork.waitifylock.helpers

import android.content.Context
import android.util.Log
import ca.maplenetwork.waitifylock.BuildConfig
import ca.maplenetwork.waitifylock.Variables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object VersionHelper {
    fun startupCheck(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            val lastVersion = Variables.lastAppVersion(context)
            val currentVersion = getBuildVersion()

            when {
                currentVersion == lastVersion -> return@launch
                lastVersion == 0 -> updateToCurrentVersion(context, currentVersion)
                else -> {
                    DownloadHelper.deleteAllWaitifyLockApk(context)
                    updateToCurrentVersion(context, currentVersion)
                }
            }
        }
    }

    private fun updateToCurrentVersion(context: Context, currentVersion: Int) {
        Variables.lastAppVersion(context, currentVersion)
    }

    private fun getBuildVersion(): Int {
        return BuildConfig.VERSION_CODE
    }
}