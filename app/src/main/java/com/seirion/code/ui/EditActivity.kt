package com.seirion.code.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.seirion.code.R
import com.seirion.code.data.DataManager
import com.seirion.code.db.CodeData
import com.seirion.code.util.BitmapCache
import com.seirion.code.util.codeTextPretty
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_edit.*


class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        initUi()
    }

    private fun initUi() {
        cardName.setText(codeData.name)
        code.text = codeTextPretty(codeData.code)
        codeImage.setImageBitmap(BitmapCache.get(codeData.code))
        ok.setOnClickListener { updateCodeData() }
    }

    @SuppressLint("CheckResult")
    private fun updateCodeData() {
        val name = cardName.text.toString().trim()
        Log.d(TAG, "updateCodeData: $name, ${codeData.code}")
        if (name.isEmpty() || codeData.name == name) return

        codeData.name = name
        DataManager.updateCodeData(this, codeData)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { finish() },
                { Log.e(TAG, "Failed to updateCodeData: $name") })
    }

    companion object {
        private val TAG = EditActivity::class.java.simpleName
        private lateinit var codeData: CodeData

        fun start(activity: Activity, codeData: CodeData) {
            Log.d(TAG, "$TAG.start()")
            EditActivity.codeData = codeData
            val intent = Intent(activity, EditActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivity(intent)
        }
    }
}
