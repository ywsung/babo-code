package com.seirion.code.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface CodeDataDao {
    @Query("SELECT * FROM CodeData")
    fun getAll(): List<CodeData>

    @Insert(onConflict = REPLACE)
    fun insert(cat: CodeData)

    @Query("DELETE from CodeData")
    fun deleteAll()
}