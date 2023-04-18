package com.company.eventogether.helpclasses

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object Tools {

    @Throws(IOException::class)
    fun createImageFile(context: Context, locale: Locale): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun getTimeArray(timeString: String, delimiter: String): Array<out String> {
        return Pattern.compile(delimiter).split(timeString)
    }

    fun getTimeStringHHMM(timeInMillis: Long) : String {

        var time = convertUTCToLocalTimeInMillis(timeInMillis)

        val days = TimeUnit.MILLISECONDS.toDays(time)
        time -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(time)
        time -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        time -= TimeUnit.MINUTES.toMillis(minutes)

        return String.format("%02d:%02d", hours, minutes)
    }

    fun convertUTCToLocalTimeInMillis(timeInMillis: Long): Long {

        val zoneId = ZoneId.systemDefault()
        val timeZoneInMillis = TimeZone.getTimeZone(zoneId).getOffset(timeInMillis)

        return timeInMillis + timeZoneInMillis
    }
}