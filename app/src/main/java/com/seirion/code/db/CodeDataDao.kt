package com.seirion.code.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface CodeDataDao {
    @Query("SELECT * FROM CodeData")
    fun getAll(): List<CodeData>

    @Insert(onConflict = REPLACE)
    fun insert(codeData: CodeData)

    @Delete
    fun delete(codeData: CodeData)

    @Query("DELETE from CodeData where id = :id")
    fun delete(id: Long)

    @Query("DELETE from CodeData")
    fun deleteAll()
}