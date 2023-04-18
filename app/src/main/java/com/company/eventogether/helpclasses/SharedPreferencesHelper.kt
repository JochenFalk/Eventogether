package com.company.eventogether.helpclasses

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.lang.Exception
import java.lang.Long.getLong

object SharedPreferencesHelper {

    private lateinit var sharedPreferences: SharedPreferences
    private const val TAG = "SharedPreferencesHelper"

    fun savePreferences(
        activity: Activity,
        setName: String,
        key: String,
        stringValue: String? = null,
        intValue: Int? = null,
        doubleValue: Double? = null
    ) {
        sharedPreferences =
            activity.getSharedPreferences(setName, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        if (stringValue != null) {
            editor.putString(key, stringValue)
        }
        if (intValue != null) {
            editor.putInt(key, intValue)
        }
        if (doubleValue != null ) {
            editor.putLong(key, java.lang.Double.doubleToRawLongBits(doubleValue))
        }

        editor.apply()
        Log.d(TAG,"Preference $key is saved with set name: $setName")
    }

    fun retrievePreferences(
        activity: Activity,
        setName: String,
        key: String,
        type: String
    ): Any? {

        sharedPreferences =
            activity.getSharedPreferences(setName, Context.MODE_PRIVATE)

        when (type) {

            "STRING" -> {
                try {
                    return sharedPreferences.getString(key, null).toString()
                } catch (ex: Exception) {
                    Log.d(TAG,"Preference not found: $ex")
                }
            }
            "INT" -> {
                try {
                    return sharedPreferences.getInt(key, 0)
                } catch (ex: Exception) {
                    Log.d(TAG,"Preference not found: $ex")
                }
            }
            "DOUBLE" -> {
                try {
                    return getLong(
                        key,
                        java.lang.Double.doubleToRawLongBits(0.0)
                    )?.let {
                        java.lang.Double.longBitsToDouble(it)
                    }
                } catch (ex: Exception) {
                    Log.d(TAG,"Preference not found: $ex")
                }
            }
            else -> {
                Log.d(TAG,"Incompatible type specified")
            }
        }

        return Unit
    }

    fun clearPreferences(activity: Activity, setName: String) {

        sharedPreferences =
            activity.getSharedPreferences(setName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        Log.d(TAG, "Preferences cleared for set name: $setName")
    }
}