package com.company.eventogether

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.company.eventogether.helpclasses.reminders.AlarmReceiver
import com.company.eventogether.koin.koinExpandableListAdapter
import com.company.eventogether.koin.koinControllers
import com.company.eventogether.koin.koinHelpers
import com.company.eventogether.koin.koinRepositories
import com.company.eventogether.koin.koinViews
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                koinHelpers,
                koinViews,
                koinControllers,
                koinRepositories,
                koinExpandableListAdapter
            )
        }

        createNotificationsChannels()
    }

    private fun createNotificationsChannels() {

        val channel = NotificationChannel(
            AlarmReceiver.CHANNEL_ID,
            AlarmReceiver.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        ContextCompat.getSystemService(this, NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }
}