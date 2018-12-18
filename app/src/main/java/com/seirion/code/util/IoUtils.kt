package com.seirion.code.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.seirion.code.db.CodeData
import java.util.*



class IoUtils {
    companion object {
        fun toJson(codeData: List<CodeData>): String {
            var gson = Gson()
            return gson.toJson(codeData)
        }

        fun fromJson(json: String): List<CodeData> = try {
            val gson = GsonBuilder().create()
            gson.fromJson(json, Array<CodeData>::class.java).toList()
        } catch (e: JsonSyntaxException) {
            Collections.emptyList()
        }
    }
}