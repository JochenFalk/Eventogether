package com.company.eventogether.helpclasses.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.company.eventogether.AppSettings.APP_LOCAL
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

        if (calendar.timeInMillis - Calendar.getInstance(APP_LOCAL).timeInMillis < 0) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

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
}