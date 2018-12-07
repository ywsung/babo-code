package com.seirion.code.data

import android.content.Context
import com.seirion.code.db.CodeData
import com.seirion.code.db.CodeDataBase
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


fun addCodeData(context: Context, name: String, code: String) =
    Single.fromCallable { CodeDataBase.getInstance(context) }
        .doOnSuccess{ it.codeDataDao().insert(CodeData(name, code)) }
        .subscribeOn(Schedulers.io())!!

fun allCodeData(context: Context) =
    Single.fromCallable { CodeDataBase.getInstance(context) }
        .map { it.codeDataDao().getAll() }
        .subscribeOn(Schedulers.io())!!

fun deleteCodeData(context: Context, codeData: CodeData) =
    Single.fromCallable { CodeDataBase.getInstance(context) }
        .map { it.codeDataDao().delete(codeData) }
        .subscribeOn(Schedulers.io())!!
        .subscribe({}, {})!!
