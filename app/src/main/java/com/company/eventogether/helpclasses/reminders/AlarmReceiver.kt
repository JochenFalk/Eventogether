package com.company.eventogether.helpclasses.reminders

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.company.eventogether.R
import com.company.eventogether.activities.EventActivity
import com.company.eventogether.helpclasses.Tools.getLocalTimeStringHHMM
import com.company.eventogether.model.EventReminderDTO
import com.company.eventogether.viewmodels.EventViewModel
import org.koin.java.KoinJavaComponent.inject
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val REQUEST_CODE = 100
        const val CHANNEL_ID = "10"
        const val CHANNEL_NAME = "Event_Reminder"
    }

    private val eventViewModel: EventViewModel by inject(EventViewModel::class.java)
    private val remindersManager: RemindersManager by inject(RemindersManager::class.java)

    override fun onReceive(context: Context, intent: Intent) {

        try {
            val fbKey = intent.getStringExtra("FbKey").toString()
            val calendar = intent.getSerializableExtra("Calendar") as Calendar
            val intentId = intent.getLongExtra("IntentId", 0).toInt()
            val isRecurring = intent.getBooleanExtra("IsRecurring", true)
            val title = intent.getStringExtra("Title").toString()
            val text = intent.getStringExtra("Text").toString()
            val textBig = intent.getStringExtra("TextBig").toString()

            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            val contentIntent = Intent(context, EventActivity::class.java).apply {
                putExtra("FbKey", fbKey)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                REQUEST_CODE,
                contentIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            notificationManager.sendReminderNotification(
                context = context,
                intentId = intentId,
                title = title,
                text = text,
                textBig = textBig,
                pendingIntent = pendingIntent
            )

            if (isRecurring) {
                remindersManager.startReminder(
                    context = context,
                    fbKey = fbKey,
                    calendar = calendar,
                    isRecurring = isRecurring,
                    title = title,
                    text = text,
                    textBig = textBig
                )
            } else {
                eventViewModel.updateReminder(
                    context = null,
                    event = null,
                    reminder = EventReminderDTO(
                        fbKey = fbKey,
                        position = -1,
                        timeString = getLocalTimeStringHHMM(calendar.timeInMillis),
                        isRecurring = isRecurring,
                        isActive = false
                    )
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred receiving an alarm broadcast: $e")
        }
    }

    private fun NotificationManager.sendReminderNotification(
        context: Context,
        intentId: Int,
        title: String,
        text: String,
        textBig: String,
        pendingIntent: PendingIntent?
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.default_logo_square)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(textBig)
            )
            .setAutoCancel(true)

        notify(intentId, builder.build())
    }
}