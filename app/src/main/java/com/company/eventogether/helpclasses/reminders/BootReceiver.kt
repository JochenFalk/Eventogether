package com.company.eventogether.helpclasses.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.company.eventogether.viewmodels.EventViewModel
import org.koin.java.KoinJavaComponent.inject

class BootReceiver : BroadcastReceiver() {

    private val eventViewModel: EventViewModel by inject(EventViewModel::class.java)

    override fun onReceive(context: Context, intent: Intent) {

        intent.action.let { action ->

            if (action == Intent.ACTION_BOOT_COMPLETED) {
                eventViewModel.rescheduleAllReminders(context)
            }
        }
    }
}