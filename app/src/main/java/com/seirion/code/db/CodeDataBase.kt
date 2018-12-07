package com.seirion.code.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [CodeData::class], version = 1)
abstract class CodeDataBase : RoomDatabase() {
    abstract fun codeDataDao(): CodeDataDao

    companion object {
        private var INSTANCE: CodeDataBase? = null

        fun getInstance(context: Context): CodeDataBase {
            if (INSTANCE == null) {
                synchronized(CodeDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CodeDataBase::class.java, "CodeData.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}