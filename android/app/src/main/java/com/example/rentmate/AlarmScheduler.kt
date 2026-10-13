package com.example.rentmate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {

    fun scheduleAlarm(
        context: Context,
        alarmId: Long,
        title: String,
        time: String
    ) {

        // Split time such as "07:30"
        val parts = time.split(":")

        if (parts.size != 2) {
            return
        }

        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        // Check that the time is valid
        if (hour !in 0..23 || minute !in 0..59) {
            return
        }

        val calendar = Calendar.getInstance().apply {

            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the selected time has already passed,
            // schedule the alarm for tomorrow.
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Intent that will be sent to our AlarmReceiver
        val intent = Intent(
            context,
            RentMateAlarmReceiver::class.java
        ).apply {

            putExtra("alarm_id", alarmId)
            putExtra("alarm_title", title)
        }

        // Unique PendingIntent for this alarm
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        /*
         * Android 12+ requires permission for exact alarms.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {
                // Approximate reminder instead of silently failing.
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                )
                return
            }
        }

        // Schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelAlarm(
        context: Context,
        alarmId: Long
    ) {

        val intent = Intent(
            context,
            RentMateAlarmReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }
}

