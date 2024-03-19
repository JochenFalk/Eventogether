package com.company.eventogether.helpclasses.reminders

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import java.util.*

class RemindersManager {

    companion object {
        const val TAG = "RemindersManager"
    }

    fun startReminder(
        context: Context,
        fbKey: String,
        calendar: Calendar,
        isRecurring: Boolean,
        title: String,
        text: String,
        textBig: String
    ) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val contentIntent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            putExtra("Calendar", calendar)
            putExtra("FbKey", fbKey)
            putExtra("IntentId", calendar.timeInMillis)
            putExtra("IsRecurring", isRecurring)
            putExtra("Title", title)
            putExtra("Text", text)
            putExtra("TextBig", textBig)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            AlarmReceiver.REQUEST_CODE,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (calendar.timeInMillis - Calendar.getInstance(Locale.getDefault()).timeInMillis < 0) {
            calendar.add(Calendar.DATE, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    context.startActivity(intent)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context.startActivity(intent)
                }
            } else if (NotificationManagerCompat.from(context).areNotificationsEnabled()){
                doSetExactAndAllowWhileIdle(alarmManager, calendar.timeInMillis, pendingIntent)
            }
        } else {
            doSetExactAndAllowWhileIdle(alarmManager, calendar.timeInMillis, pendingIntent)
        }

        Log.d(TAG, "Alarm set for: ${calendar.time}")
    }

    fun stopReminder(
        context: Context,
        calendar: Calendar
    ) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val contentIntent = Intent(context.applicationContext, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            AlarmReceiver.REQUEST_CODE,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (calendar.timeInMillis - Calendar.getInstance(Locale.getDefault()).timeInMillis < 0) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.cancel(pendingIntent)

        Log.d(TAG, "Alarm deleted for: ${calendar.time}")
    }

    private fun doSetExactAndAllowWhileIdle(
        alarmManager: AlarmManager,
        timeInMillis: Long,
        pendingIntent: PendingIntent
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }
}