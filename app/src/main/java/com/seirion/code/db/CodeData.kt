package com.seirion.code.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class CodeData(@ColumnInfo var name: String,
               @ColumnInfo var code: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
