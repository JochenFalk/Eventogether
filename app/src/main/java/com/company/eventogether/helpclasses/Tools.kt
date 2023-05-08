package com.company.eventogether.helpclasses

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object Tools {

    private const val TAG = "Tools"

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

    fun formatDateToTimeString(date: Date, pattern: String): String {
        val timeZoneDate = SimpleDateFormat(pattern, Locale.getDefault())
        return timeZoneDate.format(date)
    }

    fun getLocalTimeStringHHMM(timeInMillis: Long): String {

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

    fun convertSerpApiDateToTimeInMillis(date: com.company.eventogether.model.Date): Long? {

        try {

            val dateString = date.startDate.toString().split(" ")
            val monthString = dateString[0]
            val timeString = date.whenX.toString().split(",")[2].trim().split(" ")
            val hoursString = timeString[0].trim()
            val operatorString = if (timeString[1].trim() == "PM" || timeString[1].trim() == "AM") {
                timeString[1].trim()
            } else if (timeString[2].trim() == "PM" || timeString[2].trim() == "AM") {
                timeString[2].trim()
            } else if (timeString[3].trim() == "PM" || timeString[3].trim() == "AM") {
                timeString[3].trim()
            } else {
                ""
            }

            val calendar = getInstance()
            val year = calendar.get(YEAR)
            val day = dateString[1].toInt()

            var month = 0

            for (i in 0..11) {
                if (getMonthByNumber(i).uppercase() == monthString.uppercase()) month = i
            }

            val timeFormat: SimpleDateFormat
            var stringToParse = ""

            if (operatorString != "") {
                timeFormat = if (hoursString.contains(":")) {
                    SimpleDateFormat("HH:mm a", Locale.US)
                } else {
                    SimpleDateFormat("h a", Locale.US)
                }
                stringToParse = "$hoursString $operatorString"
            } else {
                timeFormat = SimpleDateFormat("HH:mm", Locale.US)
                stringToParse = hoursString
            }
            val time = timeFormat.parse(stringToParse)
            calendar.time = time!!

            calendar.set(year, month, day)

            return calendar.timeInMillis

        } catch (e: Exception) {
            Log.d(
                TAG,
                "An unknown exception occurred parsing the time string: $e"
            )
        }

        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthByNumber(monthNumber: Int): String {

        val calendar = getInstance(Locale.US).apply {
            this.set(MONTH, monthNumber)
        }

        return String.format(Locale.US, "%tB", calendar)
    }

    fun capitalizeString(inputString: String): String {
        var outputString = inputString
        try {
            outputString = inputString.substring(0, 1).uppercase() + inputString.substring(1)
        } catch (e: Exception) {
            Log.d(
                TAG,
                "An unknown exception occurred capitalising the input string: $inputString"
            )
        }
        return outputString
    }
}