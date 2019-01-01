package com.seirion.code.data

import android.content.Context
import com.seirion.code.db.CodeData
import com.seirion.code.db.CodeDataBase
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject


class DataManager {

    companion object {
        private val event = PublishSubject.create<Any>()

        fun addCodeData(context: Context, name: String, code: String) =
            Single.fromCallable { CodeDataBase.getInstance(context) }
                .doOnSuccess { it.codeDataDao().insert(CodeData(name, code)) }
                .doAfterSuccess { event.onNext(true) }
                .subscribeOn(Schedulers.io())!!

        fun updateCodeData(context: Context, codeData: CodeData) =
            Single.fromCallable { CodeDataBase.getInstance(context) }
                .doOnSuccess { it.codeDataDao().insert(codeData) }
                .doAfterSuccess { event.onNext(true) }
                .subscribeOn(Schedulers.io())!!

        fun allCodeData(context: Context) =
            Single.fromCallable { CodeDataBase.getInstance(context) }
                .map { it.codeDataDao().getAll() }
                .subscribeOn(Schedulers.io())!!

        fun deleteCodeData(context: Context, codeData: CodeData) =
            Single.fromCallable { CodeDataBase.getInstance(context) }
                .map { it.codeDataDao().delete(codeData) }
                .doAfterSuccess { event.onNext(true) }
                .subscribeOn(Schedulers.io())!!
                .subscribe({}, {})!!

        fun observeDataChange() = event
    }
}
