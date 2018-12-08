package com.seirion.code.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.seirion.code.R

class SingleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        initUi()
    }

    private fun initUi() {
    }

    companion object {
        private val TAG = SingleActivity::class.java.simpleName

        fun start(activity: Activity) {
            Log.d(TAG, "$TAG.start()")
            val intent = Intent(activity, SingleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivity(intent)
        }
    }
}
