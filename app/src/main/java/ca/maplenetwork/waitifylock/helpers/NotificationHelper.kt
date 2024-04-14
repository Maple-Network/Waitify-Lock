package ca.maplenetwork.waitifylock.helpers

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ca.maplenetwork.waitifylock.MainActivity
import ca.maplenetwork.waitifylock.R


object NotificationHelper {
    private const val GENERAL_GROUP_ID = "WaitifyGeneralGroup"
    private const val UPDATE_CHANNEL_ID = "WaitifyUpdateChannel"
    private const val UPDATE_NOTIFICATION_ID = 1
    private var GeneralGroup = NotificationChannelGroup(GENERAL_GROUP_ID, "General")
    private val UpdateNotificationChannel = NotificationChannel(UPDATE_CHANNEL_ID, "App Update", NotificationManager.IMPORTANCE_DEFAULT)

    fun createNotificationGroups(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(GeneralGroup)

        UpdateNotificationChannel.group = GENERAL_GROUP_ID
        UpdateNotificationChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
        notificationManager.createNotificationChannel(UpdateNotificationChannel)
    }

    fun createUpdateNotification(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_waitify)
            .setContentTitle(context.getString(R.string.update_notification_title))
            .setContentText(context.getString(R.string.update_notification_message))
            .setAutoCancel(false)
            .setOngoing(false)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(UPDATE_NOTIFICATION_ID, builder.build())
    }
}

