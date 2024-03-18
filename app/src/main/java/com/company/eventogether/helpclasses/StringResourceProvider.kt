package com.company.eventogether.helpclasses

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.company.eventogether.R

class StringResourcesProvider(private val application: Application) {

    fun getStringByResId(stringResId: Int): String {

        return application.resources.getString(stringResId)
    }

    fun getStringArrayByResId(stringArrayResId: Int): Array<String> {

        return application.resources.getStringArray(stringArrayResId)
    }

    fun getStringByName(stringName: String): String {

        val resourceId = application.resources.getIdentifier(
            stringName,
            "string",
            application.resources.getResourcePackageName(R.string.app_name)
        )

        return if (resourceId != 0) {
            application.resources.getString(resourceId)
        } else {
            ""
        }
    }

    fun getStringArrayByName(stringArrayName: String): Array<String> {
        val resourceId = application.resources.getIdentifier(
            stringArrayName,
            "array",
            application.packageName
        )

        return if (resourceId != 0) {
            application.resources.getStringArray(resourceId)
        } else {
            arrayOf()
        }
    }

    fun getColor(colorResId: Int): Int {

        return ContextCompat.getColor(application.applicationContext, colorResId)
    }

    fun getDrawable(stringResId: Int): Drawable? {

        return ResourcesCompat.getDrawable(application.resources, stringResId, null)
    }
}