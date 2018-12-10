package com.seirion.code.util

import android.app.Activity
import android.graphics.Point
import android.util.Log

class DisplayUtils {

    companion object {
        private val TAG = DisplayUtils::class.java.simpleName

        var displayWidth: Int = 1080
        var displayHeight: Int = 1800
        fun init(activity: Activity) {
            val display = activity.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            displayWidth = size.x
            displayHeight = size.y
            Log.d(TAG, "display: $displayWidth x $displayHeight")
        }
    }
}
