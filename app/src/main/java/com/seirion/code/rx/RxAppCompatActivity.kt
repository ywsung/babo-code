package com.seirion.code.rx

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

open class RxAppCompatActivity : AppCompatActivity() {
    private val lifecycleSubject = BehaviorSubject.createDefault(ActivityLifecycle.CREATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentLifecycle(ActivityLifecycle.CREATE)
    }

    override fun onStart() {
        super.onStart()
        setCurrentLifecycle(ActivityLifecycle.START)
    }

    override fun onResume() {
        super.onResume()
        setCurrentLifecycle(ActivityLifecycle.RESUME)
    }

    override fun onPause() {
        super.onPause()
        setCurrentLifecycle(ActivityLifecycle.PAUSE)
    }

    override fun onStop() {
        super.onStop()
        setCurrentLifecycle(ActivityLifecycle.STOP)
    }

    override fun onDestroy() {
        setCurrentLifecycle(ActivityLifecycle.DESTROY)
        super.onDestroy()
    }

    private fun setCurrentLifecycle(lifecycle: ActivityLifecycle) {
        lifecycleSubject.onNext(lifecycle)
    }

    fun getLifecycleSignal(lifecycle: ActivityLifecycle): Observable<*> =
            lifecycleSubject.filter { it == lifecycle }.take(1)
}