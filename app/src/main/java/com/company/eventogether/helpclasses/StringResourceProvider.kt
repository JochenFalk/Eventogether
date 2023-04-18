package com.company.eventogether.helpclasses

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

class StringResourcesProvider(private val application: Application) {

    fun getString(stringResId: Int) : String {

        return application.resources.getString(stringResId)
    }

    fun getStringArray(stringArrayResId: Int) : Array<String> {

        return application.resources.getStringArray(stringArrayResId)
    }

    fun getColor(colorResId: Int): Int {

        return ContextCompat.getColor(application.applicationContext, colorResId)
    }

    fun getDrawable(stringResId: Int) : Drawable? {

        return ResourcesCompat.getDrawable(application.resources, stringResId, null)
    }
}