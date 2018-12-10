package com.seirion.code.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

fun generateBarCode(string: String, width: Int): Bitmap {
    try {
        val codeWriter = MultiFormatWriter()
        val width = width * 8 / 10 // 80%
        val height = width * 5 / 4 / 4
        return toBitmap(codeWriter.encode(string, BarcodeFormat.CODE_128, width, height))
    } catch (e: WriterException) {
        throw e
    }
}

fun toBitmap(matrix: BitMatrix): Bitmap {
    val height = matrix.height
    val width = matrix.width
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (x in 0 until width) {
        for (y in 0 until height) {
            val color = when (matrix.get(x, y)) {
                true -> Color.BLACK
                else -> Color.WHITE
            }
            bmp.setPixel(x, y, color)
        }
    }
    return bmp
}

fun codeTextPretty(str: String, separator: String = " - ") =
    if (str.length == 16) {
        "${str.substring(0, 4)}$separator${str.substring(4, 8)}$separator${str.substring(8, 12)}$separator${str.substring(12, 16)}"
    } else str
