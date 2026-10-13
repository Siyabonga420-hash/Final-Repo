package com.example.rentmate

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build

class RentMateAlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "rentmate_alarm_channel"
        private const val CHANNEL_NAME = "RentMate Alarms"
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val alarmTitle =
            intent.getStringExtra("alarm_title")
                ?: "RentMate Reminder"

        createNotificationChannel(context)

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        // Alarm sound
        val soundUri =
            RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_ALARM
            )

        /*
         * Open the RentMate app when the
         * user taps the notification.
         */
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(
                context.packageName
            )

        val pendingIntent =
            if (launchIntent != null) {

                PendingIntent.getActivity(
                    context,
                    alarmTitle.hashCode(),
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            } else {
                null
            }

        /*
         * Build the notification.
         *
         * Notification.Builder is used for
         * newer Android versions.
         */
        val builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                android.app.Notification.Builder(
                    context,
                    CHANNEL_ID
                )

            } else {

                @Suppress("DEPRECATION")
                android.app.Notification.Builder(context)
            }

        builder
            .setSmallIcon(
                android.R.drawable.ic_lock_idle_alarm
            )
            .setContentTitle(
                "🔔 RentMate Alarm"
            )
            .setContentText(
                alarmTitle
            )
            .setPriority(
                android.app.Notification.PRIORITY_MAX
            )
            .setCategory(
                android.app.Notification.CATEGORY_ALARM
            )
            .setSound(soundUri)
            .setAutoCancel(true)

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }

        /*
         * Android 13+ requires notification permission.
         */
        if (
            Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            notificationManager.notify(
                alarmTitle.hashCode(),
                builder.build()
            )
        }
    }

    private fun createNotificationChannel(
        context: Context
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val soundUri =
                RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_ALARM
                )

            val audioAttributes =
                android.media.AudioAttributes.Builder()
                    .setUsage(
                        android.media.AudioAttributes.USAGE_ALARM
                    )
                    .build()

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )

            channel.setSound(
                soundUri,
                audioAttributes
            )

            channel.enableVibration(true)

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.createNotificationChannel(
                channel
            )
        }
    }
}

