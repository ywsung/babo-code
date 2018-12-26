package com.seirion.code.util

import android.graphics.Bitmap

object BitmapCache {
    private val cache = HashMap<String, Bitmap>()

    fun get(key: String): Bitmap {
        if (cache.contains(key)) return cache[key]!!
        return generateBarCode(key, DisplayUtils.displayWidth).also {
            cache[key] = it
        }
    }
}